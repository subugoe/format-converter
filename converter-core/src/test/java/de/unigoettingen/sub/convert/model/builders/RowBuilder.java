package de.unigoettingen.sub.convert.model.builders;

import de.unigoettingen.sub.convert.model.Row;

public class RowBuilder {

	private Row row = new Row();
	
	public static RowBuilder row() {
		return new RowBuilder();
	}
	
	public RowBuilder with(CellBuilder cell) {
		row.getCells().add(cell.build());
		return this;
	}
	
	public Row build() {
		return row;
	}
	
}
