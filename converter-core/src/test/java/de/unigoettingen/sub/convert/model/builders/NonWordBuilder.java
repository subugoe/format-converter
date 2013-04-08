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
	
	public NonWord build() {
		return nonWord;
	}

}
