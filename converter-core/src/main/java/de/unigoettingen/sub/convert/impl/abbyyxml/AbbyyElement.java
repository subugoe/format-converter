package de.unigoettingen.sub.convert.impl.abbyyxml;

import javax.xml.stream.XMLStreamException;

import de.unigoettingen.sub.convert.impl.abbyyxml.AbbyyXMLReader.CurrentPageState;

/**
 * 
 * Represents an element in Abbyy XML. Knows how to deal with the internal model 
 * according to the concrete Abbyy element Implementation.
 *
 */

interface AbbyyElement {

	/**
	 * Can update the current state of the internal model objects.
	 * 
	 * @param current The current state of a model Page, containing the Page itself and its contents
	 * @throws XMLStreamException
	 */
	void updatePageState(CurrentPageState current) throws XMLStreamException;

}
