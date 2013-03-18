package de.unigoettingen.sub.convert.impl;

import static org.junit.Assert.*;

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

public class TeiP5WriterTest {

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
		writer = new TeiP5Writer();
		baos = new ByteArrayOutputStream();
	}

	@After
	public void tearDown() throws Exception {
		baos.close();
	}

	private String processPage(Page page) {
		writer.setTarget(baos);
		writer.writePage(page);
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

		assertTrue(output.contains("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		assertTrue(output.contains("<TEI xmlns=\"http://www.tei-c.org/ns/1.0\""));
	}
	
	@Test
	public void emptyMetadataShouldLeadToEmptyTEIHeaderPlusSomeStartTags() {
		writer.setTarget(baos);
		writer.writeMetadata(new Metadata());
		String output = baos.toString();

		assertTrue(output.contains("<teiHeader></teiHeader>"));
		assertTrue(output.contains("<text>"));
		assertTrue(output.contains("<body"));
	}
	
	@Test
	public void outputShouldContainCorrectMetadata() {
		writer.setTarget(baos);
		Metadata meta = ModelObjectFactory.createSimpleMetadata();
		writer.writeMetadata(meta);
		String output = baos.toString();
		
		assertTrue(output.contains("<creation>Finereader 8.0</creation>"));
		assertTrue(output.contains("<language>GermanStandard</language>"));
	}
		
	@Test
	public void emptyPageShouldResultInAPageBreak() {
		Page page = new Page();
		String output = processPage(page);
		
		assertTrue(output.contains("<milestone n=\"1\" type=\"page\"/>"));
		assertTrue(output.contains("<pb"));
	}
	
	@Test
	public void secondPageShouldCreateSecondMilestone() {
		writer.setTarget(baos);
		Page page = new Page();
		writer.writePage(page);
		writer.writePage(page);
		
		String output = baos.toString();
		assertTrue(output.contains("<milestone n=\"2\""));
	}

	@Test
	public void paragraphShouldGetAnID() {
		Page page = ModelObjectFactory.createPageWithOneParagraph();
		String output = processPage(page);

		assertTrue(output.contains("<p id=\"ID1\"></p>"));
	}
	
	@Test
	public void paragraphIDShouldBeIncremented() {
		Page page = ModelObjectFactory.createPageWithTwoParagraphs();
		String output = processPage(page);

		assertTrue(output.contains("<p id=\"ID2\"></p>"));
	}
	
	@Test
	public void addLineBreakAfterALine() {
		Page page = ModelObjectFactory.createPageWithOneLine();
		String output = processPage(page);

		assertTrue(output.contains("<lb/>"));
	}
	
	@Test
	public void shouldWrapWordInTags() {
		Page page = ModelObjectFactory.createPageWithOneWord("test");
		String output = processPage(page);

		assertTrue(output.contains("<w>test</w>"));
	}
	
	@Test
	public void shouldNotWrapNonWordInTags() {
		Page page = ModelObjectFactory.createPageWithOneNonWord("...");
		String output = processPage(page);
		
		assertFalse(output.contains("<w>...</w>"));
		assertTrue(output.contains("..."));
	}
	
	@Test
	public void shouldAddWordCoordinates() {
		Page page = ModelObjectFactory.createPageWithOneWordAndCoordinates("test");
		String output = processPage(page);

		assertTrue(output.contains("<w function=\"1,2,3,4\">test</w>"));

	}
	
	@Test
	public void shouldCreateTable() {
		Page page = ModelObjectFactory.createPageWithTable();
		String output = processPage(page);
		
		assertTrue(output.contains("<table>"));
		assertTrue(output.contains("<row>"));
		assertTrue(output.contains("<cell>"));
		assertTrue(output.contains("<w>a</w>"));
	}
}
