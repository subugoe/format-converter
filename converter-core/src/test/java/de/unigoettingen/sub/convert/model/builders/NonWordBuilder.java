package de.unigoettingen.sub.convert.model.builders;

import de.unigoettingen.sub.convert.model.NonWord;

public class NonWordBuilder {

	private NonWord nonWord = new NonWord();
	
	public static NonWordBuilder nonWord() {
		return new NonWordBuilder();
	}
	
	public static NonWordBuilder nonWord(String string) {
		NonWordBuilder nwb = new NonWordBuilder();
		for (int i = 0; i < string.length(); i++) {
			String ch = "" + string.charAt(i);
			nwb.with(CharBuilder.character(ch));
		}
		return nwb;
	}
	
	public NonWordBuilder with(CharBuilder ch) {
		nonWord.getCharacters().add(ch.build());
		return this;
	}
	
	public NonWordBuilder withCoordinatesLTRB(int left, int top, int right, int bottom) {
		nonWord.setLeft(left);
		nonWord.setTop(top);
		nonWord.setRight(right);
		nonWord.setBottom(bottom);
		return this;
	}

	
	public NonWord build() {
		return nonWord;
	}

}
