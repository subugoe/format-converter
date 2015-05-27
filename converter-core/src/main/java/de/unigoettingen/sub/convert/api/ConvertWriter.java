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
import java.util.Map;

import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.Page;

/**
 * 
 * Converts objects of the inner format into an output format.
 * 
 */
public interface ConvertWriter {

	/**
	 * Writes a start header that must precede the actual data, e.g. an XML
	 * declaration.
	 */
	public void writeStart();

	/**
	 * Writes general data about the document, typically once at the top of the
	 * document.
	 * 
	 * @param meta
	 *            Metadata object of the inner format.
	 */
	public void writeMetadata(Metadata meta);

	/**
	 * Writes one page to the document.
	 * 
	 * @param page
	 *            Page object of the inner format.
	 */
	public void writePage(Page page);

	/**
	 * Writes some data that must come after all pages, e.g. closing XML tags.
	 */
	public void writeEnd();

	/**
	 * Sets the stream to which the document will be written.
	 * @param stream Typically a FileOutputStream
	 */
	public void setTarget(OutputStream stream);
	
	/**
	 * Implementing classes can use this method for specific configurations.
	 * @param key
	 * @param value
	 */
	public void addImplementationSpecificOption(String key, String value);
	
	/**
	 * If the concrete class needs special configuration, then it must expose 
	 * the possible options.
	 * @return Map containing supported configuration option keys and short 
	 * descriptions of the respective options.
	 */
	public Map<String, String> getSupportedOptions();

}
