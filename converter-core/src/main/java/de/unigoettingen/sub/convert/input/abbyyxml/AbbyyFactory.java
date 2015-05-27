package de.unigoettingen.sub.convert.input.abbyyxml;

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
