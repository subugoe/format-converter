package de.unigoettingen.sub.convert.impl;

import de.unigoettingen.sub.convert.model.Char;
import de.unigoettingen.sub.convert.model.Line;
import de.unigoettingen.sub.convert.model.LineItem;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.NonWord;
import de.unigoettingen.sub.convert.model.Page;
import de.unigoettingen.sub.convert.model.Paragraph;
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

}
