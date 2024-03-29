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

import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;

import de.unigoettingen.sub.convert.input.abbyyxml.AbbyyXMLReader.CurrentPageState;
import de.unigoettingen.sub.convert.model.Image;
import de.unigoettingen.sub.convert.model.PageItem;
import de.unigoettingen.sub.convert.model.Table;
import de.unigoettingen.sub.convert.model.TextBlock;
import de.unigoettingen.sub.convert.util.XmlAttributesExtractor;

class BlockElement implements AbbyyElement {

	private StartElement tag;
	private PageItem newPageItem;

	public BlockElement(StartElement tag) {
		this.tag = tag;
	}

	@Override
	public void updatePageState(CurrentPageState current) {
		newPageItem = createPageItem();
		if (newPageItem != null) {
			commitStateChanges(current);
		}
	}
	
	private PageItem createPageItem() {
		String blockType = tag.getAttributeByName(new QName("blockType"))
				.getValue();
		PageItem item = null;
		if (blockType.equals("Text")) {
			item = new TextBlock();
		} else if (blockType.equals("Table")) {
			item = new Table();
		} else if (blockType.equals("Picture")) {
			item = new Image();
			// following block types are ignored
		} else if (blockType.equals("Barcode") || blockType.equals("Separator")
				|| blockType.equals("SeparatorsBox")
				|| blockType.equals("Checkmark")
				|| blockType.equals("GroupCheckmark")) {
			return null;
		} else {
			return null;
		}
		copyAttributesTo(item);
		return item;
	}

	private void copyAttributesTo(PageItem item) {
		XmlAttributesExtractor extract = new XmlAttributesExtractor(tag);
		item.setLeft(extract.integerValueOf("l"));
		item.setTop(extract.integerValueOf("t"));
		item.setRight(extract.integerValueOf("r"));
		item.setBottom(extract.integerValueOf("b"));
	}

	private void commitStateChanges(CurrentPageState current) {
		current.page.getPageItems().add(newPageItem);
		current.pageItem = newPageItem;
	}

}
