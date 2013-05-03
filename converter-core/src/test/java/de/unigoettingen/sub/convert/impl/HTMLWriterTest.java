package de.unigoettingen.sub.convert.impl;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unigoettingen.sub.convert.api.ConvertWriter;
import de.unigoettingen.sub.convert.model.Page;
import static org.hamcrest.CoreMatchers.*;
import static de.unigoettingen.sub.convert.model.builders.PageBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.WordBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.LineBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.TableBuilder.*;

public class HTMLWriterTest {

	private ConvertWriter writer;
	private ByteArrayOutputStream htmlBaos;

	@Before
	public void setUp() throws Exception {
		writer = new HTMLWriter();
		htmlBaos = new ByteArrayOutputStream();
		writer.setTarget(htmlBaos);
	}

	@After
	public void tearDown() throws Exception {
		htmlBaos.close();
	}

	@Test
	public void writesHtmlWithEmptyBody() {
		writer.writeStart();
		writer.writeEnd();
		
		String html = fromBaos();
		
		assertThat(html, containsString("<html>"));
		assertThat(html, containsString("<body></body>"));
		assertThat(html, containsString("</html>"));
	}
	
	@Test
	public void writesHtmlWithOneWord() {
		Page page = page().with(word("test")).build();
		writer.writePage(page);
		
		String html = fromBaos();
		
		assertThat(html, containsString("test"));
	}
	
	@Test
	public void writesHtmlWithEmptyLine() {
		Page page = page().with(line()).build();
		writer.writePage(page);
		
		String html = fromBaos();
		
		assertThat(html, containsString("<br/>"));
	}
	
	@Test
	public void createsHtmlWithImage() throws FileNotFoundException {
		Page page = page().with(word("word")).build();
		
		writer.addImplementationSpecificOption("images", "src/test/resources/withOneImage");
		writer.addImplementationSpecificOption("imagesoutdir", "target/test.html.images");
		writer.writePage(page);
		
		String html = fromBaos();
		
		assertThat(html, containsString("word"));
		assertThat(html, containsString("<img src=\"test.html.images/image1.png\""));
	}

	@Test
	public void createsHtmlWithTwoImages() throws FileNotFoundException {
		Page page = page().with(word("word1")).build();
		Page page2 = page().with(word("word2")).build();
		
		writer.addImplementationSpecificOption("images", "src/test/resources/withTwoImages");
		writer.addImplementationSpecificOption("imagesoutdir", "target/test2.html.images");
		writer.writePage(page);
		writer.writePage(page2);
		
		String html = fromBaos();
		
		assertThat(html, containsString("<img src=\"test2.html.images/image1.png\""));
		assertThat(html, containsString("<img src=\"test2.html.images/image2.png\""));
	}
	
	@Test(expected=IllegalStateException.class)
	public void exceptionWhenTooFewImages() throws FileNotFoundException {
		Page page = page().with(word("word1")).build();
		Page page2 = page().with(word("word2")).build();
		
		String oneImageDir = "src/test/resources/withOneImage";
		writer.addImplementationSpecificOption("images", oneImageDir);
		writer.addImplementationSpecificOption("imagesoutdir", "target/test3.html.images");
		writer.writePage(page);

		writer.writePage(page2);		
	}
	
	@Test
	public void createsHtmlWithTable() throws FileNotFoundException {
		Page page = page().with(table().with(word("word1"))).build();
//		writer.setTarget(new FileOutputStream("target/bla.html"));
		
		writer.writePage(page);
		
		String html = fromBaos();
		
		assertThat(html, containsString("<table>"));
		assertThat(html, containsString("<tr>"));
		assertThat(html, containsString("<td>"));
		assertThat(html, containsString("word1"));
	}
	


	private String fromBaos() {
		return new String(htmlBaos.toByteArray());
	}

}
