package de.unigoettingen.sub.convert.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.xml.stream.XMLStreamException;

import de.unigoettingen.sub.convert.api.StaxWriter;
import de.unigoettingen.sub.convert.model.Char;
import de.unigoettingen.sub.convert.model.Line;
import de.unigoettingen.sub.convert.model.LineItem;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.Page;
import de.unigoettingen.sub.convert.model.PageItem;
import de.unigoettingen.sub.convert.model.Paragraph;
import de.unigoettingen.sub.convert.model.TextBlock;
import de.unigoettingen.sub.convert.util.ResourceHandler;

public class HTMLWriter extends StaxWriter {

	private int pageNumber = 0;
	private static final String FOLDER_WITH_IMAGES_DESCRIPTION = "[folder] (containing original Tiff images)";
	private static final String OUTPUT_FOLDER_FOR_IMAGES = "[folder] for the converted PNG images";
	private static final String PNG = "png";

	private ResourceHandler resourceHandler = new ResourceHandler();

	public HTMLWriter() {
		supportedOptions.put("images", FOLDER_WITH_IMAGES_DESCRIPTION);
		supportedOptions.put("imagesoutdir", OUTPUT_FOLDER_FOR_IMAGES);
	}
	
	@Override
	protected void writeStartStax() throws XMLStreamException {
		xwriter.writeStartDocument("UTF-8", "1.0");
		xwriter.writeStartElement("html");
		xwriter.writeStartElement("body");
	}

	@Override
	protected void writeMetadataStax(Metadata meta) throws XMLStreamException {
	}

	@Override
	protected void writePageStax(Page page) throws XMLStreamException {
		pageNumber++;
		
		int linesOnPage = getLinesOnPage(page, 40);
		
		if (imagesAvailable()) {
			File imagesOutDir = new File(actualOptions.get("imagesoutdir"));
			xwriter.writeEmptyElement("img");
			xwriter.writeAttribute("src", imagesOutDir.getName() + "/image" + pageNumber + "." + PNG);
			xwriter.writeAttribute("style", "height: " + linesOnPage + "em; float: left");
			try {
				prepareImageForPage();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		xwriter.writeStartElement("div");
		xwriter.writeAttribute("id", "page" + pageNumber);
		xwriter.writeAttribute("style", "height: " + linesOnPage + "em");
		
		for (PageItem pageItem : page.getPageItems()) {
			if (pageItem instanceof TextBlock) {
				TextBlock block = (TextBlock) pageItem;
				for (Paragraph par : block.getParagraphs()) {
					xwriter.writeStartElement("p");
					
					for (Line line : par.getLines()) {
						for (LineItem item : line.getLineItems()) {
							for (Char ch : item.getCharacters()) {
								xwriter.writeCharacters(ch.getValue());
							}
						}
						xwriter.writeEmptyElement("br");
					}
					
					xwriter.writeEndElement(); // p
				}
			}
		}

		xwriter.writeEndElement(); // div

	}

	private int getLinesOnPage(Page page, int minimum) {
		int lines = 0;
		for (PageItem pageItem : page.getPageItems()) {
			if (pageItem instanceof TextBlock) {
				TextBlock block = (TextBlock) pageItem;
				for (Paragraph par : block.getParagraphs()) {
					lines += par.getLines().size();
				}
			}
		}
		
		return Math.max(lines, minimum);
	}

	private boolean imagesAvailable() {
		return actualOptions.get("images") != null && actualOptions.get("imagesoutdir") != null;
	}
	
	private void prepareImageForPage() throws IOException {
		File imagesFolder = new File(actualOptions.get("images"));
		File imageFile = resourceHandler.getImageForPage(pageNumber, imagesFolder);
		BufferedImage tif = ImageIO.read(imageFile);
		
		File imageOutDir = new File(actualOptions.get("imagesoutdir"));
		imageOutDir.mkdir();
		File pngImage = new File(imageOutDir, "image" + pageNumber + "." + PNG);

		FileOutputStream fos = new FileOutputStream(pngImage);
		ImageIO.write(tif, PNG, fos);
	}

	@Override
	protected void writeEndStax() throws XMLStreamException {
		xwriter.writeEndElement(); // body
		xwriter.writeEndElement(); // html
		xwriter.writeEndDocument();
	}

}
