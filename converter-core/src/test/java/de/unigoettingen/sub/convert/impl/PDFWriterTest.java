package de.unigoettingen.sub.convert.impl;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.pdf.PRTokeniser;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;

import de.unigoettingen.sub.convert.api.ConvertWriter;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.Page;
import static de.unigoettingen.sub.convert.model.builders.PageBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.TextBlockBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.MetadataBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.LanguageBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.WordBuilder.*;

public class PDFWriterTest {

	private static final int A4_HEIGHT = 842;
	private static final int A4_WIDTH = 595;

	private ConvertWriter writer;
	private ByteArrayOutputStream pdfBaos;
	
	@Before
	public void setUp() throws Exception {
		writer = new PDFWriter();
		pdfBaos = new ByteArrayOutputStream();
		writer.setTarget(pdfBaos);
	}

	@After
	public void tearDown() throws Exception {
		pdfBaos.close();
	}

	@Test
	public void addsCreatorInHeader() throws IOException {
		Metadata meta = metadata().withSoftwareName("Finereader").withSoftwareVersion("8.0").build();
		writeMetaToPdfBaos(meta);
		
		PdfReader reader = new PdfReader(pdfBaos.toByteArray());
		
		assertEquals("Creator", "Finereader 8.0", reader.getInfo().get("Creator"));
	}
	
	@Test
	public void addsLanguageInPdfCatalog() throws IOException {
		
		Metadata meta = metadata().with(language("German").withLangId("de")).build();
		writeMetaToPdfBaos(meta);
		
		PdfReader reader = new PdfReader(pdfBaos.toByteArray());

		assertEquals("language id", "de", reader.getCatalog().get(PdfName.LANG).toString());
	}
	
	private void writeMetaToPdfBaos(Metadata meta) {
		writer.writeStart();
		writer.writeMetadata(meta);
		writer.writePage(page().build());
		writer.writeEnd();
	}
	
	@Test
	public void addsOneEmptyPage() throws IOException {
		writeToPdfBaos(page().build());
		PdfReader reader = new PdfReader(pdfBaos.toByteArray());
		
		assertEquals("# of pages", 1, reader.getNumberOfPages());

	}
	
	@Test
	public void addsTwoEmptyPages() throws IOException {
		writeToPdfBaos(page().build(), page().build());
		PdfReader reader = new PdfReader(pdfBaos.toByteArray());
		
		assertEquals("# of pages", 2, reader.getNumberOfPages());

	}
	
	@Test
	public void writesWordWithCoordinates() throws IOException {
		
		//writer.setTarget(System.out);
		//writer.setTarget(new FileOutputStream("/tmp/test.pdf"));
		Page page = page().withHeight(A4_HEIGHT).withWidth(A4_WIDTH)
				.with(word("test").withCoordinatesLTRB(100,200,400,300))
				.build();
		writeToPdfBaos(page);
		
		PdfReader reader = new PdfReader(pdfBaos.toByteArray());
		String rawPdf = new String(reader.getPageContent(1));
		
		assertThat(rawPdf, containsString("100 542 Tm"));
		assertThat(rawPdf, containsString("(test)Tj"));
		
		assertEquals("text in pdf", "test", parsePdf(reader));
	}
	
	@Test
	public void pageScalingDoesntChangeTheOutputCoordinates() throws IOException {
		//writer.setTarget(new FileOutputStream("/tmp/test.pdf"));
		Page page = page().withHeight(A4_HEIGHT*2).withWidth(A4_WIDTH*2)
				.with(word("test").withCoordinatesLTRB(100*2,200*2,400*2,300*2))
				.build();
		writeToPdfBaos(page);
		
		PdfReader reader = new PdfReader(pdfBaos.toByteArray());
		String rawPdf = new String(reader.getPageContent(1));
		
		assertThat(rawPdf, containsString("100 542 Tm"));
		assertThat(rawPdf, containsString("(test)Tj"));
	}
	
	@Test
	public void wordWithoutCoordinates() {
		Page page = page().with(word("test")).build();
		writeToPdfBaos(page);
	}
	
	@Test
	public void testWithFile() throws FileNotFoundException {
	}
	
	private void writeToPdfBaos(Page... pages) {
		writer.writeStart();
		writer.writeMetadata(metadata().build());
		for (Page page : pages) {
			writer.writePage(page);
		}
		writer.writeEnd();
		
	}
	
    private String parsePdf(PdfReader reader) throws IOException {
		
        byte[] streamBytes = reader.getPageContent(1);
        PRTokeniser tokenizer = new PRTokeniser(new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(streamBytes)));
        StringBuilder sb = new StringBuilder();
        while (tokenizer.nextToken()) {
            if (tokenizer.getTokenType() == PRTokeniser.TokenType.STRING) {
                sb.append(tokenizer.getStringValue());
            }
        }
        reader.close();
        return sb.toString();
    }

}
