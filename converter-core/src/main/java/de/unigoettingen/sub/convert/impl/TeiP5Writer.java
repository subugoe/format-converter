package de.unigoettingen.sub.convert.impl;

import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import de.unigoettingen.sub.convert.api.StaxWriter;
import de.unigoettingen.sub.convert.model.Cell;
import de.unigoettingen.sub.convert.model.Char;
import de.unigoettingen.sub.convert.model.Image;
import de.unigoettingen.sub.convert.model.Line;
import de.unigoettingen.sub.convert.model.LineItem;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.Page;
import de.unigoettingen.sub.convert.model.PageItem;
import de.unigoettingen.sub.convert.model.Paragraph;
import de.unigoettingen.sub.convert.model.Row;
import de.unigoettingen.sub.convert.model.Table;
import de.unigoettingen.sub.convert.model.TextBlock;
import de.unigoettingen.sub.convert.model.WithCoordinates;
import de.unigoettingen.sub.convert.model.Word;

public class TeiP5Writer extends StaxWriter {

	private int pageCounter = 1;
	private int paragraphCounter = 1;
	
	@Override
	protected void writeStartStax() throws XMLStreamException {

		xwriter.writeStartDocument("UTF-8", "1.0");
		xwriter.writeStartElement("TEI");
		xwriter.writeDefaultNamespace("http://www.tei-c.org/ns/1.0");

	}

	@Override
	protected void writeMetadataStax(Metadata meta) throws XMLStreamException {
		xwriter.writeStartElement("teiHeader");
		boolean swName = meta.getOcrSoftwareName() != null;
		boolean swVersion = meta.getOcrSoftwareVersion() != null;
		boolean langs = !meta.getLanguages().isEmpty();
		boolean infosPresent = swName || swVersion || langs;
		if (infosPresent) {
			xwriter.writeStartElement("profileDesc");
			if (swName || swVersion) {
				xwriter.writeStartElement("creation");
					if (swName) {
						xwriter.writeCharacters(meta.getOcrSoftwareName());
					}
					if (swVersion) {
						xwriter.writeCharacters(meta.getOcrSoftwareVersion());
					}
				xwriter.writeEndElement(); // creation
			}
			if (langs) {
				xwriter.writeStartElement("langUsage");
				Set<String> langsSet = new HashSet<String>(meta.getLanguages());
				for (String lang : langsSet) {
					xwriter.writeStartElement("language");
					xwriter.writeCharacters(lang);
					xwriter.writeEndElement(); // language
				}
				xwriter.writeEndElement(); // langUsage
				
			}
			xwriter.writeEndElement(); // profileDesc
		}
		
		xwriter.writeEndElement(); // teiHeader

		addTeiStartElements();

	}

	private void addTeiStartElements() throws XMLStreamException {
		xwriter.writeStartElement("text");
		xwriter.writeStartElement("body");

	}

	@Override
	protected void writePageStax(Page page) throws XMLStreamException {
		for (PageItem item : page.getPageItems()) {
			if (item instanceof TextBlock) {
				TextBlock block = (TextBlock) item;
				writeTextBlock(block);
			} else if (item instanceof Table) {
				Table table = (Table) item;
				writeTable(table);
			} else if (item instanceof Image) {
				Image image = (Image) item;
				writeImage(image);
			}
		}
		xwriter.writeEmptyElement("milestone");
		xwriter.writeAttribute("n", "" + pageCounter);
		xwriter.writeAttribute("type", "page");
		xwriter.writeEmptyElement("pb");
		pageCounter++;
	}

	private void writeImage(Image image) throws XMLStreamException {
		xwriter.writeStartElement("figure");
		xwriter.writeAttribute("id", "ID" + paragraphCounter);
		if (image.getTop() != null && image.getRight() != null) {
			xwriter.writeAttribute("function", wordCoordinates(image));
		}
		xwriter.writeEndElement(); // figure
		paragraphCounter++;
	}

	private void writeTable(Table table) throws XMLStreamException {
		xwriter.writeStartElement("table");
		for (Row row : table.getRows()) {
			xwriter.writeStartElement("row");
			for (Cell cell : row.getCells()) {
				xwriter.writeStartElement("cell");
				PageItem cellContent = cell.getContent();
				if (cellContent instanceof TextBlock) {
					TextBlock cellBlock = (TextBlock) cellContent;
					writeTextBlock(cellBlock);
				} else if (cellContent instanceof Image) {
					Image cellImage = (Image) cellContent;
					writeImage(cellImage);
				}
				xwriter.writeEndElement(); // cell
			}
			xwriter.writeEndElement(); // row
		}
		xwriter.writeEndElement(); // table
	}

	private void writeTextBlock(TextBlock block) throws XMLStreamException {
		for (Paragraph par : block.getParagraphs()) {
			xwriter.writeStartElement("p");
			xwriter.writeAttribute("id", "ID" + paragraphCounter);
			for (Line line : par.getLines()) {
				for (LineItem lineItem : line.getLineItems()) {
					if (lineItem instanceof Word) {
						xwriter.writeStartElement("w");
						if (lineItem.getTop() != null && lineItem.getRight() != null) {
							xwriter.writeAttribute("function", wordCoordinates(lineItem));
						}
					}
					for (Char ch : lineItem.getCharacters()) {
						xwriter.writeCharacters(ch.getValue());
					}
					if (lineItem instanceof Word) {
						xwriter.writeEndElement(); // w
					}

				}
				xwriter.writeEmptyElement("lb");
			}
			xwriter.writeEndElement(); // p
			paragraphCounter++;
		}
	}

	private String wordCoordinates(WithCoordinates item) {
		return "" + item.getLeft() + "," + item.getTop() + "," + item.getRight() + "," + item.getBottom();
	}
	
	@Override
	protected void writeEndStax() throws XMLStreamException {
		addTeiEndElements();
		xwriter.writeEndElement(); // TEI
		xwriter.writeEndDocument();

	}

	private void addTeiEndElements() throws XMLStreamException {
		xwriter.writeEndElement(); // body
		xwriter.writeEndElement(); // text

	}

}
