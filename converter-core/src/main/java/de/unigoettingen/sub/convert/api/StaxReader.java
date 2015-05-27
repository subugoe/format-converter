package de.unigoettingen.sub.convert.api;

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

import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * 
 * Expects an XML stream and uses the Stax API to parse the document.
 * 
 */
public abstract class StaxReader implements ConvertReader {

	protected ConvertWriter writer;
	protected XMLEventReader eventReader;
	
	@Override
	public void setWriter(ConvertWriter w) {
		writer = w;
	}

	/**
	 * reads an XML stream. The handleXXX() hook methods are called for each
	 * relevant XML event. Concrete implementations only need to implement the
	 * hook methods.
	 */
	@Override
	public void read(InputStream is) {

		if (writer == null) {
			throw new IllegalStateException("The Writer is not set");
		}

		XMLInputFactory factory = XMLInputFactory.newInstance();

		try {
			eventReader = factory.createXMLEventReader(is);

			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();

				switch (event.getEventType()) {

				case XMLStreamConstants.START_DOCUMENT:
					handleStartDocument();
					break;
				case XMLStreamConstants.START_ELEMENT:
					handleStartElement(event.asStartElement());
					break;
				case XMLStreamConstants.END_ELEMENT:
					handleEndElement(event.asEndElement());
					break;
				case XMLStreamConstants.END_DOCUMENT:
					handleEndDocument();
					break;
				default:
					// ignore all of the other events
				}
			}

			eventReader.close();

		} catch (XMLStreamException e) {
			throw new IllegalArgumentException("Error reading XML", e);
		}

	}

	abstract protected void handleStartDocument()
			throws XMLStreamException;

	abstract protected void handleStartElement(StartElement startTag) throws XMLStreamException;

	abstract protected void handleEndElement(EndElement endTag);

	abstract protected void handleEndDocument();

}
