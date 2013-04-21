package de.unigoettingen.sub.convert.impl.abbyyxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;

class AbbyyFactory {

	public static AbbyyElement createElementFromEvent(StartElement tag, XMLEventReader eventReader) {
		String tagName = tag.getName().getLocalPart();
		if (tagName.equals("page")) {
			return new PageElement(tag);
		} else if (tagName.equals("block")) {
			return new BlockElement(tag);	
		} else if (tagName.equals("par")) {
			return new ParElement(tag);	
		} else if (tagName.equals("line")) {
			return new LineElement(tag);	
		} else if (tagName.equals("formatting")) {
			return new FormattingElement(tag, eventReader);	
		} else if (tagName.equals("charParams")) {
			return new CharParamsElement(tag, eventReader);	
		} else if (tagName.equals("row")) {
			return new RowElement(tag);	
		} else if (tagName.equals("cell")) {
			return new CellElement(tag);	
		}
		return new UnsupportedElement(tag);
	}

}
