package de.unigoettingen.sub.convert.impl;

import de.unigoettingen.sub.convert.model.Cell;
import de.unigoettingen.sub.convert.model.Char;
import de.unigoettingen.sub.convert.model.Image;
import de.unigoettingen.sub.convert.model.Line;
import de.unigoettingen.sub.convert.model.LineItem;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.NonWord;
import de.unigoettingen.sub.convert.model.Page;
import de.unigoettingen.sub.convert.model.Paragraph;
import de.unigoettingen.sub.convert.model.Row;
import de.unigoettingen.sub.convert.model.Table;
import de.unigoettingen.sub.convert.model.TextBlock;
import de.unigoettingen.sub.convert.model.Word;

public class ModelObjectFactory {

	public static Metadata createSimpleMetadata() {
		Metadata meta = new Metadata();
		meta.setOcrSoftwareName("Finereader 8.0");
		meta.getLanguages().add("GermanStandard");
		meta.getLanguages().add("GermanStandard");
		return meta;
	}

	public static Page createPageWithOneParagraph() {
		Page page = new Page();
		TextBlock block = new TextBlock();
		Paragraph par = new Paragraph();
		block.getParagraphs().add(par);
		page.getPageItems().add(block);
		return page;
	}

	public static Page createPageWithTwoParagraphs() {
		Page page = createPageWithOneParagraph();
		Paragraph p = new Paragraph();
		((TextBlock)page.getPageItems().get(0)).getParagraphs().add(p);
		return page;
	}

	public static Page createPageWithOneLine() {
		Page page = createPageWithOneParagraph();
		Line line = new Line();
		((TextBlock)page.getPageItems().get(0)).getParagraphs().get(0).getLines().add(line);
		return page;
	}

	public static Page createPageWithOneWord(String input) {
		Page page = createPageWithOneLine();
		Word w = new Word();
		for (char ch : input.toCharArray()) {
			Char c = new Char();
			c.setValue("" + ch);
			w.getCharacters().add(c);
		}
		((TextBlock)page.getPageItems().get(0)).getParagraphs().get(0).getLines().get(0).getLineItems().add(w);
		return page;
	}

	public static Page createPageWithOneNonWord(String input) {
		Page page = createPageWithOneLine();
		NonWord nonw = new NonWord();
		for (char ch : input.toCharArray()) {
			Char c = new Char();
			c.setValue("" + ch);
			nonw.getCharacters().add(c);
		}
		((TextBlock)page.getPageItems().get(0)).getParagraphs().get(0).getLines().get(0).getLineItems().add(nonw);
		return page;
	}

	public static Page createPageWithOneWordAndCoordinates(String input) {
		Page page = createPageWithOneWord(input);
		LineItem item = ((TextBlock)page.getPageItems().get(0)).getParagraphs().get(0).getLines().get(0).getLineItems().get(0);
		item.setLeft(1);
		item.setTop(2);
		item.setRight(3);
		item.setBottom(4);
		return page;
	}

	public static Page createPageWithTable() {
		Page page = new Page();
		Table table = new Table();
		Row row = new Row();
		Cell cell = new Cell();
		TextBlock block = new TextBlock();
		Paragraph par = new Paragraph();
		Line line = new Line();
		Word word = new Word();
		Char ch = new Char();
		ch.setValue("a");
		
		word.getCharacters().add(ch);
		line.getLineItems().add(word);
		par.getLines().add(line);
		block.getParagraphs().add(par);
		cell.setContent(block);
		row.getCells().add(cell);
		table.getRows().add(row);
		page.getPageItems().add(table);
		return page;
	}

	public static Page createPageWithImage() {
		Page page = new Page();
		Image image = new Image();
		image.setLeft(1);
		image.setTop(2);
		image.setRight(3);
		image.setBottom(4);
		page.getPageItems().add(image);
		return page;
	}

}
