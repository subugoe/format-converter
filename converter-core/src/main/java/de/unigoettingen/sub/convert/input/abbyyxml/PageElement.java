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
