package de.unigoettingen.sub.convert.impl.abbyyxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;

/**
 * 
 * Factory for creating Objects that correspond to Abbyy XML Elements.
 *
 */
class AbbyyFactory {

	/**
	 * Creates Objects for Abbyy XML elements depending on the element names
	 * 
	 * @param tag Start element event from Stax
	 * @param eventReader Is used to read character events
	 * @return Object corresponding to an Abbyy XML element, the Object knows how to alter the internal model
	 */
	public static AbbyyElement createElementFromTag(StartElement tag, XMLEventReader eventReader) {
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
