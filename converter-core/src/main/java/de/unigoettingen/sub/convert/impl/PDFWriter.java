package de.unigoettingen.sub.convert.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.io.RandomAccessSource;
import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.TiffImage;

import de.unigoettingen.sub.convert.api.ConvertWriter;
import de.unigoettingen.sub.convert.api.WriterWithOptions;
import de.unigoettingen.sub.convert.model.Cell;
import de.unigoettingen.sub.convert.model.Char;
import de.unigoettingen.sub.convert.model.Language;
import de.unigoettingen.sub.convert.model.Line;
import de.unigoettingen.sub.convert.model.LineItem;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.Page;
import de.unigoettingen.sub.convert.model.PageItem;
import de.unigoettingen.sub.convert.model.Paragraph;
import de.unigoettingen.sub.convert.model.Row;
import de.unigoettingen.sub.convert.model.Table;
import de.unigoettingen.sub.convert.model.TextBlock;
import de.unigoettingen.sub.convert.util.ResourceHandler;

public class PDFWriter extends WriterWithOptions implements ConvertWriter {

	private final static Logger LOGGER = LoggerFactory.getLogger(PDFWriter.class);
	private Document pdfDocument;
	private PdfWriter pwriter;
	private Page currentPage;
	private int pageNumber = 0;
	private static final String FOLDER_WITH_IMAGES_DESCRIPTION = "[folder] (containing original Tiff images)";
	private static final String PAGESIZE_DESCRIPTION = "[A4 or original], default is A4";
	
	private ResourceHandler resourceHandler = new ResourceHandler();
	
	public PDFWriter() {
		supportedOptions.put("scans", FOLDER_WITH_IMAGES_DESCRIPTION);
		supportedOptions.put("pagesize", PAGESIZE_DESCRIPTION);
	}
	
	@Override
	public void writeStart() {
		pdfDocument = new Document(PageSize.A4, 0, 0, 0, 0);
		try {
			pwriter = PdfWriter.getInstance(pdfDocument, output);
			pdfDocument.open();
			
		} catch (DocumentException e) {
			throw new IllegalStateException("Error while writing start of document.", e);
		}

	}

	@Override
	public void writeMetadata(Metadata meta) {
		
		String swName = meta.getOcrSoftwareName();
		String swVersion = meta.getOcrSoftwareVersion();
		String creator = swName != null ? swName : "";
		creator = creator + (swVersion != null ? " "+swVersion : "");
		pdfDocument.addCreator(creator);

		List<Language> langs = meta.getLanguages();
		if (!langs.isEmpty()) {
			String langId = langs.get(0).getLangId();
			if (langId != null) {
				pdfDocument.addLanguage(langId);
			}
		}
		
		pwriter.flush();
		
	}

