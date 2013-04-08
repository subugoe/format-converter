package de.unigoettingen.sub.convert.model.builders;

import de.unigoettingen.sub.convert.model.TextBlock;

public class TextBlockBuilder extends PageItemBuilder {

	private TextBlock textBlock = new TextBlock();
	
	public static TextBlockBuilder textBlock() {
		return new TextBlockBuilder();
	}
	
	public TextBlockBuilder with(ParagraphBuilder par) {
		textBlock.getParagraphs().add(par.build());
		return this;
	}
	
	public TextBlock build() {
		return textBlock;
	}
}
