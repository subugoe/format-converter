package de.unigoettingen.sub.convert.model.builders;

import de.unigoettingen.sub.convert.model.Word;

public class WordBuilder {

	private Word word = new Word();
	
	public static WordBuilder word() {
		return new WordBuilder();
	}
	
	public static WordBuilder word(String string) {
		WordBuilder wb = new WordBuilder();
		for (int i = 0; i < string.length(); i++) {
			String ch = "" + string.charAt(i);
			wb.with(CharBuilder.character(ch));
		}
		return wb;
	}
	
	public WordBuilder with(CharBuilder ch) {
		word.getCharacters().add(ch.build());
		return this;
	}
	
	public WordBuilder withCoordinatesLTRB(int left, int top, int right, int bottom) {
		word.setLeft(left);
		word.setTop(top);
		word.setRight(right);
		word.setBottom(bottom);
		return this;
	}
	
	public Word build() {
		return word;
	}
	
}
