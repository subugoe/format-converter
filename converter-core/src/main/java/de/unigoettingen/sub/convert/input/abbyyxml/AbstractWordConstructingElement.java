package de.unigoettingen.sub.convert.input.abbyyxml;

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

import de.unigoettingen.sub.convert.input.abbyyxml.AbbyyXMLReader.CurrentPageState;
import de.unigoettingen.sub.convert.model.Char;
import de.unigoettingen.sub.convert.model.FontStyleEnum;
import de.unigoettingen.sub.convert.model.LineItem;
import de.unigoettingen.sub.convert.model.NonWord;
import de.unigoettingen.sub.convert.model.Word;

abstract class AbstractWordConstructingElement implements AbbyyElement {

	protected CurrentPageState current;

	protected boolean startOfLine() {
		return current.lineItem == null;
	}

	protected boolean inWord() {
		return current.lineItem instanceof Word;
	}

	protected boolean inNonWord() {
		return current.lineItem instanceof NonWord;
	}
	
	protected boolean inWhiteSpace() {
		for (Char currentChar : current.lineItem.getCharacters()) {
			if (!currentChar.getValue().trim().equals("")) {
				return false;
			}
		}
		return true;
	}

	protected void beginNewWord() {
		current.word = constructUsingWordContainer(new Word());
		current.line.getLineItems().add(current.word);
		current.lineItem = current.word;
	}

	protected void beginNewNonWord() {
		current.nonWord = constructUsingWordContainer(new NonWord());
		current.line.getLineItems().add(current.nonWord);
		current.lineItem = current.nonWord;
	}

	private LineItem constructUsingWordContainer(LineItem itemType) {
		LineItem item;
		if (itemType instanceof Word) {
			item = new Word();
			((Word)item).setLanguage(current.wordAttributeContainer.getLanguage());
		} else {
			item = new NonWord();
		}
		item.setFont(current.wordAttributeContainer.getFont());
		item.setFontSize(current.wordAttributeContainer.getFontSize());
		item.setFontColor(current.wordAttributeContainer.getFontColor());
		for (FontStyleEnum style : current.wordAttributeContainer.getFontStyles()) {
			item.getFontStyles().add(style);
		}
		return item;
	}

}