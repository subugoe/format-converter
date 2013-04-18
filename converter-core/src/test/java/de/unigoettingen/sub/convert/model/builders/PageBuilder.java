package de.unigoettingen.sub.convert.model.builders;


import de.unigoettingen.sub.convert.model.Page;

public class PageBuilder {

	private Page page = new Page();
	
	public static PageBuilder page() {
		return new PageBuilder();
	}
	
	public static PageBuilder pageA4() {
		return new PageBuilder().withWidth(595).withHeight(842);
	}
	
	public PageBuilder withHeight(int height) {
		page.setHeight(height);
		return this;
	}

	public PageBuilder withWidth(int width) {
		page.setWidth(width);
		return this;
	}

	public PageBuilder with(PageItemBuilder item) {
		page.getPageItems().add(item.build());
		return this;
	}
	
	public PageBuilder with(ParagraphBuilder par) {
		
		return this.with(new TextBlockBuilder().with(par));
	}
	
	public PageBuilder with(LineBuilder line) {
		return this.with(new ParagraphBuilder().with(line));
	}
	
	public PageBuilder with(WordBuilder word) {
		return this.with(new LineBuilder().with(word));
	}
	
	public PageBuilder with(NonWordBuilder nonWord) {
		return this.with(new LineBuilder().with(nonWord));
	}
	
	public Page build() {
		return page;
	}
	
}
