package de.unigoettingen.sub.convert.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;
import de.unigoettingen.sub.convert.api.ConvertWriter;
import de.unigoettingen.sub.convert.api.WriterWithOptions;
import de.unigoettingen.sub.convert.model.Language;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.Page;
import de.unigoettingen.sub.convert.util.ResourceHandler;

public class EPUBWriter extends WriterWithOptions implements ConvertWriter {

	private Book book;
	private static final String FOLDER_WITH_IMAGES_DESCRIPTION = "[folder] (containing original Tiff images)";
	private static final String INCLUDE_SCANS_DESCRIPTION = "[true or false], include the original scanned images into the result file, default is 'true'";
	private ResourceHandler resourceHandler = new ResourceHandler();
	private Page page;

	public EPUBWriter() {
		supportedOptions.put("scans", FOLDER_WITH_IMAGES_DESCRIPTION);
		supportedOptions.put("includescans", INCLUDE_SCANS_DESCRIPTION);
	}
	
	@Override
	public void writeStart() {
		book = new Book();
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
		this.page = page;
		try {
			writeHtmlPageToTemp();
		} catch (IOException e) {
			throw new IllegalStateException("Could not write page " + resourceHandler.getCurrentPageNumber(), e);
		}
		
		resourceHandler.addCurrentHtmlToTemp();
		if (scansAvailable()) {
			resourceHandler.addCurrentScanToTemp();
		}
	}

	private boolean scansAvailable() {
		return setOptions.get("scans") != null;
	}

	private void writeHtmlPageToTemp()
			throws FileNotFoundException, IOException {
		File tempHtml = resourceHandler.getNextTempHtmlFile();
		File tempDir = tempHtml.getParentFile();

		ConvertWriter htmlWriter = new HTMLWriter(resourceHandler);
		FileOutputStream htmlStream = new FileOutputStream(tempHtml);
		htmlWriter.setTarget(htmlStream);
		if (scansAvailable()) {
			String imagesSource = setOptions.get("scans");
			htmlWriter.addImplementationSpecificOption("scans", imagesSource);
			htmlWriter.addImplementationSpecificOption("imagesoutdir", tempDir.getAbsolutePath());
			htmlWriter.addImplementationSpecificOption("onedir", "true");
			htmlWriter.addImplementationSpecificOption("includescans", setOptions.get("includescans"));
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
			//resourceHandler.deleteTempFiles();
		}
	}

	private void fillBookWithTempFiles() throws IOException {
		for (int pageNr = 1; pageNr <= resourceHandler.getCurrentPageNumber(); pageNr++) {
			InputStream tempHtmlFis = resourceHandler.getHtmlPage(pageNr);
			String htmlName = resourceHandler.getNameForHtml(pageNr);
			book.addSection("Page " + pageNr, new Resource(tempHtmlFis, htmlName));
			
			if (scansAvailable()) {
				InputStream tempImageFis = resourceHandler.getScanForPage(pageNr);
				String scanName = resourceHandler.getNameForScan(pageNr);
				book.getResources().add(new Resource(tempImageFis, scanName));
			}
		}
		if (scansAvailable()) {
			for (File subimage : resourceHandler.getAllSubimages()) {
				InputStream is = new FileInputStream(subimage);
				book.getResources().add(new Resource(is, subimage.getName()));

			}
		}
	}

}
