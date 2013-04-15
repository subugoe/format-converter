package de.unigoettingen.sub.convert.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
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
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.TiffImage;

import de.unigoettingen.sub.convert.api.ConvertWriter;
import de.unigoettingen.sub.convert.model.Language;
import de.unigoettingen.sub.convert.model.Line;
import de.unigoettingen.sub.convert.model.LineItem;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.Page;
import de.unigoettingen.sub.convert.model.PageItem;
import de.unigoettingen.sub.convert.model.TextBlock;
import de.unigoettingen.sub.convert.model.Paragraph;
import de.unigoettingen.sub.convert.model.Char;

public class PDFWriter implements ConvertWriter {

	private final static Logger LOGGER = LoggerFactory.getLogger(PDFWriter.class);
	private OutputStream output;
	private Document pdfDocument;
	private PdfWriter pwriter;
	private Page currentPage;
	private int pageNumber = 0;
	
	@Override
	public void writeStart() {
		pdfDocument = new Document(PageSize.A4, 0, 0, 0, 0);
		try {
			pwriter = PdfWriter.getInstance(pdfDocument, output);
			//pwriter.setCompressionLevel(0); //TODO: remove this?
			pdfDocument.open();
			
		} catch (DocumentException e) {
			LOGGER.error("Error while writing start of document.", e);
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
		if (currentPage.getWidth() == null || currentPage.getHeight() == null) {
			int pdfPageWidth = (int)pdfDocument.getPageSize().getWidth();
			int pdfPageHeight = (int)pdfDocument.getPageSize().getHeight();
			currentPage.setWidth(pdfPageWidth);
			currentPage.setWidth(pdfPageHeight);
			LOGGER.warn("Page size is not set in input document. Setting to defaults. Width: "
					+ pdfPageWidth + ", height: " + pdfPageHeight);
		}
		try {
			//pdfDocument.setPageSize(new Rectangle(page.getWidth().floatValue(), page.getHeight().floatValue()));
			pdfDocument.newPage();
			pwriter.setPageEmpty(false);
			List<Line> lines = allLinesFromPage(page);
			
			if (lines.isEmpty()) {
				return;
			}
			
			PdfContentByte pdfPage = pwriter.getDirectContent();
			
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
			
			putImageOnPage();
			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			pwriter.flush();
		}

	}

	private void putImageOnPage() throws DocumentException, FileNotFoundException, IOException {
		File imageFile = new File(
				System.getProperty("user.dir") + "/src/test/resources/00000001.tif");
		RandomAccessFileOrArray ra = new RandomAccessFileOrArray(new FileInputStream(imageFile));
		Image image = TiffImage.getTiffImage(ra, 1);
		
		image.scalePercent(pdfSize(100));
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

		//cb.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_INVISIBLE);
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
				for (Paragraph par : block.getParagraphs()) {
					for (Line line : par.getLines()) {
						lines.add(line);
					}
				}
			}
		}
		return lines;
	}

	@Override
	public void writeEnd() {
		pdfDocument.close();

	}

	@Override
	public void setTarget(OutputStream stream) {
		output = stream;

	}

}
