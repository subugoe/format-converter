package de.unigoettingen.sub.convert.impl.abbyyxml;

import javax.xml.stream.XMLStreamException;

import de.unigoettingen.sub.convert.impl.abbyyxml.AbbyyXMLReader.CurrentPageState;

interface AbbyyElement {

	void updatePageState(CurrentPageState current) throws XMLStreamException;

}
