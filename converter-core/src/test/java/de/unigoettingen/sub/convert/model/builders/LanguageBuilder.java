package de.unigoettingen.sub.convert.model.builders;

import de.unigoettingen.sub.convert.model.Language;

public class LanguageBuilder {

	private Language lang = new Language();

	public static LanguageBuilder language(String langValue) {
		LanguageBuilder builder = new LanguageBuilder();
		builder.lang.setValue(langValue);
		return builder;
	}

	public LanguageBuilder withLangId(String langId) {
		lang.setLangId(langId);
		return this;
	}
	
	public Language build() {
		return lang;
	}
}
