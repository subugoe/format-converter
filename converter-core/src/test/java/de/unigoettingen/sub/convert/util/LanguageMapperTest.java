package de.unigoettingen.sub.convert.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class LanguageMapperTest {


	@Test
	public void test() {
		LanguageMapper mapper = new LanguageMapper();
		assertEquals("language code", "de", mapper.abbyyToIso("GermanStandard"));
		assertEquals("language code", "fr", mapper.abbyyToIso("FrenchStandard"));
		assertEquals("language code", "ru", mapper.abbyyToIso("Russian"));
		assertEquals("language code", "en", mapper.abbyyToIso("EnglishUnitedStates"));
	}

}
