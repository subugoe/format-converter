package de.unigoettingen.sub.convert.input.abbyyxml;

import javax.xml.stream.events.StartElement;

import de.unigoettingen.sub.convert.input.abbyyxml.AbbyyXMLReader.CurrentPageState;
import de.unigoettingen.sub.convert.model.Cell;

class CellElement implements AbbyyElement {

	public CellElement(StartElement tag) {
		
	}

	@Override
	public void updatePageState(CurrentPageState current) {
		current.tableCell = new Cell();
		current.tableRow.getCells().add(current.tableCell);
	}

}
