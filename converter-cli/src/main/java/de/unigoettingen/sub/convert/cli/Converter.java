package de.unigoettingen.sub.convert.cli;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

import de.unigoettingen.sub.convert.api.ConvertReader;
import de.unigoettingen.sub.convert.api.ConvertWriter;

/**
 * 
 * This class is configured with Spring, see context.xml.
 * You can add any number of readers and writers there.
 * The actual reader and writer are chosen at runtime using the map keys
 * passed into convert() method.
 *
 */
public class Converter {

	private Map<String, ConvertReader> readers;
	private Map<String, ConvertWriter> writers;

	public void setReaders(Map<String, ConvertReader> readers) {
		this.readers = readers;
	}
	public void setWriters(Map<String, ConvertWriter> writers) {
		this.writers = writers;
	}

	public void convert(String inputFormat, InputStream is, String outputFormat, OutputStream os) {
		ConvertReader reader = readers.get(inputFormat);
		ConvertWriter writer = writers.get(outputFormat);
		writer.setTarget(os);
		reader.setWriter(writer);
		reader.read(is);
	}

	public Set<String> getReaderNames() {
		return readers.keySet();
	}

	public Set<String> getWriterNames() {
		return writers.keySet();
	}
	
	
	
}
