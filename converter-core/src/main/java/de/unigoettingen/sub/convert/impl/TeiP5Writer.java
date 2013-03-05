package de.unigoettingen.sub.convert.impl;

import javax.xml.stream.XMLStreamException;

import de.unigoettingen.sub.convert.api.StaxWriter;
import de.unigoettingen.sub.convert.model.Char;
import de.unigoettingen.sub.convert.model.Line;
import de.unigoettingen.sub.convert.model.LineItem;
import de.unigoettingen.sub.convert.model.Page;
import de.unigoettingen.sub.convert.model.PageItem;
import de.unigoettingen.sub.convert.model.Paragraph;
import de.unigoettingen.sub.convert.model.TextBlock;
import de.unigoettingen.sub.convert.model.Word;

public class TeiP5Writer extends StaxWriter {

	@Override
	protected void writeStartStax() throws XMLStreamException {

		xwriter.writeStartDocument("UTF-8", "1.0");
		xwriter.writeStartElement("TEI");
		xwriter.writeDefaultNamespace("http://www.tei-c.org/ns/1.0");

	}

	@Override
	protected void writeMetadataStax() throws XMLStreamException {
		xwriter.writeStartElement("teiHeader");
		xwriter.writeEndElement();

		addTeiStartElements();

	}

	private void addTeiStartElements() throws XMLStreamException {
		xwriter.writeStartElement("text");
		xwriter.writeStartElement("body");

	}

	@Override
	protected void writePageStax(Page page) throws XMLStreamException {

		for (PageItem item : page.getPageItems()) {
			xwriter.writeStartElement("div");
			if (item instanceof TextBlock) {
				TextBlock block = (TextBlock) item;
				for (Paragraph par : block.getParagraphs()) {
					xwriter.writeStartElement("p");
					for (Line line : par.getLines()) {
						for (LineItem lineItem : line.getLineItems()) {
							if (lineItem instanceof Word) {
								xwriter.writeStartElement("w");
								xwriter.writeAttribute("function", wordCoordinates(lineItem));
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
				}
			}
			xwriter.writeEndElement(); // div

		}
		//xwriter.writeCharacters("" + page.getHeight());

		xwriter.writeEmptyElement("pb");
	}

	private String wordCoordinates(LineItem word) {
		return "" + word.getLeft() + "," + word.getTop() + "," + word.getRight() + "," + word.getBottom();
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
