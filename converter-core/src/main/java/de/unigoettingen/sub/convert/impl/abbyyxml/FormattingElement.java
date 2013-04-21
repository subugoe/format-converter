package de.unigoettingen.sub.convert.impl.abbyyxml;

import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import de.unigoettingen.sub.convert.impl.abbyyxml.AbbyyXMLReader.CurrentPageState;
import de.unigoettingen.sub.convert.model.Char;
import de.unigoettingen.sub.convert.model.FontStyleEnum;
import de.unigoettingen.sub.convert.model.Word;
import de.unigoettingen.sub.convert.util.LanguageMapper;

class FormattingElement extends AbstractWordConstructingElement implements AbbyyElement {

	private LanguageMapper map = new LanguageMapper();

	private StartElement tag;
	private XMLEventReader eventReader;

	public FormattingElement(StartElement tag, XMLEventReader eventReader) {
		this.tag = tag;
		this.eventReader = eventReader;
	}

	@Override
	public void updatePageState(CurrentPageState current) throws XMLStreamException {
		this.current = current;
		current.wordAttributeContainer = new Word();
		processFormattingAttributes(tag, current.wordAttributeContainer);
		XMLEvent nextEvent = eventReader.peek();
		if (nextEvent.isCharacters()
				&& !nextEvent.asCharacters().isWhiteSpace()) {
			processLineWithoutCharParams(nextEvent);
			eventReader.nextEvent();
		}


	}

	private void processFormattingAttributes(StartElement tag,
			Word wordContainer) {
		Iterator<?> attributes = tag.getAttributes();
		while (attributes.hasNext()) {
			Attribute attr = (Attribute) attributes.next();
			String attrName = attr.getName().getLocalPart();
			String attrValue = attr.getValue();
			if (attrValue.isEmpty()) {
				continue;
			}
			if (attrName.equals("lang") ) {
				String isoLanguage = map.abbyyToIso(attrValue);
				wordContainer.setLanguage(isoLanguage);
			} else if (attrName.equals("ff")) {
				wordContainer.setFont(attrValue);
			} else if (attrName.equals("fs")) {
				wordContainer.setFontSize(attrValue);
			} else if (attrName.equals("color")) {
				wordContainer.setFontColor(attrValue);
			} else if (attrName.equals("bold") && (attrValue.equals("true") || attrValue.equals("1"))) {
				wordContainer.getFontStyles().add(FontStyleEnum.BOLD);
			} else if (attrName.equals("italic") && (attrValue.equals("true") || attrValue.equals("1"))) {
				wordContainer.getFontStyles().add(FontStyleEnum.ITALIC);
			} else if (attrName.equals("underline") && (attrValue.equals("true") || attrValue.equals("1"))) {
				wordContainer.getFontStyles().add(FontStyleEnum.UNDERLINE);
			}
		}

	}

	private void processLineWithoutCharParams(XMLEvent charEvent) {
		String chars = charEvent.asCharacters().getData();
		for (char ch : chars.toCharArray()) {
			Char modelChar = new Char();
			modelChar.setValue("" + ch);
			boolean isLetterOrDigit = Character.isLetterOrDigit(ch);
			if (startOfLine() && isLetterOrDigit) {
				switchToWord();
			} else if (startOfLine() && !isLetterOrDigit) {
				switchToNonWord();
			} else if (inWord() && !isLetterOrDigit) {
				switchToNonWord();
			} else if (inNonWord() && isLetterOrDigit) {
				switchToWord();
			}
			current.lineItem.getCharacters().add(modelChar);
		}
	}

}
