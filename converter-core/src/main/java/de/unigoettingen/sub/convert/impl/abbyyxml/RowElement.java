package de.unigoettingen.sub.convert.impl.abbyyxml;

import javax.xml.stream.events.StartElement;

import de.unigoettingen.sub.convert.impl.abbyyxml.AbbyyXMLReader.CurrentPageState;
import de.unigoettingen.sub.convert.model.Row;
import de.unigoettingen.sub.convert.model.Table;

class RowElement implements AbbyyElement {

	public RowElement(StartElement tag) {
		
	}

	@Override
	public void updatePageState(CurrentPageState current) {
		Table table = (Table) current.pageItem;
		current.tableRow = new Row();
		table.getRows().add(current.tableRow);
	}

}
