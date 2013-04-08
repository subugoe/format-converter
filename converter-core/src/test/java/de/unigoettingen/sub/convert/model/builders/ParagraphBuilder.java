package de.unigoettingen.sub.convert.model.builders;

import de.unigoettingen.sub.convert.model.Paragraph;

public class ParagraphBuilder {

	private Paragraph par = new Paragraph();
	
	public static ParagraphBuilder paragraph() {
		return new ParagraphBuilder();
	}
	
	public ParagraphBuilder with(LineBuilder line) {
		par.getLines().add(line.build());
		return this;
	}

	public Paragraph build() {
		return par;
	}
}
