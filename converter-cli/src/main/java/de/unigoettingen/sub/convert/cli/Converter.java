package de.unigoettingen.sub.convert.cli;

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
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashSet;
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
	private PrintStream out;

	public void setReaders(Map<String, ConvertReader> readers) {
		this.readers = readers;
	}
	public void setWriters(Map<String, ConvertWriter> writers) {
		this.writers = writers;
	}
	
	public void setSysOut(PrintStream out) {
		this.out = out;
	}

	public void convert(String inputFormat, InputStream is, String outputFormat, OutputStream os, Map<String, String> writerOptions) {
		ConvertReader reader = readers.get(inputFormat);
		if (out != null) {
			reader.setSystemOutput(out);
		}
		ConvertWriter writer = writers.get(outputFormat);
		writer.setTarget(os);
		for (Map.Entry<String, String> option : writerOptions.entrySet()) {
			writer.addImplementationSpecificOption(option.getKey(), option.getValue());
		}
		reader.setWriter(writer);
		reader.read(is);
	}

	public Set<String> getReaderNames() {
		return readers.keySet();
	}

	public Set<String> getWriterNames() {
		return writers.keySet();
	}
	
	public String constructHelpForOutputOptions() {
		StringBuilder allWritersOptions = new StringBuilder();
		for (String writerName : getWriterNames()) {
			Set<String> optionsOfOneWriter = getOptionDescriptionsForWriter(writerName);
			if (!optionsOfOneWriter.isEmpty()) {
				allWritersOptions.append("-- for output format '").append(writerName).append("' --\n");
				for (String option : optionsOfOneWriter) {
					allWritersOptions.append(option).append("\n");
				}
			}
		}
		return allWritersOptions.toString();
	}
	
	private Set<String> getOptionDescriptionsForWriter(String writerName) {
		Set<String> descriptions = new HashSet<String>();
		ConvertWriter writer = writers.get(writerName);
		Map<String, String> options = writer.getSupportedOptions();
		
		for (Map.Entry<String, String> entry : options.entrySet()) {
			descriptions.add(entry.getKey() + "=" + entry.getValue());
		}
		
		return descriptions;
	}
	
	public boolean unknownInput(String inFormat) {
		return !getReaderNames().contains(inFormat);
	}
	
	public boolean unknownOutput(String outFormat) {
		return !getWriterNames().contains(outFormat);
	}
	
	
}
