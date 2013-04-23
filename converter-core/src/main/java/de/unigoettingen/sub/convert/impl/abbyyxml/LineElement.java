package de.unigoettingen.sub.convert.impl.abbyyxml;

import javax.xml.stream.events.StartElement;

import de.unigoettingen.sub.convert.impl.abbyyxml.AbbyyXMLReader.CurrentPageState;
import de.unigoettingen.sub.convert.model.Line;
import de.unigoettingen.sub.convert.util.XmlAttributesExtractor;

class LineElement implements AbbyyElement {

	private StartElement tag;
	private Line newLine;

	public LineElement(StartElement tag) {
		this.tag = tag;
	}

	@Override
	public void updatePageState(CurrentPageState current) {
		newLine = new Line();
		copyAttributesTo(newLine);
		commitStateChanges(current);
	}

	private void copyAttributesTo(Line line) {
		XmlAttributesExtractor extract = new XmlAttributesExtractor(tag);
		line.setLeft(extract.integerValueOf("l"));
		line.setTop(extract.integerValueOf("t"));
		line.setRight(extract.integerValueOf("r"));
		line.setBottom(extract.integerValueOf("b"));
		line.setBaseline(extract.integerValueOf("baseline"));
	}

	private void commitStateChanges(CurrentPageState current) {
		current.line = newLine;
		current.paragraph.getLines().add(newLine);
	}

}
