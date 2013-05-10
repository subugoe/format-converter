package de.unigoettingen.sub.convert.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;
import de.unigoettingen.sub.convert.api.ConvertWriter;
import de.unigoettingen.sub.convert.api.WriterWithOptions;
import de.unigoettingen.sub.convert.model.Language;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.Page;

public class EPUBWriter extends WriterWithOptions implements ConvertWriter {

	private Book book;
	private List<File> htmls;
	private List<File> scans;
	private int pageNumber = 0;
	private static final String FOLDER_WITH_IMAGES_DESCRIPTION = "[folder] (containing original Tiff images)";
	private static final String PNG = "png";

	private Page page;

	public EPUBWriter() {
		supportedOptions.put("scans", FOLDER_WITH_IMAGES_DESCRIPTION);
	}
	
	@Override
	public void writeStart() {
		book = new Book();
		htmls = new ArrayList<File>();
		scans = new ArrayList<File>();
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
		this.page = page;
		
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		File tempHtml = new File(tempDir, "page" + pageNumber + ".html");
		htmls.add(tempHtml);
		if (scansAvailable()) {
			scans.add(new File(tempDir, "scan" + pageNumber + "." + PNG));
		}
		
		try {
			writeHtmlPageToDir(tempHtml, tempDir);
		} catch (IOException e) {
			throw new IllegalStateException("Could not write page " + pageNumber, e);
		}
		
	}

	private boolean scansAvailable() {
		return setOptions.get("scans") != null;
	}

	private void writeHtmlPageToDir(File tempHtml, File tempDir)
			throws FileNotFoundException, IOException {
		ConvertWriter htmlWriter = new HTMLWriter();
		FileOutputStream htmlStream = new FileOutputStream(tempHtml);
		htmlWriter.setTarget(htmlStream);
		htmlWriter.addImplementationSpecificOption("fixedpagenr", ""+pageNumber);
		if (scansAvailable()) {
			String imagesSource = setOptions.get("scans");
			htmlWriter.addImplementationSpecificOption("scans", imagesSource);
			htmlWriter.addImplementationSpecificOption("imagesoutdir", tempDir.getAbsolutePath());
			htmlWriter.addImplementationSpecificOption("onedir", "true");
		}
		htmlWriter.writeStart();
		htmlWriter.writePage(page);
		htmlWriter.writeEnd();
		htmlStream.close();
	}

	@Override
	public void writeEnd() {
		try {
			fillBookWithTempFiles();
			EpubWriter epubWriter = new EpubWriter();
			epubWriter.write(book, output);
	
		} catch (IOException e) {
			throw new IllegalStateException("Could not create epub", e);
		} finally {
			deleteTempFiles();
		}
	}

	private void fillBookWithTempFiles() throws IOException {
		for (int i = 0; i < htmls.size(); i++) {
			int pageNr = i + 1;
			FileInputStream tempHtmlFis = new FileInputStream(htmls.get(i));
			book.addSection("Page " + pageNr, new Resource(tempHtmlFis, "page" + pageNr + ".html"));
			
			if (scansAvailable()) {
				FileInputStream tempImageFis = new FileInputStream(scans.get(i));
				book.getResources().add(new Resource(tempImageFis, "scan" + pageNr + "." + PNG));
			}
		}
	}

	private void deleteTempFiles() {
		for (File tempHtml : htmls) {
			tempHtml.delete();
		}
		for (File tempImage : scans) {
			tempImage.delete();
		}
	}

}
