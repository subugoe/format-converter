package de.unigoettingen.sub.convert.integrationtests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.unigoettingen.sub.convert.api.ConvertReader;
import de.unigoettingen.sub.convert.api.ConvertWriter;
import de.unigoettingen.sub.convert.impl.PDFWriter;
import de.unigoettingen.sub.convert.impl.abbyyxml.AbbyyXMLReader;

public class AbbyyToPdfTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws IOException {
		File abbyy = new File(
				System.getProperty("user.dir") + "/src/test/resources/abbyy10_coverAndText.xml");
		InputStream is = new FileInputStream(abbyy);
		ConvertReader reader = new AbbyyXMLReader();
		ConvertWriter writer = new PDFWriter();
		
		//writer.addImplementationSpecificOption("scans", "src/test/resources/withTwoImages");
		//writer.addImplementationSpecificOption("includescans", "false");

		
		OutputStream s = new FileOutputStream("target/sohnrey.pdf");
		//OutputStream s = System.out;
		writer.setTarget(s);
		
		reader.setWriter(writer);
		reader.read(is);
		s.close();
	}

}