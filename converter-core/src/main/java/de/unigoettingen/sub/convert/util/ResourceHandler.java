package de.unigoettingen.sub.convert.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ResourceHandler {

	private final static Logger LOGGER = LoggerFactory.getLogger(ResourceHandler.class);
	private File[] images;
	private int pageNumber = 0;
	private int subimageCounter = 0;
	
	private List<File> htmls = new ArrayList<File>();
	private List<File> scans = new ArrayList<File>();
	private List<File> subimages = new ArrayList<File>();
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

	public String getNameForHtml(int number) {
		return "page" + number + ".html";
	}
	
	public String getNameForScan(int number) {
		return "scan" + number + ".png";
	}
	public String getNameForCurrentScan() {
		return getNameForScan(pageNumber);
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
		File tempHtml = new File(tempDir, getNameForHtml(pageNumber));
		htmls.add(tempHtml);
	}
	
	public void addCurrentScanToTemp() {
		File tempHtml = new File(tempDir, getNameForCurrentScan());
		scans.add(tempHtml);
	}
	
	public void addCurrentSubimageToTemp() {
		File tempSubimage = new File(tempDir, getNameForCurrentSubimage());
		subimages.add(tempSubimage);
	}
	
	public File getNextTempHtmlFile() {
		return new File(tempDir, getNameForHtml(pageNumber + 1));
	}
	
	public InputStream getHtmlPage(int pageNumber) throws FileNotFoundException {
		int index = pageNumber - 1;
		return new FileInputStream(htmls.get(index));
	}
	
	public InputStream getScanForPage(int pageNumber) throws FileNotFoundException {
		int index = pageNumber - 1;
		return new FileInputStream(scans.get(index));
	}
	
	public List<File> getAllSubimages() {
		return subimages;
	}
	
	public void deleteTempFiles() {
		deleteAllInList(htmls);
		deleteAllInList(scans);
		deleteAllInList(subimages);
	}
	
	private void deleteAllInList(List<File> files) {
		for (File tempFile : files) {
			boolean deleted = tempFile.delete();
			if (!deleted) {
				LOGGER.warn("Could not delete temp file: " + tempFile.getAbsolutePath());
			}
		}
	}
	
	public File getTifImageForPage(int pageNumber, File folder) {
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
		FileOutputStream fos = null;
		try {
			BufferedImage originalImage = ImageIO.read(tifFile);
			boolean madeDirs = pngFile.getParentFile().mkdirs();
			if (madeDirs) {
				LOGGER.debug("created directory " + pngFile.getParentFile().getAbsolutePath());
			}
			
			fos = new FileOutputStream(pngFile);
			cutAndWriteToStream(originalImage, area, fos);
		} catch (IOException e) {
			throw new IllegalStateException("Error while processing image: " + tifFile.getAbsolutePath(), e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}
	private void cutAndWriteToStream(BufferedImage originalImage,
			ImageArea area, OutputStream os) throws IOException {
		int x = area.getLeft();
		int y = area.getTop();
		int width = area.getRight() - area.getLeft();
		int height = area.getBottom() - area.getTop();
		ImageIO.write(originalImage.getSubimage(x, y, width, height), "png", os);
	}
	
	public byte[] tifToPngAndCut(File tifFile, ImageArea area) {
		byte[] imageBytes = null;
		try {
			BufferedImage originalImage = ImageIO.read(tifFile);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			cutAndWriteToStream(originalImage, area, baos);
			imageBytes = baos.toByteArray();
		} catch (IOException e) {
			throw new IllegalStateException("Error while processing image: " + tifFile.getAbsolutePath(), e);
		}
		return imageBytes;
	}
	
	public int getWidthOfImage(int pageNumber, File folder) {
		int width = 0;
		File tifFile = getTifImageForPage(pageNumber, folder);
		try {
			BufferedImage bufImage = ImageIO.read(tifFile);
			width = bufImage.getWidth();
		} catch (IOException e) {
			throw new IllegalStateException("Error while processing image: " + tifFile.getAbsolutePath(), e);
		}
		return width;
	}
}
