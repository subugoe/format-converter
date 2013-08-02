package de.unigoettingen.sub.convert.integrationtests;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import de.unigoettingen.sub.convert.api.WriterWithOptions;
import de.unigoettingen.sub.convert.model.Document;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.Page;

public class InternalFormatWriter extends WriterWithOptions {

	private Document doc = new Document();

	@Override
	public void writeStart() {

	}

	@Override
	public void writeMetadata(Metadata meta) {
		doc.setMetadata(meta);
	}

	@Override
	public void writePage(Page page) {
		doc.getPage().add(page);
	}

	@Override
	public void writeEnd() {
		try {
			JAXBContext context = JAXBContext.newInstance(Document.class);
			Marshaller m = context.createMarshaller();
			m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			m.marshal( doc, output);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

}
