package de.unigoettingen.sub.convert.output;

/*

Copyright 2014 SUB Goettingen. All rights reserved.
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.

*/

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
