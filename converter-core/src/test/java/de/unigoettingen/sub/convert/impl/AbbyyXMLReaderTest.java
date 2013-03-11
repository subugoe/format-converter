package de.unigoettingen.sub.convert.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import de.unigoettingen.sub.convert.api.ConvertReader;
import de.unigoettingen.sub.convert.api.ConvertWriter;
import de.unigoettingen.sub.convert.model.Char;
import de.unigoettingen.sub.convert.model.Line;
import de.unigoettingen.sub.convert.model.LineItem;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.Page;
import de.unigoettingen.sub.convert.model.Paragraph;
import de.unigoettingen.sub.convert.model.TextBlock;
import de.unigoettingen.sub.convert.model.Word;

public class AbbyyXMLReaderTest {

	private ConvertReader reader;
	private ConvertWriter writerMock;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		reader = new AbbyyXMLReader();
		writerMock = mock(ConvertWriter.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void shouldNotWorkWithoutWriter() {
		InputStream is = mock(InputStream.class);
		try {
			reader.read(is);
			fail("did not throw IllegalStateException");
		} catch (IllegalStateException e) {
			assertEquals("The Writer is not set", e.getMessage());
		}
	}
	
	@Test
	public void shouldNotWorkWithTextFile() throws FileNotFoundException {
		reader.setWriter(writerMock);
		try {
			reader.read(fromFile("some_file.txt"));
			fail("did not throw exception");
		} catch (IllegalArgumentException e) {
			assertEquals("Error reading XML", e.getMessage());
		}
	}
	
	@Test
	public void shouldNotWorkWithWrongXMLFormat() throws FileNotFoundException {
		reader.setWriter(writerMock);
		try {
			reader.read(fromFile("some_file.xml"));
			fail("did not throw exception");
		} catch (IllegalArgumentException e) {
			assertEquals("Error reading XML", e.getMessage());
		}
	}
	
	@Test
	public void writeStartAndMetadataOneTime() throws FileNotFoundException {
		reader.setWriter(writerMock);
		reader.read(fromFile("abbyy6_metadata.xml"));
		
		verify(writerMock, times(1)).writeStart();
		verify(writerMock, times(1)).writeMetadata(any(Metadata.class));
	}
	
	@Test
	public void metadataObjectShouldContainInfos() throws FileNotFoundException {
		ArgumentCaptor<Metadata> argument = ArgumentCaptor.forClass(Metadata.class);
		reader.setWriter(writerMock);
		reader.read(fromFile("abbyy6_metadata.xml"));
		
		verify(writerMock).writeMetadata(argument.capture());

		Metadata meta = argument.getValue();
		assertEquals("FineReader 8.0", meta.getOcrSoftwareName());
		assertTrue(meta.getLanguages().contains("GermanStandard"));
	}
	
	@Test
	public void emptyPageWithHeightAndWidth() throws FileNotFoundException {
		ArgumentCaptor<Page> argument = ArgumentCaptor.forClass(Page.class);
		reader.setWriter(writerMock);
		reader.read(fromFile("abbyy6_metadata.xml"));
		
		verify(writerMock, times(1)).writePage(argument.capture());
		
		Page page = argument.getValue();
		assertEquals(new Integer(5675), page.getHeight());
		assertEquals(new Integer(3603), page.getWidth());
	}
	
	@Test
	public void shouldWriteTwoPages() throws FileNotFoundException {
		reader.setWriter(writerMock);
		reader.read(fromFile("abbyy6_twoPages.xml"));
		
		verify(writerMock, times(2)).writePage(any(Page.class));
	}
	
	@Test
	public void pageShouldContainCertainValues() throws FileNotFoundException {
		ArgumentCaptor<Page> argument = ArgumentCaptor.forClass(Page.class);
		reader.setWriter(writerMock);
		reader.read(fromFile("abbyy6_withoutCharParams.xml"));
		verify(writerMock).writePage(argument.capture());
		
		Page page = argument.getValue();
		assertNotNull(page.getPageItems());
		TextBlock block = (TextBlock)page.getPageItems().get(0);
		assertSomeValuesAreCorrect(block);
	}
	
	@Test
	public void wordsAndCharsShouldHaveCoordinates() throws FileNotFoundException {
		ArgumentCaptor<Page> argument = ArgumentCaptor.forClass(Page.class);
		reader.setWriter(writerMock);
		reader.read(fromFile("abbyy6.xml"));
		verify(writerMock).writePage(argument.capture());
		
		Page page = argument.getValue();
		TextBlock block = (TextBlock) page.getPageItems().get(0);
		Line line = block.getParagraphs().get(0).getLines().get(0);
		assertCoordinatesArePresent(line);
		
	}
	
	@Test
	public void emptyFormattingElementShouldBeIgnored() throws FileNotFoundException {
		reader.setWriter(writerMock);
		reader.read(fromFile("abbyy10_emptyFormatting.xml"));
		// used to throw a NullPointerException
	}
	
	private InputStream fromFile(String file) throws FileNotFoundException {
		File dir = new File(System.getProperty("user.dir") + "/src/test/resources/");
		return new FileInputStream(new File(dir, file));
	}

	private void assertSomeValuesAreCorrect(TextBlock block) {
		assertEquals(new Integer(604), block.getLeft());
		assertEquals(2, block.getParagraphs().size());
		
		Paragraph par = block.getParagraphs().get(0);
		assertEquals(1, par.getLines().size());
		Line line1 = par.getLines().get(0);
		assertEquals(new Integer(1032), line1.getTop());
		
		assertEquals(6, line1.getLineItems().size());
		
		String expectedString = "Some test text.";
		StringBuilder strB = new StringBuilder();
		for (LineItem item : line1.getLineItems()) {
			assertNull(item.getRight());
			for (Char ch : item.getCharacters()) {
				strB.append(ch.getValue());
			}
		}
		assertEquals(expectedString, strB.toString());
	}
	
	private void assertCoordinatesArePresent(Line line) {
		Word firstWord = (Word)line.getLineItems().get(0);
		assertEquals(new Integer(1220), firstWord.getLeft());
		assertEquals(new Integer(1036), firstWord.getTop());
		assertEquals(new Integer(2744), firstWord.getRight());
		assertEquals(new Integer(1248), firstWord.getBottom());
		
		Char firstChar = firstWord.getCharacters().get(0);
		assertEquals(new Integer(1220), firstChar.getLeft());
		assertEquals(new Integer(1036), firstChar.getTop());
		assertEquals(new Integer(1464), firstChar.getRight());
		assertEquals(new Integer(1244), firstChar.getBottom());
	}
}
