package de.unigoettingen.sub.convert.impl;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;

import org.junit.Before;
import org.junit.Test;

public class EpubWriterTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws IOException {
		Book book = new Book();
		
		book.getMetadata().addTitle("Epublib test book 1");
		book.getMetadata().addAuthor(new Author("Joe", "Tester"));
		
		
		for (int i = 0; i < 5; i++) {
			book.addSection("Page " + i, new Resource(this.getClass().getResourceAsStream("/test.html"), "page" + i + ".html"));
			
			BufferedImage tif = ImageIO.read(new File(System.getProperty("user.dir") + "/src/test/resources/00000001.tif"));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(tif, "png", baos);
	//		ImageIO.write(tif, "png", new File("/tmp/1.png"));
			byte[] bytes = baos.toByteArray();
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
	
			book.getResources().add(new Resource(bais, "1.png"));
	//		book.getResources().add(new Resource(new FileInputStream("/tmp/1.png"), "1.png"));
		}

		
		EpubWriter epubWriter = new EpubWriter();
		
		epubWriter.write(book, new FileOutputStream("/tmp/test1_book1.epub"));
		 
		 
	}

}
