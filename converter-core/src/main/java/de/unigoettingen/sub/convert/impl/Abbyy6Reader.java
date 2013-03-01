package de.unigoettingen.sub.convert.impl;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import de.unigoettingen.sub.convert.api.StaxReader;
import de.unigoettingen.sub.convert.model.Image;
import de.unigoettingen.sub.convert.model.Line;
import de.unigoettingen.sub.convert.model.Page;
import de.unigoettingen.sub.convert.model.PageItem;
import de.unigoettingen.sub.convert.model.Paragraph;
import de.unigoettingen.sub.convert.model.Table;
import de.unigoettingen.sub.convert.model.TextBlock;

public class Abbyy6Reader extends StaxReader {

	private PageItem currentPageItem;
	private Paragraph currentParagraph;
	private Line currentLine;
	
	@Override
	protected void handleStartDocument() {
		writer.writeStart();
	}
	@Override
	protected void handleStartElement(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {
		StartElement tag = event.asStartElement();
		String name = tag.getName().getLocalPart();
		if (name.equals("document")) {
			writer.writeMetadata();
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
		} else if (name.equals("charParams")) {
			XMLEvent nextEvent = eventReader.peek();
			if(nextEvent.isCharacters()) {
				//page.setTextBlock(page.getTextBlock() + "\nchars/ " + nextEvent.asCharacters().toString() + " /chars");
				eventReader.nextEvent();
			}
		}
	}
	@Override
	protected void handleEndElement(XMLEvent event) {
		String name = event.asEndElement().getName()
				.getLocalPart();
		if (name.equals("page")) {
			writer.writePage(page);
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
		} else if (blockType.equals("Picture") || blockType.equals("Barcode")) {
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
	
}
