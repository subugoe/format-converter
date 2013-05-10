package de.unigoettingen.sub.convert.util;

public class ImageArea {

	private int left;
	private int top;
	private int right;
	private int bottom;
	
	private ImageArea(int left, int top, int right, int bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}
	public static ImageArea createLTRB(int left, int top, int right, int bottom) {
		return new ImageArea(left, top, right, bottom);
	}
	public int getLeft() {
		return left;
	}
	public int getTop() {
		return top;
	}
	public int getRight() {
		return right;
	}
	public int getBottom() {
		return bottom;
	}
}
