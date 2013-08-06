package de.unigoettingen.sub.convert.input.abbyyxml;

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
			char ch = nextEvent.asCharacters().getData().charAt(0);
			attachCharToLineItem(ch);
		}
	}

	private void attachCharToLineItem(char ch) {
		Char modelChar = new Char();
		copyAttributesTo(modelChar);
		modelChar.setValue("" + ch);
		boolean isLetterOrDigit = Character.isLetterOrDigit(ch);
		if (startOfLine() && isLetterOrDigit) {
			switchToWord();
			setTopLeft(current.word, modelChar);
			setBottom(current.word, modelChar);
		} else if (startOfLine() && !isLetterOrDigit) {
			switchToNonWord();
			setTopLeft(current.nonWord, modelChar);
			setBottom(current.nonWord, modelChar);
		} else if (inWord() && !isLetterOrDigit) {
			switchToNonWord();
			setTopLeft(current.nonWord, modelChar);
			setBottom(current.nonWord, modelChar);
		} else if (inNonWord() && isLetterOrDigit) {
			switchToWord();
			setTopLeft(current.word, modelChar);
			setBottom(current.word, modelChar);
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
