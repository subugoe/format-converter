package de.unigoettingen.sub.convert.impl.abbyyxml;

import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import de.unigoettingen.sub.convert.impl.abbyyxml.AbbyyXMLReader.CurrentPageState;
import de.unigoettingen.sub.convert.model.Char;
import de.unigoettingen.sub.convert.model.LineItem;

class CharParamsElement extends AbstractWordConstructingElement implements AbbyyElement {

	private StartElement tag;
	private XMLEventReader eventReader;

	public CharParamsElement(StartElement tag, XMLEventReader eventReader) {
		this.tag = tag;
		this.eventReader = eventReader;
	}

	@Override
	public void updatePageState(CurrentPageState current)
			throws XMLStreamException {
		this.current = current;
		XMLEvent nextEvent = eventReader.peek();
		if (nextEvent.isCharacters()) {
			processCharParamTag(tag, nextEvent);
			eventReader.nextEvent();
		}
	}

	private void processCharParamTag(StartElement tag, XMLEvent charEvent) {
		char ch = charEvent.asCharacters().getData().charAt(0);
		Char modelChar = new Char();
		processCharAttributes(tag, modelChar);
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
		item.setBottom(new Integer(ch.getBottom()));
	}

	private void correctTopIfNecessary(LineItem item, Char ch) {
		item.setTop(Math.min(ch.getTop(), item.getTop()));
	}

	private void correctBottomIfNecessary(LineItem item, Char ch) {
		item.setBottom(Math.max(item.getBottom(), ch.getBottom()));
	}
	
	private void setTopLeft(LineItem li, Char ch) {
		li.setLeft(new Integer(ch.getLeft()));
		li.setTop(new Integer(ch.getTop()));
	}

	private void setRight(LineItem li, Char ch) {
		li.setRight(new Integer(ch.getRight()));
	}

	private void processCharAttributes(StartElement tag, Char ch) {
		Iterator<?> attributes = tag.getAttributes();
		while (attributes.hasNext()) {
			Attribute attr = (Attribute) attributes.next();
			String attrName = attr.getName().getLocalPart();
			String attrValue = attr.getValue();
			if (attrName.equals("l")) {
				ch.setLeft(new Integer(attrValue));
			} else if (attrName.equals("r")) {
				ch.setRight(new Integer(attrValue));
			} else if (attrName.equals("t")) {
				ch.setTop(new Integer(attrValue));
			} else if (attrName.equals("b")) {
				ch.setBottom(new Integer(attrValue));
			}
		}
	}

}
