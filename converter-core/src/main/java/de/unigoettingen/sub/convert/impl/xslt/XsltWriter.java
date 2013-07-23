package de.unigoettingen.sub.convert.impl.xslt;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import de.unigoettingen.sub.convert.api.ConvertWriter;
import de.unigoettingen.sub.convert.api.StaxWriter;
import de.unigoettingen.sub.convert.model.Document;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.Page;

public class XsltWriter extends StaxWriter {

	private Document doc = new Document();
	

	@Override
	protected void writeStartStax() throws XMLStreamException {
//		xwriter.writeStartDocument("UTF-8", "1.0");
//		xwriter.writeStartElement("TEI");
//		xwriter.writeCharacters("");
		
		Document emptyMeta = new Document();
		emptyMeta.setMetadata(new Metadata());
		transformAndOutput(emptyMeta);

		Document emptyPage = new Document();
		emptyPage.getPage().add(new Page());
		transformAndOutput(emptyPage);

	}


	@Override
	protected void writeMetadataStax(Metadata meta) throws XMLStreamException {
		doc.setMetadata(meta);

		//tansformAndOutput(meta);

	}

	@Override
	protected void writePageStax(Page page) throws XMLStreamException {
		doc.getPage().add(page);
		

		//tansformAndOutput(page);
	}

	private void transformAndOutput(Object fragment) {
		try {
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    org.w3c.dom.Document document = builder.newDocument();
			
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
			Result target = new StreamResult(output);

			
			//StAXResult target = new StAXResult(xwriter);
			
			transformer.transform(src, target);
			
			//System.out.println(os.toString());
			
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
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

	@Override
	protected void writeEndStax() throws XMLStreamException {
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

//		xwriter.writeEndElement();
//		xwriter.writeEndDocument();
		
	}


}
