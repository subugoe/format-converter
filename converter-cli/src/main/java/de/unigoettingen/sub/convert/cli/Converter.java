package de.unigoettingen.sub.convert.cli;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

import de.unigoettingen.sub.convert.api.ConvertReader;
import de.unigoettingen.sub.convert.api.ConvertWriter;

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
