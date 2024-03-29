package de.unigoettingen.sub.convert.output;

/*

Copyright 2014 SUB Goettingen. All rights reserved.
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.

*/

import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import de.unigoettingen.sub.convert.api.StaxWriter;
import de.unigoettingen.sub.convert.model.Cell;
import de.unigoettingen.sub.convert.model.Char;
import de.unigoettingen.sub.convert.model.Image;
import de.unigoettingen.sub.convert.model.Language;
import de.unigoettingen.sub.convert.model.Line;
import de.unigoettingen.sub.convert.model.LineItem;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.NonWord;
import de.unigoettingen.sub.convert.model.Page;
import de.unigoettingen.sub.convert.model.PageItem;
import de.unigoettingen.sub.convert.model.Paragraph;
import de.unigoettingen.sub.convert.model.Row;
import de.unigoettingen.sub.convert.model.Table;
import de.unigoettingen.sub.convert.model.TextBlock;
import de.unigoettingen.sub.convert.model.WithCoordinates;

/**
 * 
 * Converts objects of the inner format into TEI P5 XML.
 * 
 */
public class CustomTeiP5Writer extends StaxWriter {

	private int pageCounter = 1;
	private int paragraphCounter = 1;

	/**
	 * Writes the start of a TEI document.
	 */
	@Override
	protected void writeStartStax() throws XMLStreamException {

		xwriter.writeStartDocument("UTF-8", "1.0");
		xwriter.writeStartElement("TEI");

	}

	/**
	 * Writes the TEI header including information about the languages used in
	 * the document.
	 */
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
					xwriter.writeCharacters(" " + meta.getOcrSoftwareVersion());
				}
				xwriter.writeEndElement(); // creation
			}
			if (langs) {
				xwriter.writeStartElement("langUsage");
				Set<Language> langsSet = new HashSet<Language>(
						meta.getLanguages());
				for (Language lang : langsSet) {
					xwriter.writeStartElement("language");
					if (lang.getLangId() != null) {
						xwriter.writeAttribute("ident", lang.getLangId());
					}
					xwriter.writeCharacters(lang.getValue());
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

	/**
	 * Writes one page including a page break.
	 */
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
			xwriter.writeAttribute("function", coordinatesFor(image));
		}
		xwriter.writeEndElement(); // figure
		paragraphCounter++;
	}

	private void writeTable(Table table) throws XMLStreamException {
		xwriter.writeStartElement("table");
		if (table.getTop() != null && table.getRight() != null) {
			xwriter.writeAttribute("function", coordinatesFor(table));
		}
		int rowsCount = table.getRows().size();
		xwriter.writeAttribute("rows", "" + rowsCount);
		int columnsCount = table.getRows().get(0).getCells().size();
		xwriter.writeAttribute("cols", "" + columnsCount);
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
					String tagName = "w";
					if (lineItem instanceof NonWord) {
						tagName = "pc";
					}
					xwriter.writeStartElement(tagName);
					if (lineItem.getTop() != null
							&& lineItem.getRight() != null) {
						xwriter.writeAttribute("function",
								coordinatesFor(lineItem));
					}
					for (Char ch : lineItem.getCharacters()) {
						xwriter.writeCharacters(ch.getValue());
					}
					xwriter.writeEndElement(); // w or pc

				}
				xwriter.writeEmptyElement("lb");
			}
			xwriter.writeEndElement(); // p
			paragraphCounter++;
		}
	}

	private String coordinatesFor(WithCoordinates item) {
		return "" + item.getLeft() + "," + item.getTop() + ","
				+ item.getRight() + "," + item.getBottom();
	}

	/**
	 * Writes the necessary TEI end tags.
	 */
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
