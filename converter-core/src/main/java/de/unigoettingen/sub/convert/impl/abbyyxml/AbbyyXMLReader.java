package de.unigoettingen.sub.convert.impl.abbyyxml;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import de.unigoettingen.sub.convert.api.StaxReader;
import de.unigoettingen.sub.convert.model.Cell;
import de.unigoettingen.sub.convert.model.Language;
import de.unigoettingen.sub.convert.model.Line;
import de.unigoettingen.sub.convert.model.LineItem;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.Page;
import de.unigoettingen.sub.convert.model.PageItem;
import de.unigoettingen.sub.convert.model.Paragraph;
import de.unigoettingen.sub.convert.model.Row;
import de.unigoettingen.sub.convert.model.Word;
import de.unigoettingen.sub.convert.util.LanguageMapper;

/**
 * 
 * Can read Abbyy XML format versions 6 and 10.
 * Transforms the input into the internal model.
 *
 */
public class AbbyyXMLReader extends StaxReader {

	private LanguageMapper map = new LanguageMapper();

	class CurrentPageState {
		Page page;
		PageItem pageItem;
		Paragraph paragraph;
		Line line;
		LineItem word;
		LineItem nonWord;
		LineItem lineItem;
		Row tableRow;
		Cell tableCell;
		Word wordAttributeContainer;
	}
	
	CurrentPageState current = new CurrentPageState();

	@Override
	protected void handleStartDocument(XMLEventReader eventReader)
			throws XMLStreamException {
		checkIfXmlFormatIsCorrect(eventReader);
		writer.writeStart();
	}

	private void checkIfXmlFormatIsCorrect(XMLEventReader eventReader)
			throws XMLStreamException {
		XMLEvent nextEvent = eventReader.peek();
		if (nextEvent.isStartElement()) {
			String rootElement = nextEvent.asStartElement().getName()
					.getLocalPart();
			if (!"document".equals(rootElement)) {
				throw new XMLStreamException("Wrong XML format");
			}
		}
	}

	@Override
	protected void handleStartElement(XMLEvent event, XMLEventReader eventReader)
			throws XMLStreamException {
		StartElement tag = event.asStartElement();
		String name = tag.getName().getLocalPart();
		if (name.equals("document")) {
			Metadata meta = new Metadata();
			processDocumentAttributes(tag, meta);
			writer.writeMetadata(meta);
		} else {
			AbbyyElement element = AbbyyFactory.createElementFromEvent(tag, eventReader);
			element.updatePageState(current);
		}
	}

	private void processDocumentAttributes(StartElement tag, Metadata meta) {
		Iterator<?> attributes = tag.getAttributes();
		Set<String> processedLanguages = new HashSet<String>();
		while (attributes.hasNext()) {
			Attribute attr = (Attribute) attributes.next();
			String attrValue = attr.getValue();
			if (attrValue.isEmpty()) {
				continue;
			}
			String attrName = attr.getName().getLocalPart();
			if (attrName.equals("producer")) {
				meta.setOcrSoftwareName(attrValue);
			} else if (attrName.equals("mainLanguage") || attrName.equals("languages")) {
				String[] splitLangs = attrValue.split(",");
				for(String lang : splitLangs) {
					processedLanguages.add(lang);
				}
			}
		}
		for (String abbyyLanguage : processedLanguages) {
			String languageId = map.abbyyToIso(abbyyLanguage);
			String languageDescription = abbyyLanguage;
			Language l = new Language();
			l.setLangId(languageId);
			l.setValue(languageDescription);
			meta.getLanguages().add(l);
		}
	}

	@Override
	protected void handleEndElement(XMLEvent event) {
		String name = event.asEndElement().getName().getLocalPart();
		if (name.equals("page")) {
			writer.writePage(current.page);
		} else if (name.equals("formatting")) {
			finishUpLastLineItem();
		}
	}

	private void finishUpLastLineItem() {
		current.lineItem = null;
	}

	@Override
	protected void handleEndDocument() {
		writer.writeEnd();
	}

}
