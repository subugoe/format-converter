package de.unigoettingen.sub.convert.api;

import java.io.OutputStream;

import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.Page;

public interface ConvertWriter {

	public void writeStart();
	
	public void writeMetadata(Metadata meta);
	
	public void writePage(Page page);
	
	public void writeEnd();
	
	public void setTarget(OutputStream stream);
	
}
