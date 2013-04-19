package de.unigoettingen.sub.convert.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;


public class ResourceHandler {

	private File[] images;
	
	public File getImageForPage(int pageNumber, File folder) {
		if (images == null) {
			getTifImagesFromFolder(folder);
		}
		if (images.length < pageNumber) {
			throw new IllegalStateException("No image found for page " + pageNumber);
		}
		return images[pageNumber - 1];
	}
	
	private void getTifImagesFromFolder(File folder) {
		if (!folder.isDirectory()) {
			throw new IllegalStateException("Not a folder: " + folder.getPath());
		}
		FilenameFilter tifFilter = new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				return name.toLowerCase().endsWith(".tif");
			}
		};
		images = folder.listFiles(tifFilter);
		Arrays.sort(images);
	}
}
