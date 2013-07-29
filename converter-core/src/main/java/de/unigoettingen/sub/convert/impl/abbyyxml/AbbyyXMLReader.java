package de.unigoettingen.sub.convert.impl.abbyyxml;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
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
import de.unigoettingen.sub.convert.util.NullOutputStream;
import de.unigoettingen.sub.convert.util.XmlAttributesExtractor;

/**
 * 
 * Can read Abbyy XML format versions 6 and 10.
 * Transforms the input into objects of the internal model and sends
 * them page-by-page to the writer.
 *
 */
public class AbbyyXMLReader extends StaxReader {

	private LanguageMapper map = new LanguageMapper();

	static class CurrentPageState {
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
	
	private CurrentPageState current = new CurrentPageState();
	private int pageCounter = 0;
	private PrintStream out = null;

	public AbbyyXMLReader() throws UnsupportedEncodingException {
		out = new PrintStream(new NullOutputStream(), false, "UTF8");
	}
	
	/**
	 * Tells the writer to start the output.
	 */
	@Override
	protected void handleStartDocument()
			throws XMLStreamException {
		checkIfXmlFormatIsCorrect();
		writer.writeStart();
		out.print("Processed pages:");
	}

	private void checkIfXmlFormatIsCorrect()
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

	/**
	 * At the start of the document, sends the available metadata to the writer.
	 * While inside a page, updates the internal model objects, so that they
	 * correspond to the Abbyy XML input page.
	 */
	@Override
	protected void handleStartElement(StartElement startTag)
			throws XMLStreamException {
		String name = startTag.getName().getLocalPart();
		if (name.equals("document")) {
			Metadata meta = createMetadataFromTag(startTag);
			writer.writeMetadata(meta);
		} else {
			XMLEvent nextEvent = eventReader.peek();
			AbbyyElement element = AbbyyFactory.createElementFromTag(startTag, nextEvent);
			element.updatePageState(current);
		}
	}

	private Metadata createMetadataFromTag(StartElement tag) {
		Metadata meta = new Metadata();
		XmlAttributesExtractor extract = new XmlAttributesExtractor(tag);
		meta.setOcrSoftwareName(extract.valueOf("producer"));
		Set<String> extractedLanguages = extract.commaSeparatedValuesOf("mainLanguage", "languages");
		for (String abbyyLanguage : extractedLanguages) {
			String languageId = map.abbyyToIso(abbyyLanguage);
			Language l = new Language();
			l.setLangId(languageId);
			l.setValue(abbyyLanguage);
			meta.getLanguages().add(l);
		}
		return meta;
	}

	/**
	 * Sends the Page model object to the writer.
	 */
	@Override
	protected void handleEndElement(EndElement endTag) {
		String name = endTag.getName().getLocalPart();
		if (name.equals("page")) {
			current.page.setPhysicalNumber(++pageCounter);
			writer.writePage(current.page);
			out.print(" " + pageCounter);
		} else if (name.equals("formatting")) {
			// this has to be done, so that a new lineItem (word/nonWord) can be started
			finishUpLastLineItem();
		}
	}

	private void finishUpLastLineItem() {
		current.lineItem = null;
	}

	/**
	 * Tells the writer to complete the output.
	 */
	@Override
	protected void handleEndDocument() {
		writer.writeEnd();
		out.println();
	}

	@Override
	public void setSystemOutput(PrintStream stream) {
		this.out = stream;
	}

}
