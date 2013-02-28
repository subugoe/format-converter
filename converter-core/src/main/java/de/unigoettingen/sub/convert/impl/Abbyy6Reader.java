package de.unigoettingen.sub.convert.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;

import de.unigoettingen.sub.convert.model.Document;
import de.unigoettingen.sub.convert.model.Page;

public class Abbyy6Reader {

	private Document document = new Document();
	private Page page = new Page();
	private Abbyy6Writer writer;
	
	public void setWriter(Abbyy6Writer writer) {
		this.writer = writer;
	}

	public void convert(InputStream stream) throws IOException {

		XMLInputFactory factory = XMLInputFactory.newInstance();

		try {
			XMLEventReader reader = factory.createXMLEventReader(stream);

			while (reader.hasNext()) {
				XMLEvent event = reader.nextEvent();

				switch (event.getEventType()) {

				case XMLStreamConstants.START_DOCUMENT:
					writer.writeStart();
					break;
				case XMLStreamConstants.START_ELEMENT:
					String name = event.asStartElement().getName()
							.getLocalPart();
					if (name.equals("document")) {
						
					} else if (name.equals("page")) {
						page = new Page();
						
					} else if (name.equals("charParams")) {
						XMLEvent nextEvent = reader.peek();
						if(nextEvent.isCharacters()) {
							page.setTextBlock(page.getTextBlock() + "\nchars/ " + nextEvent.asCharacters().toString() + " /chars");
							reader.nextEvent();
						}
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					name = event.asEndElement().getName()
							.getLocalPart();
					if (name.equals("page")) {
						writer.write(page);
					}
					break;
				case XMLStreamConstants.CHARACTERS:
					String s = event.asCharacters().toString();
					//page.setTextBlock(page.getTextBlock() + s);
					break;
				case XMLStreamConstants.END_DOCUMENT:
					writer.writeEnd();
					break;
				}
			}

			reader.close();

		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
