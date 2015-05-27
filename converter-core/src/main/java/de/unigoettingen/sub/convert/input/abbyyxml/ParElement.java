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