	@Override
	public void writePage(Page page) {
		pageNumber++;
		currentPage = page;
		try {
			setPageSize();
			pdfDocument.newPage();
			pwriter.setPageEmpty(false);
			
			PdfContentByte pdfPage = pwriter.getDirectContent();
			if (imagesAvailable()) {
				pdfPage.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_INVISIBLE);
				putBackgroundImageOnPage();
			}

			List<Line> lines = allLinesFromPage(page);
			if (lines.isEmpty()) {
				return;
			}
			pdfPage.beginText();
			for (Line line : lines) {
				for (LineItem word : line.getLineItems()) {
					if (hasAllCoordinates(word)) {
						putWordOnPage(pdfPage, word, line);
					} else {
						LOGGER.warn("Word will be ignored, because it has no coordinates: '" + stringValue(word) + "', on page " + pageNumber+ ".");
					}
				}
			}
			pdfPage.endText();
			
		} catch (DocumentException e) {
			throw new IllegalStateException("Error while writing page, number " + pageNumber, e);
		} catch (IOException e) {
			throw new IllegalStateException("Error while writing page, number " + pageNumber, e);
		} finally {
			pwriter.flush();
		}

	}

	private boolean imagesAvailable() {
		return setOptions.get("scans") != null;
	}

	private void setPageSize() {
		if (currentPage.getWidth() == null || currentPage.getHeight() == null) {
			int pdfPageWidth = (int)pdfDocument.getPageSize().getWidth();
			int pdfPageHeight = (int)pdfDocument.getPageSize().getHeight();
			currentPage.setWidth(pdfPageWidth);
			currentPage.setHeight(pdfPageHeight);
			LOGGER.warn("Page size is not set in input document. Setting to defaults. Width: "
					+ pdfPageWidth + ", height: " + pdfPageHeight);
			return;
		}
		boolean keepOririnalPageSize = "original".equals(setOptions.get("pagesize"));
		if (keepOririnalPageSize) {
			pdfDocument.setPageSize(new Rectangle(currentPage.getWidth().floatValue(), currentPage.getHeight().floatValue()));
		}
	}
	
	private void putBackgroundImageOnPage() throws DocumentException, FileNotFoundException, IOException {
		File imagesFolder = new File(setOptions.get("scans"));
		File imageFile = resourceHandler.getImageForPage(pageNumber, imagesFolder);
		
		RandomAccessSource source = new RandomAccessSourceFactory().createSource(new FileInputStream(imageFile));
		RandomAccessFileOrArray ra = new RandomAccessFileOrArray(source);
		Image image = TiffImage.getTiffImage(ra, 1);
		
		float pdfWidth = pdfDocument.getPageSize().getWidth();
		image.scalePercent(pdfWidth / image.getWidth() * 100f);
		image.setAlignment(Image.LEFT);
		pdfDocument.add(image);
	}

	private void putWordOnPage(PdfContentByte pdfPage, LineItem word,
			Line line) throws DocumentException, IOException {
		Integer left = word.getLeft();
		Integer top = computeTop(line);
		Integer right = word.getRight();
		Integer bottom = computeBottom(line, word);
		
		Integer wordHeight = bottom - top;
		Integer wordWidth = right - left; 
		
		float pdfWordHeight = pdfSize(wordHeight);
		float pdfWordWidth = pdfSize(wordWidth);
		float pdfDistanceFromPageTopToWordBottom = pdfSize(bottom);
		float pdfPageHeight = pdfDocument.getPageSize().getHeight();
		
		float leftOnPdfPage = pdfSize(left);
		// in pdf, coordinates start from left bottom corner
		float bottomOnPdfPage = pdfPageHeight - pdfDistanceFromPageTopToWordBottom;

		BaseFont font = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

		String wordString = stringValue(word);
		float fontSize = pdfWordHeight;
		float widthCorrectionForFont = font.getWidthPoint(wordString, fontSize);
		pdfPage.setHorizontalScaling(pdfWordWidth/widthCorrectionForFont * 100f);
		pdfPage.setFontAndSize(font, fontSize);
		
		float baselineCorrection = 0f;
		Integer baseline = line.getBaseline();
		if (baseline != null) {
			baselineCorrection = pdfSize(bottom - baseline);
		}
		pdfPage.setTextMatrix(leftOnPdfPage, bottomOnPdfPage + baselineCorrection);

		pdfPage.showText(wordString);
	}

	private float pdfSize(Integer originalSize) {
		float widthRelation = pdfDocument.getPageSize().getWidth() / currentPage.getWidth();
		return originalSize.floatValue() * widthRelation;
	}

	private Integer computeBottom(Line line, LineItem currentItem) {
		Integer lineBottom = line.getBottom();
		if (lineBottom != null) {
			return lineBottom;
		} else {
			return currentItem.getBottom();
		}
	}

	private Integer computeTop(Line line) {
		if (line.getTop() != null) {
			return line.getTop();
		}
		List<Integer> wordTops = new ArrayList<Integer>();
		for (LineItem item : line.getLineItems()) {
			wordTops.add(item.getTop());
		}
		return Collections.min(wordTops);
	}

	private boolean hasAllCoordinates(LineItem item) {
		if (item.getLeft() != null && item.getTop() != null && item.getRight() != null && item.getBottom() != null) {
			return true;
		}
		return false;
	}

	private String stringValue(LineItem item) {
		StringBuilder sb = new StringBuilder();
		for (Char ch : item.getCharacters()) {
			sb.append(ch.getValue());
		}
		return sb.toString();
	}

	private List<Line> allLinesFromPage(Page page) {
		List<Line> lines = new ArrayList<Line>();
		for (PageItem item : page.getPageItems()) {
			if (item instanceof TextBlock) {
				TextBlock block = (TextBlock) item;
				addLinesFromTextBlock(lines, block);
			} else if (item instanceof Table) {
				Table table = (Table) item;
				for (Row row : table.getRows()) {
					for (Cell cell : row.getCells()) {
						PageItem cellContent = cell.getContent();
						if (cellContent instanceof TextBlock) {
							TextBlock tableBlock = (TextBlock) cellContent;
							addLinesFromTextBlock(lines, tableBlock);
						}
					}
				}
			}
		}
		return lines;
	}

	private void addLinesFromTextBlock(List<Line> lines, TextBlock block) {
		for (Paragraph par : block.getParagraphs()) {
			for (Line line : par.getLines()) {
				lines.add(line);
			}
		}
	}

	@Override
	public void writeEnd() {
		pdfDocument.close();
	}

}
