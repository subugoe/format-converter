package de.unigoettingen.sub.convert.output;

import de.unigoettingen.sub.convert.api.WriterWithOptions;
import de.unigoettingen.sub.convert.model.Image;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.Page;
import de.unigoettingen.sub.convert.model.PageItem;
import de.unigoettingen.sub.convert.model.Table;
import de.unigoettingen.sub.convert.util.ImageArea;
import de.unigoettingen.sub.convert.util.ResourceHandler;

public class ImageAndTableExtractor extends WriterWithOptions {

	private int pageNumber = 0;
	private static final String FOLDER_WITH_IMAGES_DESCRIPTION = "[folder] (containing original Tiff images)";
	private static final String OUTPUT_FOLDER_FOR_IMAGES = "[folder] for the converted PNG images";
	private ResourceHandler resourceHandler = new ResourceHandler();

	public ImageAndTableExtractor() {
		supportedOptions.put("scans", FOLDER_WITH_IMAGES_DESCRIPTION);
		supportedOptions.put("imagesoutdir", OUTPUT_FOLDER_FOR_IMAGES);
	}
	
	@Override
	public void writeStart() {
	}

	@Override
	public void writeMetadata(Metadata meta) {
	}

	@Override
	public void writePage(Page page) {
		pageNumber = resourceHandler.nextPage();
		String scansFolder = setOptions.get("scans");
		String outputFolder = setOptions.get("imagesoutdir");

		for (PageItem item : page.getPageItems()) {
			ImageArea area = ImageArea.createLTRB(item.getLeft(), item.getTop(), item.getRight(), item.getBottom());
			if (item instanceof Image) {
				resourceHandler.saveSubimage(scansFolder, outputFolder, area);
			} else if (item instanceof Table) {
				resourceHandler.saveSubtable(scansFolder, outputFolder, area);
			}
		}
	}

	@Override
	public void writeEnd() {
	}

}
