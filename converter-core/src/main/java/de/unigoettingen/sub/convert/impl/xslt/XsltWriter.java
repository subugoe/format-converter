package de.unigoettingen.sub.convert.impl.xslt;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import de.unigoettingen.sub.convert.api.WriterWithOptions;
import de.unigoettingen.sub.convert.model.Char;
import de.unigoettingen.sub.convert.model.Document;
import de.unigoettingen.sub.convert.model.Line;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.Page;
import de.unigoettingen.sub.convert.model.Paragraph;
import de.unigoettingen.sub.convert.model.TextBlock;
import de.unigoettingen.sub.convert.model.Word;

public class XsltWriter extends WriterWithOptions {

	private Document doc = new Document();
	private boolean firstPage = true;
	private String beforeMeta = "";
	private String betweenMetaAndPages = "";
	private String afterPages = "";
	
	
	@Override
	public void writeStart() {
		
		Document sampleDoc = new Document();
		Metadata sampleMeta = new Metadata();
		sampleMeta.setOcrSoftwareName("sampleSoftwareName");
		sampleDoc.setMetadata(sampleMeta);
		Page samplePage = newPageWithText();
		sampleDoc.getPage().add(samplePage);
		
		String docXml = transformToString(sampleDoc);

		String metaXml = transformToString(sampleMeta);
		String pageXml = transformToString(samplePage);
		
		String patternForSplit = Pattern.quote(pageXml);
		if (!metaXml.trim().isEmpty()) {
			patternForSplit += "|" + Pattern.quote(metaXml);
		}
		String[] xmlParts = docXml.split(patternForSplit);
		
		if (xmlParts.length == 2) {
			beforeMeta = xmlParts[0];
			afterPages = xmlParts[1];
		} else if (xmlParts.length == 3) {
			beforeMeta = xmlParts[0];
			betweenMetaAndPages = xmlParts[1];
			afterPages = xmlParts[2];
		} else {
			throw new IllegalStateException("Could not determine XML structure");
		}
		
		try {
			output.write(beforeMeta.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	@Override
	public void writeMetadata(Metadata meta) {
		doc.setMetadata(meta);

		transformAndOutput(meta, new StreamResult(output));
	}

	@Override
	public void writePage(Page page) {
		doc.getPage().add(page);
		
		if (firstPage) {
			try {
				output.write(betweenMetaAndPages.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			firstPage = false;
		}
		transformAndOutput(page, new StreamResult(output));
	}

	private String transformToString(Object fragment) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		transformAndOutput(fragment, new StreamResult(baos));
		return baos.toString();
	}
		
	private void transformAndOutput(Object fragment, Result result) {
		try {
			
			org.w3c.dom.Document document = newDom();
			
			JAXBContext context = JAXBContext.newInstance(fragment.getClass());
			Marshaller m = context.createMarshaller();
			//m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			//m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			m.marshal( fragment, document );
			
			Transformer transformer;
			transformer = TransformerFactory.newInstance().newTransformer(
					new StreamSource("src/main/resources/internFormatToTei.xsl") );
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		
			Source src = new DOMSource(document);

			//OutputStream os = new ByteArrayOutputStream();
			//Result target = new StreamResult(output);

			
			//StAXResult target = new StAXResult(xwriter);
			
			transformer.transform(src, result);
			
			//System.out.println(os.toString());
			
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private org.w3c.dom.Document newDom() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		org.w3c.dom.Document document = null;
		try {
			builder = factory.newDocumentBuilder();
			document = builder.newDocument();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return document;
	}

	@Override
	public void writeEnd() {
//		try {
//		JAXBContext context = JAXBContext.newInstance(Document.class);
//		Marshaller m = context.createMarshaller();
//		m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
//		m.marshal( doc, new FileOutputStream("target/intern.xml"));
//	} catch (JAXBException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} catch (FileNotFoundException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}


		try {
			output.write(afterPages.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


}
