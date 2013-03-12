package de.unigoettingen.sub.convert.impl;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import de.unigoettingen.sub.convert.api.StaxReader;
import de.unigoettingen.sub.convert.model.Char;
import de.unigoettingen.sub.convert.model.Image;
import de.unigoettingen.sub.convert.model.Line;
import de.unigoettingen.sub.convert.model.LineItem;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.NonWord;
import de.unigoettingen.sub.convert.model.Page;
import de.unigoettingen.sub.convert.model.PageItem;
import de.unigoettingen.sub.convert.model.Paragraph;
import de.unigoettingen.sub.convert.model.Table;
import de.unigoettingen.sub.convert.model.TextBlock;
import de.unigoettingen.sub.convert.model.Word;

public class AbbyyXMLReader extends StaxReader {

	private PageItem currentPageItem;
	private Paragraph currentParagraph;
	private Line currentLine;
	private LineItem currentWord;
	private LineItem currentNonWord;
	private LineItem currentLineItem;
	
	@Override
	protected void handleStartDocument(XMLEventReader eventReader) throws XMLStreamException {
		
		// check for the right xml format
		XMLEvent nextEvent = eventReader.peek();
		if (nextEvent.isStartElement()) {
			String rootElement = nextEvent.asStartElement().getName().getLocalPart();
			if (!"document".equals(rootElement)) {
				throw new XMLStreamException("Wrong XML format");
			}
		}
		writer.writeStart();
	}
	@Override
	protected void handleStartElement(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {
		StartElement tag = event.asStartElement();
		String name = tag.getName().getLocalPart();
		if (name.equals("document")) {
			Metadata meta = new Metadata();
			processDocumentAttributes(tag, meta);
			writer.writeMetadata(meta);
		} else if (name.equals("page")) {
			page = new Page();
			processPageAttributes(tag);
		} else if (name.equals("block")) {
			currentPageItem = createPageItem(tag);
			page.getPageItems().add(currentPageItem);
		} else if (name.equals("par")) {
			currentParagraph = new Paragraph();
			if (currentPageItem instanceof TextBlock) {
				TextBlock block = (TextBlock) currentPageItem;
				block.getParagraphs().add(currentParagraph);
			} else if (currentPageItem instanceof Table) {
				//
			}
		} else if (name.equals("line")) {
			currentLine = new Line();
			processLineAttributes(tag, currentLine);
			currentParagraph.getLines().add(currentLine);
		} else if (name.equals("formatting")) {
			// TODO: ein Word template mit lang, font
			XMLEvent nextEvent = eventReader.peek();
			if(nextEvent.isCharacters() && !nextEvent.asCharacters().isWhiteSpace()) {
				processLineWithoutCharParams(nextEvent);
				eventReader.nextEvent();
			}
		} else if (name.equals("charParams")) {
			XMLEvent nextEvent = eventReader.peek();
			if(nextEvent.isCharacters()) {
				processCharParamTag(tag, nextEvent);
				eventReader.nextEvent();
			}
		}
	}
	
	private void processDocumentAttributes(StartElement tag, Metadata meta) {
		Iterator<?> attributes = tag.getAttributes();
		while (attributes.hasNext()) {
			Attribute attr = (Attribute) attributes.next();
			String attrName = attr.getName().getLocalPart();
			if (attrName.equals("producer")) {
				meta.setOcrSoftwareName(attr.getValue());
			} else if (attrName.equals("mainLanguage")) {
				meta.getLanguages().add(attr.getValue());
			} else if (attrName.equals("languages")) {
				meta.getLanguages().add(attr.getValue());
			}
		}
	}
	private void processCharParamTag(StartElement tag, XMLEvent charEvent) {
		char ch = charEvent.asCharacters().getData().charAt(0);
		Char modelChar = new Char();
		processCharAttributes(tag, modelChar);
		modelChar.setValue(""+ch);
		boolean isLetter = Character.isLetter(ch); // TODO: digits also words
		if (startOfLine() && isLetter) {
			switchToWord();
			setTopLeftCoordinate(currentWord, modelChar);
		} else if (startOfLine() && !isLetter) {
			switchToNonWord();
			setTopLeftCoordinate(currentNonWord, modelChar);
		} else if (inWord() && !isLetter) {
			setBottomRightCoordinateIfPresent(currentWord, modelChar);
			switchToNonWord();
			setTopLeftCoordinate(currentNonWord, modelChar);
		} else if (inNonWord() && isLetter) {
			setBottomRightCoordinateIfPresent(currentNonWord, modelChar);
			switchToWord();
			setTopLeftCoordinate(currentWord, modelChar);
		}
		currentLineItem.getCharacters().add(modelChar);

	}
	
	private void setTopLeftCoordinate(LineItem li, Char ch) {
		li.setLeft(new Integer(ch.getLeft()));
		li.setTop(new Integer(ch.getTop()));
	}
	private void setBottomRightCoordinateIfPresent(LineItem li, Char ch) {
		if (ch.getRight() != null && ch.getBottom() != null) {
			li.setRight(new Integer(ch.getRight()));
			li.setBottom(new Integer(ch.getBottom()));
		}
	}
	
	private void processLineWithoutCharParams(XMLEvent charEvent) {
		String chars = charEvent.asCharacters().getData();
		for(char ch : chars.toCharArray()) {
			Char modelChar = new Char();
			modelChar.setValue(""+ch);
			boolean isLetter = Character.isLetter(ch); // TODO: digits
			if (startOfLine() && isLetter) {
				switchToWord();
			} else if (startOfLine() && !isLetter) {
				switchToNonWord();
			} else if (inWord() && !isLetter) {
				switchToNonWord();
			} else if (inNonWord() && isLetter) {
				switchToWord();
			}
			currentLineItem.getCharacters().add(modelChar);
		}
	}
	private boolean startOfLine() {
		return currentLineItem == null;
	}
	private boolean inWord() {
		return currentLineItem instanceof Word;
	}
	private boolean inNonWord() {
		return currentLineItem instanceof NonWord;
	}
	private void switchToWord() {
		currentWord = new Word();
		currentLine.getLineItems().add(currentWord);
		currentLineItem = currentWord;
	}
	private void switchToNonWord() {
		currentNonWord = new NonWord();
		currentLine.getLineItems().add(currentNonWord);
		currentLineItem = currentNonWord;
	}
	
	@Override
	protected void handleEndElement(XMLEvent event) {
		String name = event.asEndElement().getName()
				.getLocalPart();
		if (name.equals("page")) {
			writer.writePage(page);
		} else if (name.equals("formatting")) {	
			if (currentLineItem != null) { // formatting element might have been empty
				int lastIndex = currentLineItem.getCharacters().size() - 1;
				Char lastChar = currentLineItem.getCharacters().get(lastIndex);
				// coordinates for the last word or non-word, since they cannot be handled in the startelement
				setBottomRightCoordinateIfPresent(currentLineItem, lastChar);
				currentLineItem = null;
			}
		}
	}
	@Override
	protected void handleEndDocument() {
		writer.writeEnd();
	}
	
	private void processPageAttributes(StartElement tag) {
		Iterator<?> attributes = tag.getAttributes();
		while (attributes.hasNext()) {
			Attribute attr = (Attribute) attributes.next();
			String attrName = attr.getName().getLocalPart();
			if (attrName.equals("width")) {
				page.setWidth(new Integer(attr.getValue()));
			} else if (attrName.equals("height")) {
				page.setHeight(new Integer(attr.getValue()));
			}
		}
	}
	
	private PageItem createPageItem(StartElement tag) {
		String blockType = tag.getAttributeByName(new QName("blockType")).getValue();
		PageItem item = null;
		if (blockType.equals("Text")) {
			item = new TextBlock();
		} else if (blockType.equals("Table")) {
			item = new Table();
			// TODO: Separator ist von abbyy10
		} else if (blockType.equals("Picture") || blockType.equals("Barcode") || blockType.equals("Separator")
				 || blockType.equals("SeparatorsBox") || blockType.equals("Checkmark") || blockType.equals("GroupCheckmark")) {
			item = new Image();
		}
		processBlockAttributes(tag, item);
		return item;
	}
	
	private void processBlockAttributes(StartElement tag, PageItem item) {
		Iterator<?> attributes = tag.getAttributes();
		while (attributes.hasNext()) {
			Attribute attr = (Attribute) attributes.next();
			String attrName = attr.getName().getLocalPart();
			String attrValue = attr.getValue();
			if (attrName.equals("l")) {
				item.setLeft(new Integer(attrValue));
			} else if (attrName.equals("r")) {
				item.setRight(new Integer(attrValue));
			} else if (attrName.equals("t")) {
				item.setTop(new Integer(attrValue));
			} else if (attrName.equals("b")) {
				item.setBottom(new Integer(attrValue));
			}
		}
	}
	
	private void processLineAttributes(StartElement tag, Line line) {
		Iterator<?> attributes = tag.getAttributes();
		while (attributes.hasNext()) {
			Attribute attr = (Attribute) attributes.next();
			String attrName = attr.getName().getLocalPart();
			String attrValue = attr.getValue();
			if (attrName.equals("l")) {
				line.setLeft(new Integer(attrValue));
			} else if (attrName.equals("r")) {
				line.setRight(new Integer(attrValue));
			} else if (attrName.equals("t")) {
				line.setTop(new Integer(attrValue));
			} else if (attrName.equals("b")) {
				line.setBottom(new Integer(attrValue));
			}
		}
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
