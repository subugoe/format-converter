package de.unigoettingen.sub.convert.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

public class XmlAttributesExtractor {
	
	Map<String, String> attributes = new HashMap<String, String>();

	public XmlAttributesExtractor(StartElement element) {
		Iterator<?> attrs = element.getAttributes();
		while (attrs.hasNext()) {
			Attribute attr = (Attribute) attrs.next();
			String attrName = attr.getName().getLocalPart();
			String attrValue = attr.getValue();
			
			attributes.put(attrName, attrValue);
		}
	}
	
	public String valueOf(String attributeName) {
		return attributes.get(attributeName);
	}
	
	public Integer integerValueOf(String attributeName) {
		String value = attributes.get(attributeName);
		if (value == null) {
			return null;
		}
		return new Integer(value);
	}

	public boolean booleanValueOf(String attributeName) {
		String value = attributes.get(attributeName);
		if ("true".equals(value) || "1".equals(value)) {
			return true;
		}
		return false;
	}

	public Set<String> commaSeparatedValuesOf(String... attributeNames) {
		Set<String> values = new HashSet<String>();
		for (String attrName : attributeNames) {
			String attrValue = attributes.get(attrName);
			if (attrValue == null || attrValue.isEmpty()) {
				continue;
			}
			String[] tokenized = attrValue.split(",");
			for (String token : tokenized) {
				values.add(token);
			}
		}
		return values;
	}
	
}
