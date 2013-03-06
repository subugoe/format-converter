package de.unigoettingen.sub.convert.api;

import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import de.unigoettingen.sub.convert.model.Page;

public abstract class StaxReader implements ConvertReader {

	protected ConvertWriter writer;
	protected Page page = new Page();

	@Override
	public void setWriter(ConvertWriter w) {
		writer = w;
	}
	
	@Override
	public void read(InputStream is) {
		
		XMLInputFactory factory = XMLInputFactory.newInstance();

		try {
			XMLEventReader eventReader = factory.createXMLEventReader(is);

			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();

				switch (event.getEventType()) {

				case XMLStreamConstants.START_DOCUMENT:
					handleStartDocument();
					break;
				case XMLStreamConstants.START_ELEMENT:
					handleStartElement(event, eventReader);
					break;
				case XMLStreamConstants.END_ELEMENT:
					handleEndElement(event);
					break;
				case XMLStreamConstants.CHARACTERS:
					//String s = event.asCharacters().toString();
					break;
				case XMLStreamConstants.END_DOCUMENT:
					handleEndDocument();
					break;
				}
			}

			eventReader.close();

		} catch (XMLStreamException e) {
			throw new IllegalArgumentException("Error reading XML", e);
		}


	}
	
	abstract protected void handleStartDocument();
	abstract protected void handleStartElement(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException;
	abstract protected void handleEndElement(XMLEvent event);
	abstract protected void handleEndDocument();

}
