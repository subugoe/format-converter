package de.unigoettingen.sub.convert.api;

import java.io.InputStream;

/**
 * 
 * A general reader that can read a stream of any format.
 * 
 */
public interface ConvertReader {

	/**
	 * Allows simple injection of any writer. Useful in tests and in Spring
	 * configurations.
	 * 
	 * @param writer
	 */
	public void setWriter(ConvertWriter writer);

	public void read(InputStream is);

}
