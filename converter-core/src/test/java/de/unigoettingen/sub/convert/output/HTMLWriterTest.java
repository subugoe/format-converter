package de.unigoettingen.sub.convert.output;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unigoettingen.sub.convert.api.ConvertWriter;
import de.unigoettingen.sub.convert.model.Page;
import de.unigoettingen.sub.convert.output.HTMLWriter;
import static org.hamcrest.CoreMatchers.*;
import static de.unigoettingen.sub.convert.model.builders.PageBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.WordBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.LineBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.TableBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.ImageBuilder.*;

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
	public void writesHtmlWithTwoPages() {
		Page page = page().with(line()).build();
		writer.writePage(page);
		writer.writePage(page);
		
		String html = fromBaos();
		
		assertThat(html, containsString("id=\"page1\""));
		assertThat(html, containsString("id=\"page2\""));
	}
		
	@Test
	public void createsHtmlWithImage() throws FileNotFoundException {
		Page page = page().withHeight(3655).with(word("word")).build();
		
		writer.addImplementationSpecificOption("scans", "src/test/resources/withOneImage");
		writer.addImplementationSpecificOption("imagesoutdir", "target/test.html.images");
		writer.writePage(page);
		
		String html = fromBaos();
		
		assertThat(html, containsString("word"));
		assertThat(html, containsString("<img src=\"test.html.images/scan1.png\""));
	}

	@Test
	public void createsHtmlAndScanImageInSameDir() {
		Page page = page().withHeight(3655).build();
		
		writer.addImplementationSpecificOption("scans", "src/test/resources/withOneImage");
		writer.addImplementationSpecificOption("imagesoutdir", "target/test_onedir.html.images");
		writer.addImplementationSpecificOption("onedir", "true");
		writer.writePage(page);
		
		String html = fromBaos();
		
		assertThat(html, containsString("<img src=\"scan1.png\""));
	}


	@Test
	public void createsHtmlWithTwoImages() throws FileNotFoundException {
		Page page = page().withHeight(3655).with(word("word1")).build();
		Page page2 = page().withHeight(3655).with(word("word2")).build();
		
		writer.addImplementationSpecificOption("scans", "src/test/resources/withTwoImages");
		writer.addImplementationSpecificOption("imagesoutdir", "target/test2.html.images");
		writer.writePage(page);
		writer.writePage(page2);
		
		String html = fromBaos();
		
		assertThat(html, containsString("<img src=\"test2.html.images/scan1.png\""));
		assertThat(html, containsString("<img src=\"test2.html.images/scan2.png\""));
	}
	
	@Test(expected=IllegalStateException.class)
	public void exceptionWhenTooFewImages() throws FileNotFoundException {
		Page page = page().withHeight(3655).with(word("word1")).build();
		Page page2 = page().withHeight(3655).with(word("word2")).build();
		
		String oneImageDir = "src/test/resources/withOneImage";
		writer.addImplementationSpecificOption("scans", oneImageDir);
		writer.addImplementationSpecificOption("imagesoutdir", "target/test3.html.images");
		writer.writePage(page);

		writer.writePage(page2);		
	}
	
	@Test
	public void createsHtmlWithTable() throws FileNotFoundException {
		Page page = page().with(table().with(word("word1"))).build();
		
		writer.writePage(page);
		
		String html = fromBaos();
		
		assertThat(html, containsString("<table>"));
		assertThat(html, containsString("<tr>"));
		assertThat(html, containsString("<td>"));
		assertThat(html, containsString("word1"));
	}
	
	@Test
	public void createsHtmlWithSubimageAndScan() throws FileNotFoundException {
		Page page = page().withHeight(3655).with(image().withCoordinatesLTRB(956, 2112, 1464, 2744)).build();
		File resultImage = new File("target/test_subimage.html.images/subimage1-1.png");
		resultImage.delete();
		
		String oneImageDir = "src/test/resources/withOneImage";
		writer.addImplementationSpecificOption("scans", oneImageDir);
		writer.addImplementationSpecificOption("imagesoutdir", "target/test_subimage.html.images");
		writer.writePage(page);
		
		String html = fromBaos();
		
		assertThat(html, containsString("<img src=\"test_subimage.html.images/scan1.png\""));
		assertThat(html, containsString("<img src=\"test_subimage.html.images/subimage1-1.png\""));
		assertTrue("image must be present", resultImage.exists());
		
	}
	
	@Test
	public void createsHtmlWithoutScan() throws FileNotFoundException {
		Page page = page().withHeight(3655).with(image().withCoordinatesLTRB(956, 2112, 1464, 2744)).build();
		
		String oneImageDir = "src/test/resources/withOneImage";
		writer.addImplementationSpecificOption("scans", oneImageDir);
		writer.addImplementationSpecificOption("includescans", "false");
		writer.addImplementationSpecificOption("imagesoutdir", "target/test_subimageNoScan.html.images");
		writer.writePage(page);
		
		String html = fromBaos();
		
		assertThat(html, not(containsString("<img src=\"test_subimageNoScan.html.images/scan1.png\"")));
		assertThat(html, containsString("<img src=\"test_subimageNoScan.html.images/subimage1-1.png\""));
	}
	
	@Test
	public void createsHtmlWithTwoSubimages() throws FileNotFoundException {
		Page page = page().withHeight(3655)
				.with(image().withCoordinatesLTRB(956, 2112, 1464, 2744))
				.with(image().withCoordinatesLTRB(956, 2400, 1464, 2744))
				.build();
		//writer.setTarget(new FileOutputStream("target/bla.html"));
		
		String oneImageDir = "src/test/resources/withOneImage";
		writer.addImplementationSpecificOption("scans", oneImageDir);
		writer.addImplementationSpecificOption("imagesoutdir", "target/test_2subimages.html.images");
		writer.writePage(page);
		
		String html = fromBaos();
		
		assertThat(html, containsString("<img src=\"test_2subimages.html.images/subimage1-1.png\""));
		assertThat(html, containsString("<img src=\"test_2subimages.html.images/subimage1-2.png\""));
		
	}
	


	private String fromBaos() {
		return new String(htmlBaos.toByteArray());
	}

}
