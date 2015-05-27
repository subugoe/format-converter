package de.unigoettingen.sub.convert.util;

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
