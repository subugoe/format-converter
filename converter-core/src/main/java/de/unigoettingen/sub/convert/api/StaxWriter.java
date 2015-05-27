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

import java.io.OutputStream;

import javanet.staxutils.IndentingXMLStreamWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.Page;

/**
 * 
 * Encapsulates common Stax behavior and exception handling for child classes.
 * Concrete children only need to implement the writeXXXStax() hook methods.
 * 
 */
abstract public class StaxWriter extends WriterWithOptions implements ConvertWriter {

	protected XMLStreamWriter xwriter;

	@Override
	public void setTarget(OutputStream stream) {
		super.setTarget(stream);
		XMLOutputFactory outfactory = XMLOutputFactory.newInstance();
		try {
			xwriter = outfactory.createXMLStreamWriter(output, "UTF-8");
			xwriter = new IndentingXMLStreamWriter(xwriter);
		} catch (XMLStreamException e) {
			throw new IllegalStateException(
					"Could not initialize the stream writer");
		}
	}

	@Override
	public void writeStart() {
		checkOutputStream();
		try {
			writeStartStax();
			xwriter.flush();
		} catch (XMLStreamException e) {
			throw new IllegalStateException("Could not write XML header");
		}

	}


	@Override
	public void writeMetadata(Metadata meta) {
		checkOutputStream();
		try {
			writeMetadataStax(meta);
			xwriter.flush();
		} catch (XMLStreamException e) {
			throw new IllegalStateException("Could not write XML metadata");
		}

	}

	@Override
	public void writePage(Page page) {
		checkOutputStream();
		try {
			writePageStax(page);
			xwriter.flush();
		} catch (XMLStreamException e) {
			throw new IllegalStateException("Could not write XML page");
		}

	}

	@Override
	public void writeEnd() {
		checkOutputStream();
		try {
			writeEndStax();
			xwriter.flush();
			xwriter.close();
		} catch (XMLStreamException e) {
			throw new IllegalStateException("Could not write XML file ending");
		}

	}
	
	abstract protected void writeStartStax() throws XMLStreamException;

	abstract protected void writeMetadataStax(Metadata meta)
			throws XMLStreamException;

	abstract protected void writePageStax(Page page) throws XMLStreamException;

	abstract protected void writeEndStax() throws XMLStreamException;

}
