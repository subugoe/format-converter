package de.unigoettingen.sub.convert.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unigoettingen.sub.convert.api.ConvertReader;
import de.unigoettingen.sub.convert.api.ConvertWriter;
import de.unigoettingen.sub.convert.impl.abbyyxml.AbbyyXMLReader;


public class AbbyyToTeiTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws IOException {
		File abbyy = new File(
				System.getProperty("user.dir") + "/src/test/resources/abbyy10_coverPage.xml");
		InputStream is = new FileInputStream(abbyy);
		ConvertReader reader = new AbbyyXMLReader();
		ConvertWriter writer = new CustomTeiP5Writer();
		
		OutputStream s = new FileOutputStream("/tmp/tei.xml");
		//OutputStream s = System.out;
		writer.setTarget(s);
		
		reader.setWriter(writer);
		reader.read(is);
		s.close();
	}

}
