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
import de.unigoettingen.sub.convert.model.LineItem;
import de.unigoettingen.sub.convert.util.XmlAttributesExtractor;

class CharParamsElement extends AbstractWordConstructingElement implements AbbyyElement {

	private StartElement tag;
	private XMLEvent nextEvent;

	public CharParamsElement(StartElement tag, XMLEvent nextEvent) {
		this.tag = tag;
		this.nextEvent = nextEvent;
	}

	@Override
	public void updatePageState(CurrentPageState current)
			throws XMLStreamException {
		this.current = current;
		if (nextEvent.isCharacters()) {
			String chars = nextEvent.asCharacters().getData();
			attachCharToLineItem(chars);
		}
	}

	private void attachCharToLineItem(String chars) {
		Char modelChar = new Char();
		copyAttributesTo(modelChar);
		modelChar.setValue(chars);
		char ch = chars.charAt(0);
		boolean isLetterOrDigit = Character.isLetterOrDigit(ch);
		boolean isDash = ch == '-' || ch == '\u2014';
		boolean isWhiteSpace = chars.trim().equals("");
		if (startOfLine() && isLetterOrDigit) {
			beginNewWord();
			setTopLeft(current.word, modelChar);
			setBottom(current.word, modelChar);
		} else if (startOfLine() && !isLetterOrDigit) {
			beginNewNonWord();
			setTopLeft(current.nonWord, modelChar);
			setBottom(current.nonWord, modelChar);
		} else if (inWord() && isDash) {
			// just add the dash to the word
			// dash inside a word is OK
		} else if (inWord() && !isLetterOrDigit) {
			beginNewNonWord();
			setTopLeft(current.nonWord, modelChar);
			setBottom(current.nonWord, modelChar);
		} else if (inNonWord() && isLetterOrDigit) {
			beginNewWord();
			setTopLeft(current.word, modelChar);
			setBottom(current.word, modelChar);
		} else if (inWhiteSpace() && isWhiteSpace) {
			// just add the whitespace
		} else if (!inWhiteSpace() && isWhiteSpace) {
			// it is safer to have whitespaces in a separate
			// nonword, because Abbyy gives us whitespaces
			// with different widths
			beginNewNonWord();
			setTopLeft(current.nonWord, modelChar);
			setBottom(current.nonWord, modelChar);
		} else if (inWhiteSpace() && !isLetterOrDigit) {
			beginNewNonWord();
			setTopLeft(current.nonWord, modelChar);
			setBottom(current.nonWord, modelChar);
		}
		// must be updated after each character as the word/nonword grows
		setRight(current.lineItem, modelChar);
		// find the highest character in word
		correctTopIfNecessary(current.lineItem, modelChar);
		// find the deepest character in word
		correctBottomIfNecessary(current.lineItem, modelChar);
		
		current.lineItem.getCharacters().add(modelChar);

	}
	
	private void setBottom(LineItem item, Char ch) {
		
		item.setBottom(ch.getBottom());
	}

	private void correctTopIfNecessary(LineItem item, Char ch) {
		item.setTop(Math.min(ch.getTop(), item.getTop()));
	}

	private void correctBottomIfNecessary(LineItem item, Char ch) {
		item.setBottom(Math.max(item.getBottom(), ch.getBottom()));
	}
	
	private void setTopLeft(LineItem li, Char ch) {
		li.setLeft(ch.getLeft());
		li.setTop(ch.getTop());
	}

	private void setRight(LineItem li, Char ch) {
		li.setRight(ch.getRight());
	}

	private void copyAttributesTo(Char ch) {
		XmlAttributesExtractor extract = new XmlAttributesExtractor(tag);
		ch.setLeft(extract.integerValueOf("l"));
		ch.setTop(extract.integerValueOf("t"));
		ch.setRight(extract.integerValueOf("r"));
		ch.setBottom(extract.integerValueOf("b"));
	}

}
