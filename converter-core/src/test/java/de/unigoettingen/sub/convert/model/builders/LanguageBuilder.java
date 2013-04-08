package de.unigoettingen.sub.convert.model.builders;

import de.unigoettingen.sub.convert.model.Language;

public class LanguageBuilder {

	private Language lang = new Language();

	public static LanguageBuilder language() {
		return new LanguageBuilder();
	}

	public LanguageBuilder withLangId(String langId) {
		lang.setLangId(langId);
		return this;
	}
	public LanguageBuilder withValue(String value) {
		lang.setValue(value);
		return this;
	}
	
	public Language build() {
		return lang;
	}
}
