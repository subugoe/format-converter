package de.unigoettingen.sub.convert.impl;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import de.unigoettingen.sub.convert.impl.abbyyxml.AbbyyXMLReader;

public class AbbyyToEpubTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws IOException {
//		File abbyy = new File(
//				System.getProperty("user.dir") + "/src/test/resources/abbyy10_textPage.xml");
		File abbyy = new File("/home/dennis/digi/cli_output_sohnrey/20130128_sohnrey_bruderhof_1898_tif.xml");
		InputStream is = new FileInputStream(abbyy);
		ConvertReader reader = new AbbyyXMLReader();
		ConvertWriter writer = new EPUBWriter();
		
		Map<String, String> options = new HashMap<String, String>();
		options.put("images", "/home/dennis/digi/fertig/20130128_sohnrey_bruderhof_1898_tif");
		writer.setImplementationSpecificOptions(options);

		
		OutputStream s = new FileOutputStream("/tmp/sohnrey.epub");
		//OutputStream s = System.out;
		writer.setTarget(s);
		
		reader.setWriter(writer);
		reader.read(is);
		s.close();

	}

}
