package de.unigoettingen.sub.convert.impl.abbyyxml;

import javax.xml.stream.events.StartElement;

import de.unigoettingen.sub.convert.impl.abbyyxml.AbbyyXMLReader.CurrentPageState;
import de.unigoettingen.sub.convert.model.Paragraph;
import de.unigoettingen.sub.convert.model.Table;
import de.unigoettingen.sub.convert.model.TextBlock;

class ParElement implements AbbyyElement {

	private Paragraph newParagraph;

	public ParElement(StartElement tag) {
	}

	@Override
	public void updatePageState(CurrentPageState current) {
		newParagraph = new Paragraph();
		commitStateChanges(current);
	}

	private void commitStateChanges(CurrentPageState current) {
		current.paragraph = newParagraph;
		if (current.pageItem instanceof TextBlock) {
			TextBlock block = (TextBlock) current.pageItem;
			block.getParagraphs().add(newParagraph);
		} else if (current.pageItem instanceof Table) {
			TextBlock tableBlock = new TextBlock();
			current.tableCell.setContent(tableBlock);
			tableBlock.getParagraphs().add(newParagraph);
		}
	}

	
}
