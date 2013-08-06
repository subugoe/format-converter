package de.unigoettingen.sub.convert.integrationtests;

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
import de.unigoettingen.sub.convert.input.abbyyxml.AbbyyXMLReader;
import de.unigoettingen.sub.convert.output.EPUBWriter;

public class AbbyyToEpubTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws IOException {
		File abbyy = new File("src/test/resources/abbyy10_coverAndText.xml");
//		File abbyy = new File("/home/dennis/digi/cli_output_sohnrey/20130128_sohnrey_bruderhof_1898_tif.xml");
		InputStream is = new FileInputStream(abbyy);
		ConvertReader reader = new AbbyyXMLReader();
		ConvertWriter writer = new EPUBWriter();
		
//		writer.addImplementationSpecificOption("scans", "/home/dennis/digi/fertig/20130128_sohnrey_bruderhof_1898_tif");
		writer.addImplementationSpecificOption("scans", "src/test/resources/withTwoImages");
		writer.addImplementationSpecificOption("includescans", "false");

		
		OutputStream s = new FileOutputStream("target/sohnrey.epub");
		//OutputStream s = System.out;
		writer.setTarget(s);
		
		reader.setWriter(writer);
		reader.read(is);
		s.close();

	}

}
