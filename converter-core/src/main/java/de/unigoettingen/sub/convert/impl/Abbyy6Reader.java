package de.unigoettingen.sub.convert.impl;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import de.unigoettingen.sub.convert.api.StaxReader;
import de.unigoettingen.sub.convert.model.Page;

public class Abbyy6Reader extends StaxReader {

	@Override
	protected void handleStartDocument() {
		writer.writeStart();
	}
	@Override
	protected void handleStartElement(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {
		String name = event.asStartElement().getName()
				.getLocalPart();
		if (name.equals("document")) {
			writer.writeMetadata();
		} else if (name.equals("page")) {
			page = new Page();
			
		} else if (name.equals("charParams")) {
			XMLEvent nextEvent = eventReader.peek();
			if(nextEvent.isCharacters()) {
				//page.setTextBlock(page.getTextBlock() + "\nchars/ " + nextEvent.asCharacters().toString() + " /chars");
				eventReader.nextEvent();
			}
		}
	}
	@Override
	protected void handleEndElement(XMLEvent event) {
		String name = event.asEndElement().getName()
				.getLocalPart();
		if (name.equals("page")) {
			writer.writePage(page);
		}
	}
	@Override
	protected void handleEndDocument() {
		writer.writeEnd();
	}
}
