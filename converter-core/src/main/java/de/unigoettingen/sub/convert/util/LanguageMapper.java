package de.unigoettingen.sub.convert.util;

/*

Copyright 2014 SUB Goettingen. All rights reserved.
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.

*/

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
