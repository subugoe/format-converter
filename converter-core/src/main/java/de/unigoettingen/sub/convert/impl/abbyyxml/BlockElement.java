package de.unigoettingen.sub.convert.impl.abbyyxml;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

import de.unigoettingen.sub.convert.impl.abbyyxml.AbbyyXMLReader.CurrentPageState;
import de.unigoettingen.sub.convert.model.Image;
import de.unigoettingen.sub.convert.model.PageItem;
import de.unigoettingen.sub.convert.model.Table;
import de.unigoettingen.sub.convert.model.TextBlock;
import de.unigoettingen.sub.convert.model.WithCoordinates;

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
		}
		processCoordinateAttributes(item);
		return item;
	}

	private void processCoordinateAttributes(WithCoordinates item) {
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

	private void commitStateChanges(CurrentPageState current) {
		current.page.getPageItems().add(newPageItem);
		current.pageItem = newPageItem;
	}

}
