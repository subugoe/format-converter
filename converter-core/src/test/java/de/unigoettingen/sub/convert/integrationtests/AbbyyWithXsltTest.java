package de.unigoettingen.sub.convert.integrationtests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import de.unigoettingen.sub.convert.api.ConvertReader;
import de.unigoettingen.sub.convert.api.ConvertWriter;
import de.unigoettingen.sub.convert.impl.CustomTeiP5Writer;
import de.unigoettingen.sub.convert.impl.abbyyxml.AbbyyXMLReader;
import de.unigoettingen.sub.convert.impl.xslt.XsltWriter;

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

}
