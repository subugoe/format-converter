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

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unigoettingen.sub.convert.api.WriterWithOptions;
import de.unigoettingen.sub.convert.model.Char;
import de.unigoettingen.sub.convert.model.Document;
import de.unigoettingen.sub.convert.model.Line;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.Page;
import de.unigoettingen.sub.convert.model.Paragraph;
import de.unigoettingen.sub.convert.model.TextBlock;
import de.unigoettingen.sub.convert.model.Word;
import de.unigoettingen.sub.convert.util.JaxbTransformer;

/**
 * Uses an XSLT script to transform the internal model to an XML or a text document.
 * Writes to the output progressively as the input model elements are coming in. 
 * This is realized with some tricks which require some restrictions to the XSLT script.
 * The script must be able to transform a metadata element and a page element.
 * Xpath expressions used inside those elements can only point to locations inside 
 * the respective metadata or page, since they each are processed as a root element.
 * Some examples are located in the test resources of the project.
 * 
 * @author dennis
 *
 */
public class XsltWriter extends WriterWithOptions {
	private final static Logger LOGGER = LoggerFactory.getLogger(XsltWriter.class);
	private static final String XSLT_DESCRIPTION = "[path] to XSLT script file";

	private boolean firstPage = true;
	private String beforeMeta = "";
	private String betweenMetaAndPages = "";
	private String afterPages = "";
	
	private JaxbTransformer transformer;
	
	public XsltWriter() {
		supportedOptions.put("xslt", XSLT_DESCRIPTION);
	}
	
	@Override
	public void writeStart() {
		checkOutputStream();
		
		findOutOutputDocStructure();
		writeBeginningOfDoc();
	}

	private void findOutOutputDocStructure() {
		Document sampleDoc = new Document();
		Metadata sampleMeta = new Metadata();
		sampleMeta.setOcrSoftwareName("sampleSoftwareName");
		sampleDoc.setMetadata(sampleMeta);
		Page samplePage = newPageWithText();
		sampleDoc.getPage().add(samplePage);
		
		String completeDocWithMetaAndPage = transformToString(sampleDoc);

		String metaPartOnly = transformToString(sampleMeta);
		metaPartOnly = makeToRegex(metaPartOnly);
		String pagePartOnly = transformToString(samplePage);
		pagePartOnly = makeToRegex(pagePartOnly);

		String patternForSplit = null;
		if (metaPartOnly.trim().isEmpty()) {
			patternForSplit = pagePartOnly;
		} else {
			patternForSplit = metaPartOnly + "|" + pagePartOnly;
		}

		String[] docParts = completeDocWithMetaAndPage.split(patternForSplit);
		if (docParts.length == 2) {
			beforeMeta = docParts[0];
			afterPages = docParts[1];
		} else if (docParts.length == 3) {
			beforeMeta = docParts[0];
			betweenMetaAndPages = docParts[1];
			afterPages = docParts[2];
		} else {
			throw new IllegalStateException("Could not determine structure for output document");
		}
	}

	private Page newPageWithText() {
		Page page = new Page();
		TextBlock block = new TextBlock();
		Paragraph par = new Paragraph();
		Line line = new Line();
		Word w = new Word();
		Char ch = new Char();
		ch.setValue("a");
		w.getCharacters().add(ch);
		line.getLineItems().add(w);
		par.getLines().add(line);
		block.getParagraphs().add(par);
		page.getPageItems().add(block);
		return page;
	}
	
	private String transformToString(Object fragment) {
		initTransformer();
		return transformer.transformToString(fragment);
	}
		
	private void initTransformer() {
		if (transformer != null) {
			return;
		}
		if (setOptions.get("xslt") == null) {
			throw new IllegalArgumentException("Path to XSLT file is not set");
		}
		File xslt = new File(setOptions.get("xslt"));
		transformer = new JaxbTransformer(xslt, output);
	}

	private String makeToRegex(String docFragment) {
		Character[] regexSymbolsToRemove = {'|', '&', '?', '+', '*', '\\', '['};
		for (char symbol : regexSymbolsToRemove) {
			docFragment = docFragment.replace(symbol, '.');
		}
		docFragment = docFragment.replaceAll("\\s+", "\\\\s*");
		docFragment = docFragment.replaceAll("><", ">\\\\s*<");
		docFragment = docFragment.replaceAll("xmlns=\".*?\"", "\\\\s*");

		return docFragment;
	}

	private void writeBeginningOfDoc() {
		try {
			output.write(beforeMeta.getBytes("utf8"));
		} catch (IOException e) {
			LOGGER.error("Could not write to output", e);
		}
	}

	@Override
	public void writeMetadata(Metadata meta) {
		transformToOutput(meta);
	}

	private void transformToOutput(Object fragment) {
		initTransformer();
		transformer.transformToTarget(fragment);
	}

	@Override
	public void writePage(Page page) {
		if (firstPage) {
			try {
				output.write(betweenMetaAndPages.getBytes("utf8"));
			} catch (IOException e) {
				LOGGER.error("Could not write to output", e);
			}
			firstPage = false;
		}
		transformToOutput(page);
	}

	@Override
	public void writeEnd() {
		try {
			output.write(afterPages.getBytes("utf8"));
		} catch (IOException e) {
			LOGGER.error("Could not write to output", e);
		}
		
	}

}
