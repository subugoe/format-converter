package de.unigoettingen.sub.convert.impl.abbyyxml;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

import de.unigoettingen.sub.convert.impl.abbyyxml.AbbyyXMLReader.CurrentPageState;
import de.unigoettingen.sub.convert.model.Line;

class LineElement implements AbbyyElement {

	private StartElement tag;
	private Line newLine;

	public LineElement(StartElement tag) {
		this.tag = tag;
	}

	@Override
	public void updatePageState(CurrentPageState current) {
		newLine = new Line();
		processAttributes(newLine);
		Integer baseline = new Integer(tag.getAttributeByName(new QName("baseline")).getValue());
		newLine.setBaseline(baseline);
		commitStateChanges(current);
	}

	private void processAttributes(Line line) {
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

	private void commitStateChanges(CurrentPageState current) {
		current.line = newLine;
		current.paragraph.getLines().add(newLine);
	}

}
