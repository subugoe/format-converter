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
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains common behavior for writers. You can extend from this class instead of 
 * implementing ConvertWriter.
 * @author dennis
 *
 */
public abstract class WriterWithOptions implements ConvertWriter {

	private final static Logger LOGGER = LoggerFactory.getLogger(WriterWithOptions.class);
	
	/**
	 * The stream to which the concrete writer must write the produced output.
	 */
	protected OutputStream output;
	
	/**
	 * Contains the option keys and descriptions (not the option values).
	 * Should be used to inform the clients of the possible specific options.
	 * The concrete writer should add option entries in the constructor.
	 */
	protected Map<String, String> supportedOptions = new HashMap<String, String>();
	
	/**
	 * Contains options that are set/chosen. Key-value pairs that configure the concrete class.
	 */
	protected Map<String, String> setOptions = new HashMap<String, String>();

	/**
	 * Sets the stream to which the output will be written.
	 */
	@Override
	public void setTarget(OutputStream stream) {
		output = stream;
	}

	/**
	 * Adds a key-value pair that the concrete class can understand. Use this for 
	 * additional configuration, e.g., paths to specific files.
	 */
	@Override
	public void addImplementationSpecificOption(String key, String value) {
		if (supportedOptions.get(key) != null) {
			setOptions.put(key, value);
		} else {
			LOGGER.warn("The option is not supported: " + key);
		}
	}
	
	/**
	 * Returns key-description pairs for all options that the concrete class understands.
	 */
	@Override
	public Map<String, String> getSupportedOptions() {
		return new HashMap<String, String>(supportedOptions);
	}

	protected void checkOutputStream() {
		if (output == null) {
			throw new IllegalStateException("The output target is not set");
		}

	}

}
