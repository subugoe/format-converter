package de.unigoettingen.sub.convert.impl.abbyyxml;

import javax.xml.stream.events.StartElement;

import de.unigoettingen.sub.convert.impl.abbyyxml.AbbyyXMLReader.CurrentPageState;
import de.unigoettingen.sub.convert.model.Page;
import de.unigoettingen.sub.convert.util.XmlAttributesExtractor;

class PageElement implements AbbyyElement {

	private Page newPage;
	private StartElement tag;

	public PageElement(StartElement tag) {
		this.tag = tag;
	}

	@Override
	public void updatePageState(CurrentPageState current) {
		newPage = new Page();
		copyAttributesTo(newPage);
		commitStateChanges(current);
	}

	private void copyAttributesTo(Page page) {
		XmlAttributesExtractor extract = new XmlAttributesExtractor(tag);
		page.setWidth(extract.integerValueOf("width"));
		page.setHeight(extract.integerValueOf("height"));
	}

	private void commitStateChanges(CurrentPageState current) {
		current.page = newPage;
	}


}
