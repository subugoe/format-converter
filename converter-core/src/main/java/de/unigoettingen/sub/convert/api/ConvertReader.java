package de.unigoettingen.sub.convert.api;

import java.io.InputStream;

public interface ConvertReader {

	public void setWriter(ConvertWriter writer);
	public void read(InputStream is);
	
}
