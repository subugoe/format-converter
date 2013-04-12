package de.unigoettingen.sub.convert.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPage;
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
import de.unigoettingen.sub.convert.model.Word;
import de.unigoettingen.sub.convert.model.Paragraph;
import de.unigoettingen.sub.convert.model.Char;

public class PDFWriter implements ConvertWriter {

	private OutputStream output;
	private Document pdfDocument;
	private PdfWriter pwriter;
	
	@Override
	public void writeStart() {
		pdfDocument = new Document(PageSize.A4, 0, 0, 0, 0);
		try {
			pwriter = PdfWriter.getInstance(pdfDocument, output);
			//pwriter.setCompressionLevel(0); //TODO: remove this?
			pdfDocument.open();
			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		try {
			//pdfDocument.setPageSize(new Rectangle(page.getWidth().floatValue(), page.getHeight().floatValue()));
			pdfDocument.newPage();
			pwriter.setPageEmpty(false);
			List<LineItem> lineItems = allLineItemsFromPage(page);
			
			if (lineItems.isEmpty()) {
				return;
			}
			
			PdfContentByte cb = pwriter.getDirectContent();
			BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

			
			float divWidth = pdfDocument.getPageSize().getWidth() / page.getWidth();
			cb.beginText();
			for (LineItem item : lineItems) {
				
				if (!hasAllCoordinates(item)) {
					//TODO: logger
					continue;
				}
				
				String word = stringValue(item);
				
				float left = (float) item.getLeft() * divWidth;
				float top = (float) item.getTop() * divWidth;
				
				Integer wordH = new Integer(item.getBottom() - item.getTop());
				Integer wordW = new Integer(item.getRight() - item.getLeft()); 
				
				float height = wordH.floatValue() * divWidth;
				float width = wordW.floatValue() * divWidth;
				
				float bottom = pdfDocument.getPageSize().getHeight() - top - height;
				float strWidth = bf.getWidthPoint(word, height);
				float descent = bf.getDescentPoint(word, height);
				cb.setHorizontalScaling(width/strWidth * 80f);
				cb.setFontAndSize(bf, height);
				cb.setTextMatrix(left, bottom - descent);

				//cb.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_INVISIBLE);
				cb.showText(word);


			}
			cb.endText();
			
//			File imageFile = new File(
//					System.getProperty("user.dir") + "/src/test/resources/00000004.tif");
//			RandomAccessFileOrArray ra = new RandomAccessFileOrArray(new FileInputStream(imageFile));
//			Image image = TiffImage.getTiffImage(ra, 1);
//			
//			image.scalePercent(divWidth * 100f);
//			image.setAlignment(Image.LEFT);
//			pdfDocument.add(image);
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

	private List<LineItem> allLineItemsFromPage(Page page) {
		List<LineItem> lineItems = new ArrayList<LineItem>();
		for (PageItem item : page.getPageItems()) {
			if (item instanceof TextBlock) {
				TextBlock block = (TextBlock) item;
				for (Paragraph par : block.getParagraphs()) {
					for (Line line : par.getLines()) {
						for (LineItem lineItem : line.getLineItems()) {
							lineItems.add(lineItem);
						}
					}
				}
			}
		}
		
		return lineItems;
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
