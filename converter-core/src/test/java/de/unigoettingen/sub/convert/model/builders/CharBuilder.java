package de.unigoettingen.sub.convert.model.builders;

import de.unigoettingen.sub.convert.model.Char;

public class CharBuilder {

	private Char character = new Char();
	
	public static CharBuilder character(String ch) {
		CharBuilder builder = new CharBuilder();
		builder.character.setValue(ch);
		return builder;
	}
	
	public Char build() {
		return character;
	}
	
}
