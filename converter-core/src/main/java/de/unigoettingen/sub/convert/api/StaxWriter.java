package de.unigoettingen.sub.convert.api;

import java.io.OutputStream;

import javanet.staxutils.IndentingXMLStreamWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.unigoettingen.sub.convert.model.Page;

abstract public class StaxWriter implements ConvertWriter {

	protected XMLStreamWriter xwriter;
	
	private OutputStream output = System.out;
	
	public StaxWriter() {
		XMLOutputFactory outfactory = XMLOutputFactory.newInstance();
		try {
			xwriter = outfactory
					.createXMLStreamWriter(output);
			xwriter = new IndentingXMLStreamWriter(xwriter);
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Override
	public void setTarget(OutputStream stream) {
		output = stream;
	}
	
	@Override
	public void writeStart() {
		try {
			writeStartStax();
			xwriter.flush();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void writeMetadata() {
		try {
			writeMetadataStax();
			xwriter.flush();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void writePage(Page page) {
		try {
			writePageStax(page);
			xwriter.flush();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void writeEnd() {
		try {
			writeEndStax();
			xwriter.flush();
			xwriter.close();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	abstract protected void writeStartStax() throws XMLStreamException;
	abstract protected void writeMetadataStax() throws XMLStreamException;
	abstract protected void writePageStax(Page page) throws XMLStreamException;
	abstract protected void writeEndStax() throws XMLStreamException;

}
