package de.unigoettingen.sub.convert.util;

import java.util.HashMap;
import java.util.Map;

public class LanguageMapper {

	private Map<String, String> abbyyToIso;
	
	public String abbyyToIso(String abbyyLanguage) {
		if (abbyyToIso == null) {
			abbyyToIso = new HashMap<String, String>();
			abbyyToIso.put("GermanStandard", "de");
			abbyyToIso.put("FrenchStandard", "fr");
			abbyyToIso.put("Russian", "ru");
			abbyyToIso.put("EnglishUnitedStates", "en");
		}
		return abbyyToIso.get(abbyyLanguage);
	}

}
