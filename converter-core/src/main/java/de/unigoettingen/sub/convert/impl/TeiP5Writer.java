package de.unigoettingen.sub.convert.impl;

import javax.xml.stream.XMLStreamException;

import de.unigoettingen.sub.convert.api.StaxWriter;
import de.unigoettingen.sub.convert.model.Line;
import de.unigoettingen.sub.convert.model.Page;
import de.unigoettingen.sub.convert.model.PageItem;
import de.unigoettingen.sub.convert.model.Paragraph;
import de.unigoettingen.sub.convert.model.TextBlock;

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
