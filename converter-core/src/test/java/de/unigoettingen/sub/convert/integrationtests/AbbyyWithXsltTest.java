package de.unigoettingen.sub.convert.integrationtests;

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
import de.unigoettingen.sub.convert.output.XsltWriter;

public class AbbyyWithXsltTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws IOException {
		InputStream is = new FileInputStream("src/test/resources/abbyy10_coverAndText.xml");
		ConvertReader reader = new AbbyyXMLReader();
		ConvertWriter writer = new XsltWriter();
		writer.addImplementationSpecificOption("xslt", "src/test/resources/xslt/toTei.xsl");

		OutputStream s = new FileOutputStream("target/xsltResult.xml");
		//OutputStream s = System.out;
		writer.setTarget(s);
		
		reader.setWriter(writer);
		reader.read(is);
		s.close();

	}

	@Test
	public void hyphenation() throws IOException {
		InputStream is = new FileInputStream("src/test/resources/abbyy10_withHyphenation.xml");
		ConvertReader reader = new AbbyyXMLReader();
		ConvertWriter writer = new XsltWriter();
		writer.addImplementationSpecificOption("xslt", "src/test/resources/xslt/toTei.xsl");

		OutputStream s = new FileOutputStream("target/xsltResult_hyphenation.xml");
		//OutputStream s = System.out;
		writer.setTarget(s);
		
		reader.setWriter(writer);
		reader.read(is);
		s.close();

	}

	@Test
	public void oldTei() throws IOException {
		InputStream is = new FileInputStream("src/test/resources/abbyy10_withHyphenation.xml");
		ConvertReader reader = new AbbyyXMLReader();
		ConvertWriter writer = new XsltWriter();
		writer.addImplementationSpecificOption("xslt", "src/test/resources/xslt/toOldTei.xsl");

		OutputStream s = new FileOutputStream("target/xsltResult_oldTei.xml");
		//OutputStream s = System.out;
		writer.setTarget(s);
		
		reader.setWriter(writer);
		reader.read(is);
		s.close();

	}

}
