package de.unigoettingen.sub.convert.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;


public class ResourceHandler {

	private File[] images;
	
	public File getImageForPage(int pageNumber, File folder) {
		if (images == null) {
			getTifImagesFromFolder(folder);
		}
		if (images.length < pageNumber || pageNumber <= 0) {
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
	
	public void tifToPng(File tifFile, File pngFile) {
		try {
			BufferedImage tifImage = ImageIO.read(tifFile);
			pngFile.getParentFile().mkdirs();
	
			FileOutputStream fos = new FileOutputStream(pngFile);
			ImageIO.write(tifImage, "png", fos);
		} catch (IOException e) {
			throw new IllegalStateException("Error while processing image: " + tifFile.getAbsolutePath(), e);
		}

	}
}