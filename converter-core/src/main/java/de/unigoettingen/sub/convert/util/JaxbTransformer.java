package de.unigoettingen.sub.convert.util;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

public class JaxbTransformer {

	private File xslt;
	private OutputStream targetStream;

	public JaxbTransformer(File xslt, OutputStream targetStream) {
		if (!xslt.exists()) {
			throw new IllegalArgumentException("File does not exist: " + xslt.getAbsolutePath());
		}
		this.xslt = xslt;
		this.targetStream = targetStream;
	}
	
	public void transformToTarget(Object jaxbObject) {
		transformToOutputStream(jaxbObject, targetStream);
	}

	public String transformToString(Object jaxbObject) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		transformToOutputStream(jaxbObject, baos);
		String transformed = "";
		try {
			transformed = baos.toString("utf8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Could not use UTF-8", e);
		}
		return transformed;
	}

	private void transformToOutputStream(Object jaxbObject, OutputStream target) {
		try {
			Document document = newDom();
			
			JAXBContext context = JAXBContext.newInstance(jaxbObject.getClass());
			Marshaller m = context.createMarshaller();
			m.marshal(jaxbObject, document);
			
			Source src = new DOMSource(document);
			Transformer transformer = newXsltTransformer();
			transformer.transform(src, new StreamResult(target));
		} catch (JAXBException e) {
			throw new IllegalStateException("Could not convert to internal XML format: " + jaxbObject.getClass(), e);
		} catch (TransformerException e) {
			throw new IllegalStateException("Could not transform XML fragment to output: " + jaxbObject.getClass(), e);
		}
	}

	private Document newDom() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		Document document = null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.newDocument();
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException("Could not create empty DOM tree", e);
		}
		return document;
	}

	private Transformer newXsltTransformer() {
		Transformer transformer = null;
		try {
			transformer = TransformerFactory.newInstance().newTransformer(
					new StreamSource(xslt) );
		} catch (TransformerConfigurationException e) {
			throw new IllegalStateException("Could not process xslt file " + xslt.getAbsolutePath(), e);
		} catch (TransformerFactoryConfigurationError e) {
			throw new IllegalStateException("Could not process xslt file " + xslt.getAbsolutePath(), e);
		}
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

		return transformer;
	}

}
