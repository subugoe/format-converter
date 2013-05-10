package de.unigoettingen.sub.convert.impl;

import java.io.File;

import javax.xml.stream.XMLStreamException;

import de.unigoettingen.sub.convert.api.StaxWriter;
import de.unigoettingen.sub.convert.model.Char;
import de.unigoettingen.sub.convert.model.Image;
import de.unigoettingen.sub.convert.model.Line;
import de.unigoettingen.sub.convert.model.LineItem;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.Page;
import de.unigoettingen.sub.convert.model.PageItem;
import de.unigoettingen.sub.convert.model.Paragraph;
import de.unigoettingen.sub.convert.model.Row;
import de.unigoettingen.sub.convert.model.Cell;
import de.unigoettingen.sub.convert.model.Table;
import de.unigoettingen.sub.convert.model.TextBlock;
import de.unigoettingen.sub.convert.util.ImageArea;
import de.unigoettingen.sub.convert.util.ResourceHandler;

public class HTMLWriter extends StaxWriter {

	private int pageNumber = 0;
	private int subimageNumber = 0;
	private static final String FOLDER_WITH_IMAGES_DESCRIPTION = "[folder] (containing original Tiff images)";
	private static final String OUTPUT_FOLDER_FOR_IMAGES = "[folder] for the converted PNG images";
	private static final String PAGE_NUMBER_DESCRIPTION = "[page number], sets all pages to this number (used internally for EPUB writer)";
	private static final String ONE_DIR_DESCRIPTION = "[true or false], use the same directory for html and image files (used internally for EPUB writer)";
	private static final String INCLUDE_SCANS_DESCRIPTION = "[true or false], include the original scanned images into the result file, default is 'true'";
	private static final String PNG = "png";
	private Page currentPage;

	private ResourceHandler resourceHandler = new ResourceHandler();

	public HTMLWriter() {
		supportedOptions.put("scans", FOLDER_WITH_IMAGES_DESCRIPTION);
		supportedOptions.put("imagesoutdir", OUTPUT_FOLDER_FOR_IMAGES);
		supportedOptions.put("fixedpagenr", PAGE_NUMBER_DESCRIPTION);
		supportedOptions.put("onedir", ONE_DIR_DESCRIPTION);
		supportedOptions.put("includescans", INCLUDE_SCANS_DESCRIPTION);
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
		
		if (setOptions.get("fixedpagenr") != null) {
			pageNumber = Integer.parseInt(setOptions.get("fixedpagenr"));
		} else {
			pageNumber++;
		}
		currentPage = page;
		
		if (scansAvailable() && includeScans()) {
			addLinkToScan();
		}
		writeHtmlPage();
	}
	
	private boolean scansAvailable() {
		return setOptions.get("scans") != null && setOptions.get("imagesoutdir") != null;
	}

	private boolean includeScans() {
		return !"false".equals(setOptions.get("includescans"));
	}
	
	private void addLinkToScan() throws XMLStreamException {
			int imageHeight = scaleToHeightInEm(currentPage.getHeight());
			File imageOutDir = new File(setOptions.get("imagesoutdir"));
			xwriter.writeEmptyElement("img");
			if ("true".equals(setOptions.get("onedir"))) {
				xwriter.writeAttribute("src", String.format("scan%d.%s", pageNumber, PNG));
			} else {
				xwriter.writeAttribute("src", String.format("%s/scan%d.%s", imageOutDir.getName(), pageNumber, PNG));
			}
			xwriter.writeAttribute("style", String.format("height: %dem", imageHeight));
			putScanForPageIntoDir(imageOutDir);
	}

	private int scaleToHeightInEm(int heightInPix) {
		int linesOnPage = getLinesOnPage(currentPage, 50);
		float scaledHeight = heightInPix / currentPage.getHeight().floatValue() * linesOnPage;
		return (int) scaledHeight;
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

	private void putScanForPageIntoDir(File imageOutDir) {
		File imagesFolder = new File(setOptions.get("scans"));
		File tifFile = resourceHandler.getImageForPage(pageNumber, imagesFolder);
		File pngFile = new File(imageOutDir, "scan" + pageNumber + "." + PNG);
		resourceHandler.tifToPng(tifFile, pngFile);
	}

	private void writeHtmlPage() throws XMLStreamException {
		xwriter.writeStartElement("div");
		xwriter.writeAttribute("id", "page" + pageNumber);
		
		for (PageItem pageItem : currentPage.getPageItems()) {
			if (pageItem instanceof TextBlock) {
				TextBlock block = (TextBlock) pageItem;
				writeTextBlock(block);
			} else if (pageItem instanceof Table) {
				Table table = (Table) pageItem;
				writeTable(table);
			} else if (scansAvailable() && pageItem instanceof Image) {
				subimageNumber++;
				Image image = (Image) pageItem;
				writeSubimage(image);
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

	private void writeTable(Table table) throws XMLStreamException {
		xwriter.writeStartElement("table");
		for (Row row : table.getRows()) {
			xwriter.writeStartElement("tr");
			for (Cell cell : row.getCells()) {
				xwriter.writeStartElement("td");
				PageItem item = cell.getContent();
				if (item instanceof TextBlock) {
					writeTextBlock((TextBlock) item);
				}
				xwriter.writeEndElement(); // td
			}
			xwriter.writeEndElement(); // tr
		}
		xwriter.writeEndElement(); // table
	}
	
	private void writeSubimage(Image image) throws XMLStreamException {
		int heightInPix = image.getBottom() - image.getTop();
		int imageHeight = scaleToHeightInEm(heightInPix);
		File imageOutDir = new File(setOptions.get("imagesoutdir"));
		xwriter.writeEmptyElement("img");
		if ("true".equals(setOptions.get("onedir"))) {
			xwriter.writeAttribute("src", String.format("subimage%d-%d.%s", pageNumber, subimageNumber, PNG));
		} else {
			xwriter.writeAttribute("src", String.format("%s/subimage%d-%d.%s", imageOutDir.getName(), pageNumber, subimageNumber, PNG));
		}
		xwriter.writeAttribute("style", String.format("height: %dem", imageHeight));
		putSubimageIntoDir(image, imageOutDir);
	}
	
	private void putSubimageIntoDir(Image image, File imageOutDir) {
		File imagesFolder = new File(setOptions.get("scans"));
		File tifFile = resourceHandler.getImageForPage(pageNumber, imagesFolder);
		File pngFile = new File(imageOutDir, "subimage" + pageNumber + "-" + subimageNumber + "." + PNG);
		ImageArea area = ImageArea.createLTRB(image.getLeft(), image.getTop(), image.getRight(), image.getBottom());
		resourceHandler.tifToPngAndCut(tifFile, pngFile, area);
	}


	@Override
	protected void writeEndStax() throws XMLStreamException {
		xwriter.writeEndElement(); // body
		xwriter.writeEndElement(); // html
		xwriter.writeEndDocument();
	}

}
