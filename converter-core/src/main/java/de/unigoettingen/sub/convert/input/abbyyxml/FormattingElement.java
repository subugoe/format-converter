package de.unigoettingen.sub.convert.input.abbyyxml;

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