package de.unigoettingen.sub.convert.model.builders;

import de.unigoettingen.sub.convert.model.Cell;

public class CellBuilder {

	private Cell cell = new Cell();
	
	public static CellBuilder cell() {
		return new CellBuilder();
	}
	
	public CellBuilder with(PageItemBuilder pageItem) {
		cell.setContent(pageItem.build());
		return this;
	}
	
	public Cell build() {
		return cell;
	}
}
