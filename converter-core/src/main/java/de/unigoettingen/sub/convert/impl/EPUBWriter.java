package de.unigoettingen.sub.convert.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;

import de.unigoettingen.sub.convert.api.ConvertWriter;
import de.unigoettingen.sub.convert.model.Char;
import de.unigoettingen.sub.convert.model.Language;
import de.unigoettingen.sub.convert.model.LineItem;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.Page;
import de.unigoettingen.sub.convert.model.PageItem;
import de.unigoettingen.sub.convert.model.Paragraph;
import de.unigoettingen.sub.convert.model.TextBlock;
import de.unigoettingen.sub.convert.model.Line;
import de.unigoettingen.sub.convert.util.ResourceHandler;

public class EPUBWriter implements ConvertWriter {

	private Book book;
	private OutputStream output;
	private List<File> htmls;
	private List<File> images;
	private int pageNumber = 0;
	private Map<String, String> options = new HashMap<String, String>();
	private static final String FOLDER_WITH_IMAGES_DESCRIPTION = "[folder] (containing original Tiff images)";
	private static final String PNG = "png";

	private ResourceHandler resourceHandler = new ResourceHandler();

	public EPUBWriter() {
		options.put("images", FOLDER_WITH_IMAGES_DESCRIPTION);
	}
	
	@Override
	public void writeStart() {
		book = new Book();
		htmls = new ArrayList<File>();
		images = new ArrayList<File>();
	}

	@Override
	public void writeMetadata(Metadata meta) {
		List<Language> langs = meta.getLanguages();
		if (!langs.isEmpty()) {
			Language lang = langs.get(0);
			String langId = lang.getLangId();
			if (langId != null && !langId.isEmpty()) {
				book.getMetadata().setLanguage(langId);
			}
		}
	}

	@Override
	public void writePage(Page page) {
		pageNumber++;

		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		File tempHtml = new File(tempDir, "page" + pageNumber + ".html");
		PrintWriter htmlWriter = null;
		try {
			htmlWriter = new PrintWriter(tempHtml);
			htmlWriter.println("<html>");
			htmlWriter.println("<body>");
			
			if (imagesAvailable()) {
				htmlWriter.println("<img src='image" + pageNumber + "." + PNG +"'>");
				prepareImageForPage();
			}
			
			for (PageItem pageItem : page.getPageItems()) {
				if (pageItem instanceof TextBlock) {
					TextBlock block = (TextBlock) pageItem;
					for (Paragraph par : block.getParagraphs()) {
						htmlWriter.println("<p>");
						
						for (Line line : par.getLines()) {
							for (LineItem item : line.getLineItems()) {
								for (Char ch : item.getCharacters()) {
									htmlWriter.print(ch.getValue());
								}
							}
							htmlWriter.println("<br/>");
						}
						
						htmlWriter.println("</p>");
					}
				}
			}
			
			htmlWriter.println("</body>");
			htmlWriter.println("</html>");
			
			htmls.add(tempHtml);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (htmlWriter != null)
				htmlWriter.close();
		}
	}

	private void prepareImageForPage() throws IOException {
		File imagesFolder = new File(options.get("images"));
		File imageFile = resourceHandler.getImageForPage(pageNumber, imagesFolder);
		BufferedImage tif = ImageIO.read(imageFile);
		
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		File tempImage = new File(tempDir, "image" + pageNumber + "." + PNG);

		FileOutputStream fos = new FileOutputStream(tempImage);
		ImageIO.write(tif, PNG, fos);
		
		images.add(tempImage);
	}

	@Override
	public void writeEnd() {
		try {
			for (int i = 0; i < htmls.size(); i++) {
				int pageNr = i + 1;
				FileInputStream fis = new FileInputStream(htmls.get(i));
				book.addSection("Page " + pageNr, new Resource(fis, "page" + pageNr + ".html"));
				
				if (imagesAvailable()) {
					FileInputStream tempImageFis = new FileInputStream(images.get(i));
					book.getResources().add(new Resource(tempImageFis, "image" + pageNr + "." + PNG));
				}
			}
	
			
			
			EpubWriter epubWriter = new EpubWriter();
			
			epubWriter.write(book, output);
	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			for (File tempHtml : htmls) {
				tempHtml.delete();
			}
			for (File tempImage : images) {
				tempImage.delete();
			}
		}
	}

	@Override
	public void setTarget(OutputStream stream) {
		this.output = stream;
	}

	@Override
	public void setImplementationSpecificOptions(Map<String, String> options) {
		this.options.putAll(options);
	}

	@Override
	public Map<String, String> getImplementationSpecificOptions() {
		return options;
	}

	private boolean imagesAvailable() {
		return !options.get("images").equals(FOLDER_WITH_IMAGES_DESCRIPTION);
	}

}
