package de.unigoettingen.sub.convert.api;

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
	
	public void addImplementationSpecificOption(String key, String value);
	public Map<String, String> getSupportedOptions();

}
