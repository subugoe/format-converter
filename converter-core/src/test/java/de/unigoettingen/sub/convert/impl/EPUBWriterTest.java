package de.unigoettingen.sub.convert.impl;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.epub.EpubWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unigoettingen.sub.convert.api.ConvertWriter;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.Page;
import static de.unigoettingen.sub.convert.model.builders.PageBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.MetadataBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.LanguageBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.WordBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.NonWordBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.TableBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.LineBuilder.*;

public class EPUBWriterTest {

	private ConvertWriter writer;
	private ByteArrayOutputStream epubBaos;

	@Before
	public void setUp() throws Exception {
		writer = new EPUBWriter();
		epubBaos = new ByteArrayOutputStream();
		writer.setTarget(epubBaos);
	}
	
	@After
	public void tearDown() throws Exception {
		epubBaos.close();
	}

	@Test
	public void writesEmptyEpub() throws IOException {
		writer.writeStart();
		writer.writeEnd();
		Book book = writtenBook();
		
		assertEquals("# of reachable book resources", 0, book.getContents().size());
	}
	
	@Test
	public void writesLanguageId() throws IOException {
		Metadata meta = metadata().with(language("German").withLangId("de")).build();
		
		writeToEpubBook(meta);
		Book book = writtenBook();
		
		assertEquals("language", "de", book.getMetadata().getLanguage());
	}
	
	@Test
	public void languageDefaultsToEnglish() throws IOException {
		Metadata emptyMeta = metadata().build();
		
		writeToEpubBook(emptyMeta);
		Book book = writtenBook();
		
		assertEquals("language", "en", book.getMetadata().getLanguage());
	}
	
	private void writeToEpubBook(Metadata meta) {
		writer.writeStart();
		writer.writeMetadata(meta);
		writer.writeEnd();
	}
	
	@Test
	public void writesOneEmptyPage() throws IOException {
		Page page = page().build();
		writeToEpubBook(page);
		
		Book book = writtenBook();
		
		assertEquals("# of reachable book resources", 1, book.getContents().size());

		Resource firstPage = book.getContents().get(0);
		String rawHtml = toHtml(firstPage);
		
		assertEquals("media type", "application/xhtml+xml", firstPage.getMediaType().toString());
		assertEquals("encoding", "UTF-8", firstPage.getInputEncoding());
		assertThat(rawHtml, containsString("<div id=\"page1\""));
	}
	
	@Test
	public void writesTwoEmptyPages() throws IOException {
		Page page = page().build();
		writeToEpubBook(page, page);
		
		Book book = writtenBook();
		assertEquals("# of reachable book resources", 2, book.getContents().size());

		Resource firstPage = book.getContents().get(0);
		String rawHtml = toHtml(firstPage);
		assertThat(rawHtml, containsString("<div id=\"page1\""));
		
		Resource secondPage = book.getContents().get(0);
		String rawHtml2 = toHtml(secondPage);
		assertThat(rawHtml2, containsString("<div id=\"page1\""));
	}
	
	@Test
	public void writesPageWithText() throws IOException {
		Page page = page().with(word("test")).build();
		writeToEpubBook(page);
		
		String rawHtml = firstHtmlPage();
		assertThat(rawHtml, containsString("test"));
	}
	
	@Test
	public void writesPageWithPuncuations() throws IOException {
		Page page = page().with(nonWord("!!?")).build();
		writeToEpubBook(page);
		
		String rawHtml = firstHtmlPage();
		assertThat(rawHtml, containsString("!!?"));
	}
	
	@Test
	public void writesPageWithUmlauts() throws IOException {
		Page page = page().with(word("üß")).build();
		writeToEpubBook(page);
		
		String rawHtml = firstHtmlPage();
		assertThat(rawHtml, containsString("üß"));
	}
	
	@Test
	public void writesPageWithEmptyLine() throws IOException {
//		writer.setTarget(new FileOutputStream("/tmp/out.epub"));
		Page page = page().with(line()).build();
		writeToEpubBook(page);
		
		String rawHtml = firstHtmlPage();
		assertThat(rawHtml, containsString("<br/>"));
	}
	
	
	
	private String firstHtmlPage() throws IOException {
		Book book = writtenBook();
		Resource firstPage = book.getContents().get(0);
		return toHtml(firstPage);
	}

	private void writeToEpubBook(Page... pages) {
		writer.writeStart();
		for(Page page : pages) {
			writer.writePage(page);
		}
		writer.writeEnd();
	}
	
	private Book writtenBook() throws IOException {
		EpubReader reader = new EpubReader();
		
		byte[] bytes = epubBaos.toByteArray();
		ByteArrayInputStream epubIn = new ByteArrayInputStream(bytes);
		
		return reader.readEpub(epubIn);
	}	

	private String toHtml(Resource htmlPage) throws IOException {
		return new String(htmlPage.getData());
	}

	//@Test
	public void test() throws IOException {
		
		Page page = page().with(word("test")).build();
		
		writer.setTarget(new FileOutputStream("/tmp/book.epub"));
		
		writer.writeStart();
		writer.writePage(page);
		writer.writeEnd();
		
		Book book = new Book();
		
		book.getMetadata().addTitle("Epublib test book 1");
		book.getMetadata().addAuthor(new Author("Joe", "Tester"));
		
		System.out.println(Arrays.toString(ImageIO.getWriterFormatNames()));
		for (int i = 0; i < 5; i++) {
			book.addSection("Page " + i, new Resource(this.getClass().getResourceAsStream("/test.html"), "page" + i + ".html"));
			
			BufferedImage tif = ImageIO.read(new File(System.getProperty("user.dir") + "/src/test/resources/00000004.tif"));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
	//		ImageIO.write(tif, "png", baos);
//			byte[] bytes = baos.toByteArray();
//			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
	
	//		book.getResources().add(new Resource(bais, "1.png"));

			
			ImageIO.write(tif, "png", new File("/tmp/04.png"));
			book.getResources().add(new Resource(new FileInputStream("/tmp/04.png"), "04.png"));
		}

		
		EpubWriter epubWriter = new EpubWriter();
		
		epubWriter.write(book, new FileOutputStream("/tmp/test1_book1.epub"));
		 
	}
	
	//@Test
	public void tiffs() throws IOException {
		Book book = new Book();
		
		book.getMetadata().addTitle("Epublib test book 1");
		book.getMetadata().addAuthor(new Author("Joe", "Tester"));
		
		for (int i = 0; i < 5; i++) {
			book.addSection("Page " + i, new Resource(this.getClass().getResourceAsStream("/test.html"), "page" + i + ".html"));
			
			
			book.getResources().add(new Resource(this.getClass().getResourceAsStream("/00000004.tif"), "04.tif"));
		}

		
		EpubWriter epubWriter = new EpubWriter();
		
		epubWriter.write(book, new FileOutputStream("/tmp/test1_book1.epub"));

	}
	
}
