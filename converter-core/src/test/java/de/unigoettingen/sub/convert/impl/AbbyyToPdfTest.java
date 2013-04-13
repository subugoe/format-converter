package de.unigoettingen.sub.convert.impl;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;

import de.unigoettingen.sub.convert.api.ConvertReader;
import de.unigoettingen.sub.convert.api.ConvertWriter;

public class AbbyyToPdfTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws IOException {
		File abbyy = new File(
				System.getProperty("user.dir") + "/src/test/resources/abbyy10_textPage.xml");
		InputStream is = new FileInputStream(abbyy);
		ConvertReader reader = new AbbyyXMLReader();
		ConvertWriter writer = new PDFWriter();
		
		OutputStream s = new FileOutputStream("/tmp/sohnrey_top_bottom.pdf");
		//OutputStream s = System.out;
		writer.setTarget(s);
		
		reader.setWriter(writer);
		reader.read(is);
		s.close();
	}

}
