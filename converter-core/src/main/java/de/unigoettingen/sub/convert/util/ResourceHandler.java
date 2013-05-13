package de.unigoettingen.sub.convert.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;


public class ResourceHandler {

	private File[] images;
	private int pageNumber = 0;
	private int subimageCounter = 0;
	
	private List<File> htmls = new ArrayList<File>();
	private List<File> scans = new ArrayList<File>();
	private File tempDir = new File(System.getProperty("java.io.tmpdir"));

	public int getCurrentPageNumber() {
		return pageNumber;
	}
	
	public void incrementPageNumber() {
		pageNumber++;
	}
	
	public void incrementSubimageCounter() {
		subimageCounter++;
	}
	
	public String getNameForCurrentScan() {
		return "scan" + pageNumber + ".png";
	}
	
	public String getNameForCurrentScan(String parentFolder) {
		return parentFolder + "/" + getNameForCurrentScan();
	}
	
	public String getNameForCurrentSubimage() {
		return "subimage" + pageNumber + "-" + subimageCounter + ".png";
	}
	
	public String getNameForCurrentSubimage(String parentFolder) {
		return parentFolder + "/" + getNameForCurrentSubimage();
	}
	
	public void addCurrentHtmlToTemp() {
		File tempHtml = new File(tempDir, "page" + pageNumber + ".html");
		htmls.add(tempHtml);
	}
	
	public void addCurrentScanToTemp() {
		File tempHtml = new File(tempDir, getNameForCurrentScan());
		scans.add(tempHtml);
	}
	
	public File getNextTempHtmlFile() {
		return new File(tempDir, "page" + (pageNumber+1) + ".html");
	}
	
	public InputStream getHtmlPage(int pageNumber) throws FileNotFoundException {
		int index = pageNumber - 1;
		return new FileInputStream(htmls.get(index));
	}
	
	public InputStream getScanForPage(int pageNumber) throws FileNotFoundException {
		int index = pageNumber - 1;
		return new FileInputStream(scans.get(index));
	}
	
	public void deleteTempFiles() {
		for (File tempHtml : htmls) {
			tempHtml.delete();
		}
		for (File tempImage : scans) {
			tempImage.delete();
		}
	}

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
			ImageArea completeImage = ImageArea.createLTRB(0, 0, tifImage.getWidth(), tifImage.getHeight());
			tifToPngAndCut(tifFile, pngFile, completeImage);
		} catch (IOException e) {
			throw new IllegalStateException("Error while processing image: " + tifFile.getAbsolutePath(), e);
		}

	}

	public void tifToPngAndCut(File tifFile, File pngFile, ImageArea area) {
		try {
			BufferedImage tifImage = ImageIO.read(tifFile);
			pngFile.getParentFile().mkdirs();
	
			FileOutputStream fos = new FileOutputStream(pngFile);
			int x = area.getLeft();
			int y = area.getTop();
			int width = area.getRight() - area.getLeft();
			int height = area.getBottom() - area.getTop();
			ImageIO.write(tifImage.getSubimage(x, y, width, height), "png", fos);
		} catch (IOException e) {
			throw new IllegalStateException("Error while processing image: " + tifFile.getAbsolutePath(), e);
		}

		
	}
}
