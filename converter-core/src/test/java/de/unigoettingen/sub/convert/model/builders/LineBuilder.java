package de.unigoettingen.sub.convert.model.builders;

import de.unigoettingen.sub.convert.model.Line;

public class LineBuilder {

	private Line line = new Line();
	
	public static LineBuilder line() {
		return new LineBuilder();
	}
	
	public LineBuilder with(WordBuilder word) {
		line.getLineItems().add(word.build());
		return this;
	}
	
	public LineBuilder with(NonWordBuilder nonWord) {
		line.getLineItems().add(nonWord.build());
		return this;
	}
	
	public Line build() {
		return line;
	}
}
