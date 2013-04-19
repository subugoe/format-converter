package de.unigoettingen.sub.convert.impl;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.pdf.PRTokeniser;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;

import de.unigoettingen.sub.convert.api.ConvertWriter;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.Page;
import static de.unigoettingen.sub.convert.model.builders.PageBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.MetadataBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.LanguageBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.WordBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.NonWordBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.TableBuilder.*;

public class PDFWriterTest {

	private static final float A4_HEIGHT = 842f;
	private static final float A4_WIDTH = 595f;

	private ConvertWriter writer;
	private ByteArrayOutputStream pdfBaos;
	private Map<String, String> options;

	@Before
	public void setUp() throws Exception {
		writer = new PDFWriter();
		pdfBaos = new ByteArrayOutputStream();
		writer.setTarget(pdfBaos);
		options = new HashMap<String, String>();
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
	public void usesA4AsDefaultPageSizeIfNotSetInModelPage() throws IOException {
		writeToPdfBaos(page().build());
		PdfReader reader = new PdfReader(pdfBaos.toByteArray());
		
		Rectangle actualSize = reader.getPageSize(1);
		
		assertEquals("default page width", A4_WIDTH, actualSize.getWidth(), 0.1f);
		assertEquals("default page height", A4_HEIGHT, actualSize.getHeight(), 0.1f);
	}
	
	@Test
	public void usesA4AsDefaultEvenIfOriginalIsDifferent() throws IOException {
		Page page = page().withWidth(1).withHeight(2).build();

		writeToPdfBaos(page);
		PdfReader reader = new PdfReader(pdfBaos.toByteArray());
		
		Rectangle actualSize = reader.getPageSize(1);
		
		assertEquals("page width", A4_WIDTH, actualSize.getWidth(), 0.1f);
		assertEquals("page height", A4_HEIGHT, actualSize.getHeight(), 0.1f);
		
	}
	
	@Test
	public void usesTheOriginalPageSizeIfSetInOptions() throws IOException {
		Page page = page().withWidth(1).withHeight(2).build();
		options.put("pagesize", "original");
		writer.setImplementationSpecificOptions(options);
		writeToPdfBaos(page);
		PdfReader reader = new PdfReader(pdfBaos.toByteArray());
		
		Rectangle actualSize = reader.getPageSize(1);
		
		assertEquals("original page width", 1f, actualSize.getWidth(), 0.1f);
		assertEquals("original page height", 2f, actualSize.getHeight(), 0.1f);
	}
	
	@Test
	public void usesA4IfSetInOptions() throws IOException {
		Page page = page().withWidth(1).withHeight(2).build();
		options.put("pagesize", "A4");
		writer.setImplementationSpecificOptions(options);
		writeToPdfBaos(page);
		PdfReader reader = new PdfReader(pdfBaos.toByteArray());
		
		Rectangle actualSize = reader.getPageSize(1);
		
		assertEquals("page width", A4_WIDTH, actualSize.getWidth(), 0.1f);
		assertEquals("page height", A4_HEIGHT, actualSize.getHeight(), 0.1f);
		
	}

	@Test
	public void writesWordWithCoordinates() throws IOException {
		Page page = pageA4()
				.with(word("test").withCoordinatesLTRB(100,200,400,300))
				.build();
		writeToPdfBaos(page);
		String rawPdf = readFromPdfBaos();
		
		assertThat(rawPdf, containsString("100 542 Tm"));
		assertThat(rawPdf, containsString("(test)Tj"));
	}
	
	@Test
	public void writesTwoWords() throws IOException {
		Page page = pageA4()
				.with(word("test").withCoordinatesLTRB(100,200,400,300))
				.with(word("test2").withCoordinatesLTRB(100,300,400,400))
				.build();
		writeToPdfBaos(page);
		String rawPdf = readFromPdfBaos();
		
		assertThat(rawPdf, containsString("100 542 Tm"));
		assertThat(rawPdf, containsString("(test)Tj"));
		assertThat(rawPdf, containsString("100 442 Tm"));
		assertThat(rawPdf, containsString("(test2)Tj"));
	}
	
	@Test
	public void pageScalingDoesntChangeTheOutputCoordinates() throws IOException {
		Page page = page().withHeight((int)A4_HEIGHT*2).withWidth((int)A4_WIDTH*2)
				.with(word("test").withCoordinatesLTRB(100*2,200*2,400*2,300*2))
				.build();
		writeToPdfBaos(page);
		String rawPdf = readFromPdfBaos();
		
		assertThat(rawPdf, containsString("100 542 Tm"));
		assertThat(rawPdf, containsString("(test)Tj"));
	}
	
	@Test
	public void writesNonWordOnPage() throws IOException {
		Page page = pageA4()
				.with(nonWord("!??").withCoordinatesLTRB(100,200,400,300))
				.build();
		writeToPdfBaos(page);
		String rawPdf = readFromPdfBaos();
		
		assertThat(rawPdf, containsString("100 542 Tm"));
		assertThat(rawPdf, containsString("(!??)Tj"));
	}
	
	@Test
	public void wordWithoutCoordinatesIsIgnored() throws IOException {
		Page page = pageA4().with(word("test")).build();
		writeToPdfBaos(page);
		String rawPdf = readFromPdfBaos();

		assertThat(rawPdf, not(containsString("test")));
	}
	
	@Test
	public void canHandleTable() throws IOException {
		Page page = pageA4().with(table().with(
				word("test").withCoordinatesLTRB(100, 200, 400, 300)
				)).build();
		writeToPdfBaos(page);
		String rawPdf = readFromPdfBaos();

		assertThat(rawPdf, containsString("100 542 Tm"));
		assertThat(rawPdf, containsString("(test)"));
	}
		
	@Test
	public void doesNotPutImageBehindText() throws IOException {
		Page page = pageA4().build();
		writeToPdfBaos(page);
		String rawPdf = readFromPdfBaos();
		
		assertThat(rawPdf, not(containsString("/img0")));
	}
	
	@Test
	public void putsImageBehindTextIfOptionIsSet() throws IOException {
		Page page = pageA4().build();
		options.put("images", System.getProperty("user.dir") + "/src/test/resources/withOneImage");
		writer.setImplementationSpecificOptions(options);
		writeToPdfBaos(page);
		String rawPdf = readFromPdfBaos();
		
		assertThat(rawPdf, containsString("/img0"));
		
		String textRenderInvisible = "3 Tr";
		assertThat("text rendering should be invisible", rawPdf, containsString(textRenderInvisible));
	}
	
	@Test
	public void puts2ImagesBehind2Pages() throws IOException {
		//writer.setTarget(new FileOutputStream("/tmp/test.pdf"));
		Page page = pageA4().build();
		options.put("images", "src/test/resources/withTwoImages");
		writer.setImplementationSpecificOptions(options);
		writeToPdfBaos(page, page);
		String rawPdf = readFromPdfBaosPages(1, 2);
		
		assertThat(rawPdf, containsString("/img0"));
		assertThat(rawPdf, containsString("/img1"));
	}
	
	@Test
	public void throwsExceptionWhenTooFewImages() throws IOException {
		Page page = pageA4().build();
		options.put("images", "src/test/resources/withOneImage");
		writer.setImplementationSpecificOptions(options);
		try {
			writeToPdfBaos(page, page);
			fail("did not throw exception");
		} catch (IllegalStateException e) {
			assertEquals("error message", "No image found for page 2", e.getMessage());
		}
	}
	
	@Test
	public void throwsExceptionWhenWrongFolder() throws IOException {
		Page page = pageA4().build();
		options.put("images", "src/test/resources/xyz");
		writer.setImplementationSpecificOptions(options);
		try {
			writeToPdfBaos(page, page);
			fail("did not throw exception");
		} catch (IllegalStateException e) {
			assertEquals("error message", "Not a folder: src/test/resources/xyz", e.getMessage());
		}
	}
	
	
	
	private String readFromPdfBaos() throws IOException {
		return readFromPdfBaosPages(1);
	}
	
	private String readFromPdfBaosPages(int... pages) throws IOException {
		PdfReader reader = new PdfReader(pdfBaos.toByteArray());
		StringBuilder allPages = new StringBuilder();
		for (int page : pages) {
			allPages.append(new String(reader.getPageContent(page)));
		}
		
		return allPages.toString();
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
