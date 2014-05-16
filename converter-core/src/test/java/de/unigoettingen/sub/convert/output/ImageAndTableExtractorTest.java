package de.unigoettingen.sub.convert.output;

import static de.unigoettingen.sub.convert.model.builders.ImageBuilder.image;
import static de.unigoettingen.sub.convert.model.builders.PageBuilder.page;
import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import de.unigoettingen.sub.convert.model.Page;

public class ImageAndTableExtractorTest {

	@Test
	public void test() {
		ImageAndTableExtractor extractor = new ImageAndTableExtractor();
		Page page = page().withHeight(3655).with(image().withCoordinatesLTRB(956, 2112, 1464, 2744)).build();
		
		extractor.addImplementationSpecificOption("scans", "src/test/resources/withOneImage");
		extractor.addImplementationSpecificOption("imagesoutdir", "target/test.extracted.images");
		extractor.writePage(page);
		
		File resultPng = new File("target/test.extracted.images/00000001.tif.1.image.png");
		assertTrue(resultPng.exists());
	}

}
