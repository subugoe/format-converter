package de.unigoettingen.sub.convert.impl;

import static de.unigoettingen.sub.convert.model.builders.LanguageBuilder.language;
import static de.unigoettingen.sub.convert.model.builders.MetadataBuilder.metadata;
import static de.unigoettingen.sub.convert.model.builders.PageBuilder.page;
import static de.unigoettingen.sub.convert.model.builders.ParagraphBuilder.paragraph;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unigoettingen.sub.convert.api.ConvertWriter;
import de.unigoettingen.sub.convert.impl.xslt.XsltWriter;
import de.unigoettingen.sub.convert.model.Document;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.Page;

public class XsltWriterTest {

	private ConvertWriter writer;
	private OutputStream baos;

	@Before
	public void setUp() throws Exception {
		writer = new XsltWriter();
		writer.addImplementationSpecificOption("xslt", "src/test/resources/xslt/toTei.xsl");
		baos = new ByteArrayOutputStream();
	}

	@After
	public void tearDown() throws Exception {
		baos.close();
	}

	private String process(Page page) {
		writer.setTarget(baos);
		writer.writePage(page);
		return baos.toString();
	}

	private String process(Metadata meta) {
		writer.setTarget(baos);
		writer.writeMetadata(meta);
		return baos.toString();
	}

	@Test
	public void shouldNotWorkWithoutOutput() {
		try {
			writer.writeStart();
			fail("did not throw exception");
		} catch (IllegalStateException e) {
			assertEquals("The output target is not set", e.getMessage());
		}
	}

	@Test
	public void shouldWriteXmlHeaderAndTeiStartElement() {
		writer.setTarget(baos);
		writer.writeStart();
		
		String output = baos.toString();
		
		assertThat(output, containsString("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		assertThat(output, containsString("<TEI"));
	}

	@Test
	public void emptyMetadataShouldLeadToEmptyTeiHeader() {
		Metadata meta = metadata().build();
		String output = process(meta);

		assertThat(output, containsString("<teiHeader>"));
		assertThat(output, containsString("</teiHeader>"));
	}

	@Test
	public void outputShouldContainLanguageAndItsValidId() {
		Metadata meta = metadata().with(language("GermanStandard").withLangId("de")).build();
		String output = process(meta);
		
		assertThat(output, containsString("<language ident=\"de\">GermanStandard</language>"));
	}
	
	@Test
	public void outputShouldContainInvalidLanguageWithoutId() {
		Metadata meta = metadata().with(language("SomeUnknownLanguage")).build();
		String output = process(meta);
		
		assertThat(output, containsString("<language>SomeUnknownLanguage</language>"));
	}

	@Test
	public void outputShouldContainCreatorInfos() {
		Metadata meta = metadata().withSoftwareName("Finereader").withSoftwareVersion("8.0").build();
		String output = process(meta);
		
		assertThat(output, containsString("<creation>Finereader 8.0</creation>"));
	}

	@Test
	public void outputShouldContainTwoLanguages() {
		Metadata meta = metadata().with(language("lang1")).with(language("lang2")).build();
		String output = process(meta);
		//System.out.println(output);

		writer.writeEnd();
		assertThat(output, containsString("<language>lang1</language>"));
		assertThat(output, containsString("<language>lang2</language>"));
	}

	@Test
	public void emptyPageShouldResultInAPageBreak() {
		Page page = new Page();
		String output = process(page);

		assertThat(output, containsString("<milestone n=\"\" type=\"page\"/>"));
		assertThat(output, containsString("<pb"));
	}

	@Test
	public void pageWithPhysicalNumber() {
		Page page = new Page();
		page.setPhysicalNumber(1);
		String output = process(page);

		assertThat(output, containsString("<milestone n=\"1\" type=\"page\"/>"));
		assertThat(output, containsString("<pb"));
	}

	@Test
	public void documentWithOnePage() {
		writer.setTarget(baos);
		writer.writeStart();
		writer.writePage(new Page());
		writer.writeEnd();
		String output = baos.toString();
		assertThat(output, containsString("<TEI"));
		assertThat(output, containsString("<text><body>\n<milestone"));
		assertThat(output, containsString("</body></text></TEI>"));
	}
	
	@Test
	public void completeDocumentWithMetaAndPage() {
		writer.setTarget(baos);
		writer.writeStart();
		writer.writeMetadata(new Metadata());
		writer.writePage(new Page());
		writer.writeEnd();
		String output = baos.toString();
		assertThat(output, containsString("<TEI"));
		assertThat(output, containsString("</teiHeader>\n<text><body>"));
		assertThat(output, containsString("</body></text></TEI>"));
	}
	
	@Test
	public void paragraphShouldGetAnID() {
		Page page = page().with(paragraph()).build();
		String output = process(page);

		assertThat(output, containsString("<p id=\"ID1_1\">"));
	}

	@Test
	public void paragraphIDShouldBeIncremented() {
		Page page = page().with(paragraph()).with(paragraph()).build();
		String output = process(page);

		assertThat(output, containsString("<p id=\"ID1_2\">"));
	}

}
