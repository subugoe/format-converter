package de.unigoettingen.sub.convert.integrationtests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;

import de.unigoettingen.sub.convert.api.ConvertReader;
import de.unigoettingen.sub.convert.api.ConvertWriter;
import de.unigoettingen.sub.convert.input.abbyyxml.AbbyyXMLReader;
import de.unigoettingen.sub.convert.output.HTMLWriter;

public class AbbyyToHtmlTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws IOException {
		File abbyy = new File(
				System.getProperty("user.dir") + "/src/test/resources/abbyy10_coverAndText.xml");
		InputStream is = new FileInputStream(abbyy);
		ConvertReader reader = new AbbyyXMLReader();
		ConvertWriter writer = new HTMLWriter();
		
		writer.addImplementationSpecificOption("images", "src/test/resources/withTwoImages");
		writer.addImplementationSpecificOption("imagesoutdir", "target/sohnrey.html.images");
		
		OutputStream s = new FileOutputStream("target/sohnrey.html");
		//OutputStream s = System.out;
		writer.setTarget(s);
		
		reader.setWriter(writer);
		reader.read(is);
		s.close();
	}

}
