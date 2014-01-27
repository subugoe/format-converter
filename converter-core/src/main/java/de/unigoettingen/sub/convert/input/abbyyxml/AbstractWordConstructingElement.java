package de.unigoettingen.sub.convert.input.abbyyxml;

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