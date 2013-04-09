package de.unigoettingen.sub.convert.impl;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.pdf.PRTokeniser;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;

import de.unigoettingen.sub.convert.api.ConvertWriter;
import de.unigoettingen.sub.convert.model.Metadata;
import de.unigoettingen.sub.convert.model.Page;
import static de.unigoettingen.sub.convert.model.builders.PageBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.TextBlockBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.MetadataBuilder.*;
import static de.unigoettingen.sub.convert.model.builders.LanguageBuilder.*;

public class PDFWriterTest {

	private ConvertWriter writer;
	private ByteArrayOutputStream baos;
	
	@Before
	public void setUp() throws Exception {
		writer = new PDFWriter();
		baos = new ByteArrayOutputStream();
		writer.setTarget(baos);
	}

	@After
	public void tearDown() throws Exception {
		baos.close();
	}

	//@Test
	public void test() throws IOException {
		
		Metadata meta = metadata().withSoftwareName("Finereader").withSoftwareVersion("8.0").build();
		
		writer.writeStart();
		writer.writeMetadata(meta);
		writer.writePage(null);
		writer.writeEnd();
		
		PdfReader reader = new PdfReader(baos.toByteArray());
		
		assertEquals("Creator", "Finereader 8.0", reader.getInfo().get("Creator"));
		
		System.out.println(reader.getCatalog());
	}
	
	@Test
	public void testWithFile() throws FileNotFoundException {
		//writer.setTarget(new FileOutputStream("/tmp/test.pdf"));
		writer.setTarget(System.out);
		Metadata meta = metadata().with(language("German")).build();
		
		writer.writeStart();
		writer.writeMetadata(meta);
		writer.writePage(null);
		writer.writePage(null);
		writer.writePage(null);
		writer.writeEnd();

	}
	
    private String parsePdf(PdfReader reader) throws IOException {
		
        // we can inspect the syntax of the imported page
        byte[] streamBytes = reader.getPageContent(1);
        PRTokeniser tokenizer = new PRTokeniser(new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(streamBytes)));
        StringBuilder sb = new StringBuilder();
        while (tokenizer.nextToken()) {
            if (tokenizer.getTokenType() == PRTokeniser.TokenType.STRING) {
                sb.append(tokenizer.getStringValue());
            }
        }
        reader.close();
        return sb.toString();
    }

}
