package de.unigoettingen.sub.convert.api;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javanet.staxutils.IndentingXMLStreamWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.Page;

/**
 * 
 * Encapsulates common Stax behavior and exception handling for child classes.
 * Concrete children only need to implement the writeXXXStax() hook methods.
 * 
 */
abstract public class StaxWriter implements ConvertWriter {

	private final static Logger LOGGER = LoggerFactory.getLogger(StaxWriter.class);
	protected XMLStreamWriter xwriter;

	private OutputStream output;
	
	protected Map<String, String> supportedOptions = new HashMap<String, String>();
	protected Map<String, String> actualOptions = new HashMap<String, String>();

	@Override
	public void setTarget(OutputStream stream) {
		output = stream;
		XMLOutputFactory outfactory = XMLOutputFactory.newInstance();
		try {
			xwriter = outfactory.createXMLStreamWriter(output);
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

	private void checkOutputStream() {
		if (output == null) {
			throw new IllegalStateException("The output target is not set");
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
	
	@Override
	public void addImplementationSpecificOption(String key, String value) {
		if (supportedOptions.get(key) != null) {
			actualOptions.put(key, value);
		} else {
			LOGGER.warn("The option is not supported: " + key);
		}
	}
	
	@Override
	public Map<String, String> getSupportedOptions() {
		return new HashMap<String, String>(supportedOptions);
	}

	abstract protected void writeStartStax() throws XMLStreamException;

	abstract protected void writeMetadataStax(Metadata meta)
			throws XMLStreamException;

	abstract protected void writePageStax(Page page) throws XMLStreamException;

	abstract protected void writeEndStax() throws XMLStreamException;

}
