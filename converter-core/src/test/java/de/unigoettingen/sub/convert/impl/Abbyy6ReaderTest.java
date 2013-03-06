package de.unigoettingen.sub.convert.impl;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.mockito.Mockito.*;
import de.unigoettingen.sub.convert.api.ConvertReader;
import de.unigoettingen.sub.convert.api.ConvertWriter;

public class Abbyy6ReaderTest {

	private final File PATH = new File(System.getProperty("user.dir") + "/src/test/resources/");
	
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
	public void shouldNotWorkWithoutWriter() {
		ConvertReader reader = new Abbyy6Reader();
		InputStream is = mock(InputStream.class);
		try {
			reader.read(is);
			fail("did not throw NPE");
		} catch (NullPointerException e) {
			
		}
	}
	
	@Test
	public void shouldNotWorkWithTextFile() throws FileNotFoundException {
		ConvertReader reader = new Abbyy6Reader();
		InputStream is = new FileInputStream(new File(PATH, "some_file.txt"));
		ConvertWriter writer = mock(ConvertWriter.class);
		reader.setWriter(writer);
		try {
			reader.read(is);
			fail("did not throw exception");
		} catch (IllegalArgumentException e) {
			
		}
	}
	
	@Test
	public void shouldNotWorkWithWrongFormat() throws FileNotFoundException {
		ConvertReader reader = new Abbyy6Reader();
		InputStream is = new FileInputStream(new File(PATH, "some_file.xml"));
		ConvertWriter writer = mock(ConvertWriter.class);
		reader.setWriter(writer);
		reader.read(is);
		fail("");
	}
	
	@Test
	public void test() throws FileNotFoundException {
		File abbyy = new File(
				System.getProperty("user.dir") + "/src/test/resources/abbyy6_metadata.xml");
		InputStream is = new FileInputStream(abbyy);
		ConvertReader reader = new Abbyy6Reader();

	}

}
