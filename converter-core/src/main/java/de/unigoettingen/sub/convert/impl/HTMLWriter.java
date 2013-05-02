package de.unigoettingen.sub.convert.impl;

import java.io.File;

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
	private static final String PAGE_NUMBER_DESCRIPTION = "[page number], sets all pages to this number (used internally for EPUB writer)";
	private static final String ONE_DIR_DESCRIPTION = "[true or false], use the same directory for html and image files";
	private static final String PNG = "png";
	private Page currentPage;

	private ResourceHandler resourceHandler = new ResourceHandler();

	public HTMLWriter() {
		supportedOptions.put("images", FOLDER_WITH_IMAGES_DESCRIPTION);
		supportedOptions.put("imagesoutdir", OUTPUT_FOLDER_FOR_IMAGES);
		supportedOptions.put("fixedpagenr", PAGE_NUMBER_DESCRIPTION);
		supportedOptions.put("onedir", ONE_DIR_DESCRIPTION);
	}
	
	@Override
	protected void writeStartStax() throws XMLStreamException {
		xwriter.writeStartDocument("UTF-8", "1.0");
		xwriter.writeStartElement("html");
		writeCharset();
		xwriter.writeStartElement("body");
	}

	private void writeCharset() throws XMLStreamException {
		xwriter.writeStartElement("head");
		xwriter.writeStartElement("meta");
		xwriter.writeAttribute("http-equiv", "content-type");
		xwriter.writeAttribute("content", "text/html; charset=utf8");
		xwriter.writeEndElement(); // meta
		xwriter.writeEndElement(); // head
	}

	@Override
	protected void writeMetadataStax(Metadata meta) throws XMLStreamException {
	}

	@Override
	protected void writePageStax(Page page) throws XMLStreamException {
		
		if (actualOptions.get("fixedpagenr") != null) {
			pageNumber = Integer.parseInt(actualOptions.get("fixedpagenr"));
		} else {
			pageNumber++;
		}
		currentPage = page;
		
		if (imagesAvailable()) {
			addImageLink();
		}
		writeHtmlPage();
		
	}
	
	private boolean imagesAvailable() {
		return actualOptions.get("images") != null && actualOptions.get("imagesoutdir") != null;
	}

	private void addImageLink() throws XMLStreamException {
			int linesOnPage = getLinesOnPage(currentPage, 40);
			File imageOutDir = new File(actualOptions.get("imagesoutdir"));
			xwriter.writeEmptyElement("img");
			if ("true".equals(actualOptions.get("onedir"))) {
				xwriter.writeAttribute("src", String.format("image%d.%s", pageNumber, PNG));
			} else {
				xwriter.writeAttribute("src", String.format("%s/image%d.%s", imageOutDir.getName(), pageNumber, PNG));
			}
			xwriter.writeAttribute("style", String.format("height: %dem; float: left", linesOnPage));
			putImageForPageIntoDir(imageOutDir);
	}

	private int getLinesOnPage(Page page, int minimumLines) {
		int lines = 0;
		for (PageItem pageItem : page.getPageItems()) {
			if (pageItem instanceof TextBlock) {
				TextBlock block = (TextBlock) pageItem;
				for (Paragraph par : block.getParagraphs()) {
					lines += par.getLines().size();
				}
			}
		}
		return Math.max(lines, minimumLines);
	}

	private void putImageForPageIntoDir(File imageOutDir) {
		File imagesFolder = new File(actualOptions.get("images"));
		File tifFile = resourceHandler.getImageForPage(pageNumber, imagesFolder);
		File pngFile = new File(imageOutDir, "image" + pageNumber + "." + PNG);
		resourceHandler.tifToPng(tifFile, pngFile);
	}

	private void writeHtmlPage() throws XMLStreamException {
		int linesOnPage = getLinesOnPage(currentPage, 40);
		xwriter.writeStartElement("div");
		xwriter.writeAttribute("id", "page" + pageNumber);
		xwriter.writeAttribute("style", "height: " + linesOnPage + "em");
		
		for (PageItem pageItem : currentPage.getPageItems()) {
			if (pageItem instanceof TextBlock) {
				TextBlock block = (TextBlock) pageItem;
				writeTextBlock(block);
			}
		}
		xwriter.writeEndElement(); // div
	}

	private void writeTextBlock(TextBlock block) throws XMLStreamException {
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


	@Override
	protected void writeEndStax() throws XMLStreamException {
		xwriter.writeEndElement(); // body
		xwriter.writeEndElement(); // html
		xwriter.writeEndDocument();
	}

}
