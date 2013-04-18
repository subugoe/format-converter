package de.unigoettingen.sub.convert.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ResourceHandler {

	private File[] images;
	
	public File getImageForPage(int pageNumber, File folder) {
		if (images == null) {
			getImagesFromFolder(folder);
		}
		return images[pageNumber - 1];
	}
	
	private void getImagesFromFolder(File folder) {
		
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
