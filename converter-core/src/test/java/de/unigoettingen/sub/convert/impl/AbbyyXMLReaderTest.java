package de.unigoettingen.sub.convert.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import static org.hamcrest.CoreMatchers.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import de.unigoettingen.sub.convert.api.ConvertReader;
import de.unigoettingen.sub.convert.api.ConvertWriter;
import de.unigoettingen.sub.convert.impl.abbyyxml.AbbyyXMLReader;
import de.unigoettingen.sub.convert.model.Cell;
import de.unigoettingen.sub.convert.model.Char;
import de.unigoettingen.sub.convert.model.FontStyleEnum;
import de.unigoettingen.sub.convert.model.Image;
import de.unigoettingen.sub.convert.model.Language;
import de.unigoettingen.sub.convert.model.Line;
import de.unigoettingen.sub.convert.model.LineItem;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.NonWord;
import de.unigoettingen.sub.convert.model.Page;
import de.unigoettingen.sub.convert.model.PageItem;
import de.unigoettingen.sub.convert.model.Paragraph;
import de.unigoettingen.sub.convert.model.Row;
import de.unigoettingen.sub.convert.model.Table;
import de.unigoettingen.sub.convert.model.TextBlock;
import de.unigoettingen.sub.convert.model.WithCoordinates;
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
		Metadata meta = metadataFromFile("abbyy6_metadata.xml");

		assertEquals("FineReader 8.0", meta.getOcrSoftwareName());
		assertEquals("same languages should become one", 1, meta.getLanguages().size());
		
		Language language = meta.getLanguages().get(0);
		assertEquals("de", language.getLangId());
		assertEquals("GermanStandard", language.getValue());
	}
	
	@Test
	public void metadataWithUnknownLanguage() throws FileNotFoundException {
		Metadata meta = metadataFromFile("abbyy6_meta_unknownLanguage.xml");
		
		Language language = meta.getLanguages().get(0);
		assertNull(language.getLangId());
		assertEquals("SomeUnknownLanguage", language.getValue());
	}

	@Test
	public void metadataWithTwoLanguages() throws FileNotFoundException {
		Metadata meta = metadataFromFile("abbyy6_meta_twoLanguages.xml");
		
		assertEquals("# of languages", 2, meta.getLanguages().size());
		Language language0 = meta.getLanguages().get(0);
		Language language1 = meta.getLanguages().get(1);
		
		ArrayList<String> languageIds = new ArrayList<String>();
		languageIds.add(language0.getValue());
		languageIds.add(language1.getValue());
		
		assertThat(languageIds, hasItems("GermanStandard", "EnglishUnitedStates"));
	}

	private Metadata metadataFromFile(String file) throws FileNotFoundException {
		ArgumentCaptor<Metadata> argument = ArgumentCaptor.forClass(Metadata.class);
		reader.setWriter(writerMock);
		reader.read(fromFile(file));
		
		verify(writerMock).writeMetadata(argument.capture());

		Metadata meta = argument.getValue();
		return meta;
	}
	
	@Test
	public void emptyPageWithHeightAndWidthAndNumber() throws FileNotFoundException {
		ArgumentCaptor<Page> argument = ArgumentCaptor.forClass(Page.class);
		reader.setWriter(writerMock);
		reader.read(fromFile("abbyy6_metadata.xml"));
		
		verify(writerMock, times(1)).writePage(argument.capture());
		
		Page page = argument.getValue();
		assertEquals(new Integer(5675), page.getHeight());
		assertEquals(new Integer(3603), page.getWidth());
		assertEquals(new Integer(1), page.getPhysicalNumber());
	}
	
	@Test
	public void shouldWriteTwoPages() throws FileNotFoundException {
		reader.setWriter(writerMock);
		reader.read(fromFile("abbyy6_twoPages.xml"));
		
		verify(writerMock, times(2)).writePage(any(Page.class));
	}
		
	@Test
	public void pageShouldContainCertainValues() throws FileNotFoundException {		
		Page page = firstPageFromFile("abbyy6_withoutCharParams.xml");
		assertNotNull(page.getPageItems());
		TextBlock block = (TextBlock)page.getPageItems().get(0);
		assertSomeValuesAreCorrect(block);
	}
	
	@Test
	public void linesWordsAndCharsShouldHaveCoordinates() throws FileNotFoundException {
		Page page = firstPageFromFile("abbyy6.xml");
		
		Line line = firstLineOnPage(page);
		assertCoordinatesArePresent(line);
		
		LineItem firstWord = line.getLineItems().get(0);
		assertCoordinatesArePresent(firstWord);
		
		Char firstChar = firstWord.getCharacters().get(0);
		assertCharCoordinates(firstChar);
	}
	
	@Test
	public void digitsShouldBecomeWords() throws FileNotFoundException {
		Page page = firstPageFromFile("abbyy6_withDigits.xml");
		
		Line line = firstLineOnPage(page);
		LineItem item = line.getLineItems().get(0);
		assertThat(item, instanceOf(Word.class));
		assertEquals("# of digits", 4, item.getCharacters().size());
	}

	private Line firstLineOnPage(Page page) {
		TextBlock block = (TextBlock) page.getPageItems().get(0);
		Line line = block.getParagraphs().get(0).getLines().get(0);
		return line;
	}
	
	@Test
	public void emptyFormattingElementShouldBeIgnored() throws FileNotFoundException {
		reader.setWriter(writerMock);
		reader.read(fromFile("abbyy10_emptyFormatting.xml"));
		// used to throw a NullPointerException
	}
	
	@Test
	public void tableShouldContainCoordinatesAndATextBlock () throws FileNotFoundException {
		Page page = firstPageFromFile("abbyy10_withTable.xml");
		Table table = (Table) page.getPageItems().get(0);
		
		assertCoordinatesArePresent(table);
		assertEquals("number of rows", 2, table.getRows().size());
		
		Row row = table.getRows().get(0);
		assertEquals("number of cells", 5, row.getCells().size());
		
		Cell cell = row.getCells().get(0);
		assertThat(cell.getContent(), instanceOf(TextBlock.class));
		
	}
	
	@Test
	public void shouldReadPictureWithCoordinates() throws FileNotFoundException {
		Page page = firstPageFromFile("abbyy10_withPicture.xml");
		PageItem item = page.getPageItems().get(0);
		
		assertThat("object type", item, instanceOf(Image.class));
		assertCoordinatesArePresent(item);
	}
	
	@Test
	public void wordShouldContainLanguageAndFontInfos() throws FileNotFoundException {
		Page page = firstPageFromFile("abbyy6.xml");
		Line line = firstLineOnPage(page);
		LineItem item = line.getLineItems().get(0);
		
		Word word = (Word) item;
		assertEquals("ISO language", "de", word.getLanguage());
		assertEquals("font type", "Times New Roman", word.getFont());
		assertEquals("font size", "38.", word.getFontSize());
		assertEquals("font color", "123", word.getFontColor());
		assertThat(word.getFontStyles(), hasItem(FontStyleEnum.BOLD));
		assertThat(word.getFontStyles(), hasItem(FontStyleEnum.ITALIC));
		assertThat(word.getFontStyles(), hasItem(FontStyleEnum.UNDERLINE));
	}

	@Test
	public void nonWordShouldContainFontInfos() throws FileNotFoundException {
		Page page = firstPageFromFile("abbyy6.xml");
		Line line = firstLineOnPage(page);
		LineItem item = line.getLineItems().get(1);
		
		NonWord word = (NonWord) item;
		assertEquals("font type", "Times New Roman", word.getFont());
		assertEquals("font size", "38.", word.getFontSize());
		assertEquals("font color", "123", word.getFontColor());
		assertThat(word.getFontStyles(), hasItem(FontStyleEnum.BOLD));
		assertThat(word.getFontStyles(), hasItem(FontStyleEnum.ITALIC));
		assertThat(word.getFontStyles(), hasItem(FontStyleEnum.UNDERLINE));
	}
	
	@Test
	public void specialAbbyyBlockTypesShouldBeIgnored() throws FileNotFoundException {
		Page page = firstPageFromFile("abbyy10_specialBlockTypes.xml");
		
		assertEquals("page items", 0, page.getPageItems().size());
	}
	
	@Test
	public void readsBaseline() throws FileNotFoundException {
		Page page = firstPageFromFile("abbyy6.xml");
		Line line = firstLineFromPage(page);
		
		assertEquals("Baseline", new Integer(1248), line.getBaseline());
	}

	private void assertCoordinatesArePresent(WithCoordinates modelItem) {
		assertThat("left coordinate", modelItem.getLeft(), instanceOf(Integer.class));
		assertThat("top coordinate", modelItem.getTop(), instanceOf(Integer.class));
		assertThat("right coordinate", modelItem.getRight(), instanceOf(Integer.class));
		assertThat("bottom coordinate", modelItem.getBottom(), instanceOf(Integer.class));
	}
	private void assertCharCoordinates(Char ch) {
		assertThat("left coordinate", ch.getLeft(), instanceOf(Integer.class));
		assertThat("top coordinate", ch.getTop(), instanceOf(Integer.class));
		assertThat("right coordinate", ch.getRight(), instanceOf(Integer.class));
		assertThat("bottom coordinate", ch.getBottom(), instanceOf(Integer.class));
	}
	
	private Line firstLineFromPage(Page page) {
		TextBlock block = (TextBlock)page.getPageItems().get(0);
		Paragraph par = block.getParagraphs().get(0);
		return par.getLines().get(0);
	}
	
	private Page firstPageFromFile(String file) throws FileNotFoundException {
		ArgumentCaptor<Page> argument = ArgumentCaptor.forClass(Page.class);
		reader.setWriter(writerMock);
		reader.read(fromFile(file));
		verify(writerMock).writePage(argument.capture());
		return argument.getValue();
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
	
}
