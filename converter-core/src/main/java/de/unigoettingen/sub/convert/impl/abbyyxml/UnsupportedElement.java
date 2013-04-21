package de.unigoettingen.sub.convert.impl.abbyyxml;

import javax.xml.stream.events.StartElement;

import de.unigoettingen.sub.convert.impl.abbyyxml.AbbyyXMLReader.CurrentPageState;

class UnsupportedElement implements AbbyyElement {


	public UnsupportedElement(StartElement tag) {
	}

	@Override
	public void updatePageState(CurrentPageState current) {
		// not supported elements are ignored
	}

}
