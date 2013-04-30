package de.unigoettingen.sub.convert.impl;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unigoettingen.sub.convert.api.ConvertWriter;
import de.unigoettingen.sub.convert.model.Page;
import static org.hamcrest.CoreMatchers.*;
import static de.unigoettingen.sub.convert.model.builders.PageBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.WordBuilder.*;

public class HTMLWriterTest {

	private ConvertWriter writer;
	private ByteArrayOutputStream htmlBaos;
	private Map<String, String> options;

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
	public void createsHtmlWithImage() {
		Page page = page().with(word("test")).build();
		writer.writePage(page);
		writer.addImplementationSpecificOption("images", "src/test/resources/withOneImage");
		writer.addImplementationSpecificOption("imagesoutdir", "src/test/resources/withOneImage");
		
		String html = fromBaos();
		
		assertThat(html, containsString("test"));
		
	}

	private String fromBaos() {
		return new String(htmlBaos.toByteArray());
	}

}
