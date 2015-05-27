package de.unigoettingen.sub.convert.input.abbyyxml;

/*

Copyright 2014 SUB Goettingen. All rights reserved.
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.

*/

import javax.xml.stream.events.StartElement;

import de.unigoettingen.sub.convert.input.abbyyxml.AbbyyXMLReader.CurrentPageState;
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
