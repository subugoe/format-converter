package de.unigoettingen.sub.convert.impl.abbyyxml;

import java.util.Iterator;

import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

import de.unigoettingen.sub.convert.impl.abbyyxml.AbbyyXMLReader.CurrentPageState;
import de.unigoettingen.sub.convert.model.Page;

class PageElement implements AbbyyElement {

	private Page newPage;
	private StartElement tag;

	public PageElement(StartElement tag) {
		this.tag = tag;
	}

	@Override
	public void updatePageState(CurrentPageState current) {
		newPage = new Page();
		processPageAttributes();
		commitStateChanges(current);
	}

	private void processPageAttributes() {
		Iterator<?> attributes = tag.getAttributes();
		while (attributes.hasNext()) {
			Attribute attr = (Attribute) attributes.next();
			String attrName = attr.getName().getLocalPart();
			if (attrName.equals("width")) {
				newPage.setWidth(new Integer(attr.getValue()));
			} else if (attrName.equals("height")) {
				newPage.setHeight(new Integer(attr.getValue()));
			}
		}
	}

	private void commitStateChanges(CurrentPageState current) {
		current.page = newPage;
	}


}
