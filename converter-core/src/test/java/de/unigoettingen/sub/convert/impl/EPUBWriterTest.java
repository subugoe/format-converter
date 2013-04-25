package de.unigoettingen.sub.convert.impl;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;

import org.junit.Before;
import org.junit.Test;

import de.unigoettingen.sub.convert.api.ConvertWriter;
import de.unigoettingen.sub.convert.model.Page;
import static de.unigoettingen.sub.convert.model.builders.PageBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.MetadataBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.LanguageBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.WordBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.NonWordBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.TableBuilder.*;

public class EPUBWriterTest {

	private ConvertWriter writer;
	private ByteArrayOutputStream epubBaos;
	private Map<String, String> options;

	@Before
	public void setUp() throws Exception {
		writer = new EPUBWriter();
		epubBaos = new ByteArrayOutputStream();
		writer.setTarget(epubBaos);
		options = new HashMap<String, String>();
	}

	@Test
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
	
}
