package de.unigoettingen.sub.convert.model.builders;

import de.unigoettingen.sub.convert.model.Table;

public class TableBuilder extends PageItemBuilder {

	private Table table = new Table();
	
	public static TableBuilder table() {
		return new TableBuilder();
	}
	
	public TableBuilder with(RowBuilder row) {
		table.getRows().add(row.build());
		return this;
	}
	
	public TableBuilder with(CellBuilder cell) {
		return this.with(new RowBuilder().with(cell));
	}
	
	public TableBuilder with(PageItemBuilder pageItem) {
		return this.with(new CellBuilder().with(pageItem));
	}
	
	public TableBuilder with(ParagraphBuilder par) {
		return this.with(new TextBlockBuilder().with(par));
	}
	
	public TableBuilder with(LineBuilder line) {
		return this.with(new ParagraphBuilder().with(line));
	}
	
	public TableBuilder with(WordBuilder word) {
		return this.with(new LineBuilder().with(word));
	}
	
	public TableBuilder withCoordinatesLTRB(int l, int t, int r, int b) {
		table.setLeft(l);
		table.setTop(t);
		table.setRight(r);
		table.setBottom(b);
		return this;
	}

	@Override
	public Table build() {
		return table;
	}


}
