package de.unigoettingen.sub.convert.impl;

import java.io.IOException;
import java.io.OutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPage;
import com.itextpdf.text.pdf.PdfWriter;

import de.unigoettingen.sub.convert.api.ConvertWriter;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.Page;

public class PDFWriter implements ConvertWriter {

	private OutputStream output;
	private Document pdfDocument;
	private PdfWriter pwriter;
	
	@Override
	public void writeStart() {
		pdfDocument = new Document();
		try {
			pwriter = PdfWriter.getInstance(pdfDocument, output);
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
		creator = creator + (swVersion != null ? swVersion : "");
		pdfDocument.addCreator(creator);

		pdfDocument.addLanguage("de");
		pdfDocument.addLanguage("bla");
		
		pwriter.flush();
		
	}

	@Override
	public void writePage(Page page) {
		try {
			pdfDocument.add(new Paragraph("Hello World!"));
			pdfDocument.newPage();
			
			pwriter.flush();
			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
