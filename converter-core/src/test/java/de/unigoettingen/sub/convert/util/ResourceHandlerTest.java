package de.unigoettingen.sub.convert.util;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;

public class ResourceHandlerTest {

	ResourceHandler handler;

	@Before
	public void setUp() throws Exception {
		handler = new ResourceHandler();
	}

	@Test
	public void getsOneImage() {
		File dir = new File("src/test/resources/withOneImage");
		File image = handler.getImageForPage(1, dir);
		
		assertEquals("image file", new File(dir, "00000001.tif"), image);
	}

	@Test
	public void getsTwoImages() {
		File dir = new File("src/test/resources/withTwoImages");
		File image1 = handler.getImageForPage(1, dir);
		File image2 = handler.getImageForPage(2, dir);
		
		assertEquals("image file", new File(dir, "00000001.tif"), image1);
		assertEquals("image file", new File(dir, "00000004.tif"), image2);
	}
	
	@Test
	public void withIllegalPageNumber() {
		File dir = new File("src/test/resources/withOneImage");
		try {
			handler.getImageForPage(0, dir);
			fail("exception was expected");
		} catch (IllegalStateException e) {
			assertEquals("error message", "No image found for page 0", e.getMessage());
		}
	}

	@Test
	public void withTooHighPageNumber() {
		File dir = new File("src/test/resources/withOneImage");
		try {
			handler.getImageForPage(2, dir);
			fail("exception was expected");
		} catch (IllegalStateException e) {
			assertEquals("error message", "No image found for page 2", e.getMessage());
		}
	}

	@Test
	public void withIllegalDir() {
		File dir = new File("src/test/resources/some_file.txt");
		try {
			handler.getImageForPage(1, dir);
			fail("exception was expected");
		} catch (IllegalStateException e) {
			assertThat(e.getMessage(), containsString("Not a folder"));
		}
	}
	
	@Test
	public void copiesTifToPng() {
		File tif = new File("src/test/resources/00000001.tif");
		File png = new File("target/converted.png");
		png.delete();
		handler.tifToPng(tif, png);
		assertTrue("image should be present", png.exists());
	}
	
	@Test
	public void makesDirectoryForPng() {
		File tif = new File("src/test/resources/00000001.tif");
		File png = new File("target/pngs/converted.png");
		png.delete();
		handler.tifToPng(tif, png);
		assertTrue("image should be present", png.exists());
	}
	


}