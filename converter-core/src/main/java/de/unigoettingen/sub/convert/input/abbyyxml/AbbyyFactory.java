package de.unigoettingen.sub.convert.input.abbyyxml;

import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

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
	 * @param nextEvent Is used to read character events
	 * @return Object corresponding to an Abbyy XML element, the Object knows how to alter the internal model
	 */
	public static AbbyyElement createElementFromTag(StartElement tag, XMLEvent nextEvent) {
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
			return new FormattingElement(tag, nextEvent);	
		} else if (tagName.equals("charParams")) {
			return new CharParamsElement(tag, nextEvent);	
		} else if (tagName.equals("row")) {
			return new RowElement(tag);	
		} else if (tagName.equals("cell")) {
			return new CellElement(tag);	
		}
		return new UnsupportedElement(tag);
	}

}
