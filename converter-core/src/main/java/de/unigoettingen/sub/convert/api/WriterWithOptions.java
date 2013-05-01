package de.unigoettingen.sub.convert.api;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class WriterWithOptions implements ConvertWriter {

	private final static Logger LOGGER = LoggerFactory.getLogger(WriterWithOptions.class);
	protected OutputStream output;
	protected Map<String, String> supportedOptions = new HashMap<String, String>();
	protected Map<String, String> actualOptions = new HashMap<String, String>();


	@Override
	public void setTarget(OutputStream stream) {
		output = stream;
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


}
