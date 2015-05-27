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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import de.unigoettingen.sub.convert.input.abbyyxml.AbbyyXMLReader.CurrentPageState;
import de.unigoettingen.sub.convert.model.Char;
import de.unigoettingen.sub.convert.model.FontStyleEnum;
import de.unigoettingen.sub.convert.model.Word;
import de.unigoettingen.sub.convert.util.LanguageMapper;
import de.unigoettingen.sub.convert.util.XmlAttributesExtractor;

class FormattingElement extends AbstractWordConstructingElement implements AbbyyElement {

	private LanguageMapper map = new LanguageMapper();

	private StartElement tag;
	private XMLEvent nextEvent;

	public FormattingElement(StartElement tag, XMLEvent nextEvent) {
		this.tag = tag;
		this.nextEvent = nextEvent;
	}

	@Override
	public void updatePageState(CurrentPageState current) throws XMLStreamException {
		this.current = current;
		current.wordAttributeContainer = new Word();
		processFormattingAttributes(current.wordAttributeContainer);
		if (isTextWithoutCoordinates(nextEvent)) {
			String plainText = nextEvent.asCharacters().getData();
			tokenizeText(plainText);
		}
	}

	private boolean isTextWithoutCoordinates(XMLEvent nextEvent) {
		return nextEvent.isCharacters()
				&& !nextEvent.asCharacters().isWhiteSpace();
	}

	private void processFormattingAttributes(Word wordContainer) {
		XmlAttributesExtractor extract = new XmlAttributesExtractor(tag);
		
		String isoLanguage = map.abbyyToIso(extract.valueOf("lang"));
		wordContainer.setLanguage(isoLanguage);
		wordContainer.setFont(extract.valueOf("ff"));
		wordContainer.setFontSize(extract.valueOf("fs"));
		wordContainer.setFontColor(extract.valueOf("color"));
		if(extract.booleanValueOf("bold")) {
			wordContainer.getFontStyles().add(FontStyleEnum.BOLD);
		}
		if(extract.booleanValueOf("italic")) {
			wordContainer.getFontStyles().add(FontStyleEnum.ITALIC);
		}
		if(extract.booleanValueOf("underline")) {
			wordContainer.getFontStyles().add(FontStyleEnum.UNDERLINE);
		}
	}

	private void tokenizeText(String text) {
		for (char ch : text.toCharArray()) {
			Char modelChar = new Char();
			modelChar.setValue("" + ch);
			boolean isLetterOrDigit = Character.isLetterOrDigit(ch);
			if (startOfLine() && isLetterOrDigit) {
				beginNewWord();
			} else if (startOfLine() && !isLetterOrDigit) {
				beginNewNonWord();
			} else if (inWord() && !isLetterOrDigit) {
				beginNewNonWord();
			} else if (inNonWord() && isLetterOrDigit) {
				beginNewWord();
			}
			current.lineItem.getCharacters().add(modelChar);
		}
	}

}
