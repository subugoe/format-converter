package de.unigoettingen.sub.convert.impl;

import java.io.FileWriter;
import java.io.IOException;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.unigoettingen.sub.convert.api.ConvertWriter;
import de.unigoettingen.sub.convert.api.StaxWriter;
import de.unigoettingen.sub.convert.model.Document;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.Page;

public class Abbyy6Writer extends StaxWriter {

	private XMLStreamWriter xwriter;
	
	public void writeStart() {
		XMLOutputFactory outfactory = XMLOutputFactory.newInstance();

		try {
			xwriter = outfactory
					.createXMLStreamWriter(new FileWriter("/tmp/dump.xml"));
			
			xwriter.writeStartDocument();
			xwriter.writeStartElement("document");

			xwriter.flush();
			
			
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void writePage(Page page) {
		try {
			xwriter.writeStartElement("page");
			
			//xwriter.writeCharacters(page.getTextBlock());
			
			xwriter.writeEndElement();
			
			xwriter.flush();
			
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeEnd() {
		try {
			xwriter.writeEndElement();
			xwriter.writeEndDocument();
			xwriter.flush();
			xwriter.close();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void writeMetadata(Metadata meta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void writeStartStax() throws XMLStreamException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void writeMetadataStax(Metadata meta) throws XMLStreamException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void writePageStax(Page page) throws XMLStreamException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void writeEndStax() throws XMLStreamException {
		// TODO Auto-generated method stub
		
	}
	
}
