package de.unigoettingen.sub.convert.model.builders;

import de.unigoettingen.sub.convert.model.Image;

public class ImageBuilder extends PageItemBuilder {

	private Image image = new Image();
	
	public static ImageBuilder image() {
		return new ImageBuilder();
	}
	
	public ImageBuilder withCoordinatesLTRB(int l, int t, int r, int b) {
		image.setLeft(l);
		image.setTop(t);
		image.setRight(r);
		image.setBottom(b);
		return this;
	}
	
	public Image build() {
		return image;
	}
}
