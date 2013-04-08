package de.unigoettingen.sub.convert.impl;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unigoettingen.sub.convert.api.ConvertWriter;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.Page;
import de.unigoettingen.sub.convert.model.builders.LanguageBuilder;
import static de.unigoettingen.sub.convert.model.builders.MetadataBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.LanguageBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.PageBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.ParagraphBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.LineBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.WordBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.NonWordBuilder.*;

public class TeiP4WriterTest {

	private ConvertWriter writer;
	private OutputStream baos;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		writer = new TeiP4Writer();
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
	public void shouldWriteXmlHeaderAndTEIStartElement() {
		writer.setTarget(baos);
		writer.writeStart();
		
		String output = baos.toString();

		assertThat(output, containsString("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		assertThat(output, containsString("<TEI.2"));
	}
	
	@Test
	public void emptyMetadataShouldLeadToEmptyTEIHeaderPlusSomeStartTags() {
		Metadata meta = metadata().build();
		String output = process(meta);

		assertThat(output, containsString("<teiHeader></teiHeader>"));
		assertThat(output, containsString("<text>"));
		assertThat(output, containsString("<body"));
	}
	
	@Test
	public void outputShouldContainLanguageAndItsValidId() {
		Metadata meta = metadata().with(language().withLangId("de").withValue("GermanStandard")).build();
		String output = process(meta);
		
		assertThat(output, containsString("<language ident=\"de\">GermanStandard</language>"));
	}
		
	@Test
	public void outputShouldContainInvalidLanguageWithoutId() {
		Metadata meta = metadata().with(language().withValue("SomeUnknownLanguage")).build();
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
		LanguageBuilder l1 = language().withValue("lang1");
		LanguageBuilder l2 = language().withValue("lang2");
		Metadata meta = metadata().with(l1).with(l2).build();
		String output = process(meta);
		
		assertThat(output, containsString("<language>lang1</language>"));
		assertThat(output, containsString("<language>lang2</language>"));
	}

	@Test
	public void emptyPageShouldResultInAPageBreak() {
		Page page = new Page();
		String output = process(page);
		
		assertTrue(output.contains("<milestone n=\"1\" type=\"page\"/>"));
		assertThat(output, containsString("<pb"));
	}
	
	@Test
	public void secondPageShouldCreateSecondMilestone() {
		writer.setTarget(baos);
		Page page = new Page();
		writer.writePage(page);
		writer.writePage(page);
		
		String output = baos.toString();
		assertThat(output, containsString("<milestone n=\"2\""));
	}
	
	@Test
	public void paragraphShouldGetAnID() {
		Page page = page().with(paragraph()).build();
		String output = process(page);

		assertThat(output, containsString("<p id=\"ID1\"></p>"));
	}
	
	@Test
	public void paragraphIDShouldBeIncremented() {
		Page page = page().with(paragraph()).with(paragraph()).build();
		String output = process(page);

		assertThat(output, containsString("<p id=\"ID2\"></p>"));
	}
	
	@Test
	public void addLineBreakAfterALine() {
		Page page = page().with(line()).build();
		String output = process(page);

		assertThat(output, containsString("<lb/>"));
	}
	
	@Test
	public void shouldWrapWordInTags() {
		Page page = page().with(word("test")).build();
		String output = process(page);

		assertThat(output, containsString("<w>test</w>"));
	}
	
	@Test
	public void shouldNotWrapNonWordInTags() {
		Page page = page().with(nonWord("...")).build();
		String output = process(page);
		
		assertThat(output, not(containsString("<w>...</w>")));
		assertThat(output, containsString("..."));
	}
	
	@Test
	public void shouldAddWordCoordinates() {
		Page page = page().with(word("test").withCoordinatesLTRB(1, 2, 3, 4)).build();
		String output = process(page);

		assertThat(output, containsString("<w function=\"1,2,3,4\">test</w>"));

	}
	
	@Test
	public void shouldCreateTableWithCoordinates() {
		Page page = ModelObjectFactory.createPageWithTable();
		String output = process(page);
		
		assertThat(output, containsString("<table"));
		assertThat(output, containsString("function=\"1,2,3,4\""));
		assertThat(output, containsString("rows=\"1\""));
		assertThat(output, containsString("cols=\"1\""));
		assertThat(output, containsString("<row>"));
		assertThat(output, containsString("<cell>"));
		assertThat(output, containsString("<w>a</w>"));
	}
	
	@Test
	public void shouldWriteFigure() {
		Page page = ModelObjectFactory.createPageWithImage();
		String output = process(page);

		assertThat(output, containsString("<figure"));
		assertThat(output, containsString("id=\"ID1\""));
		assertThat(output, containsString("function=\"1,2,3,4\""));
	}
}
