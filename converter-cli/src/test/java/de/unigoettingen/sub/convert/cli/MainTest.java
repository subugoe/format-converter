package de.unigoettingen.sub.convert.cli;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;


public class MainTest {

	private ByteArrayOutputStream baos;

	@Before
	public void setUp() throws Exception {
		baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		Main.redirectSystemOutputTo(out);
	}

	@Test
	public void printsHelp() throws IOException {
		Main.main(new String[]{"-help"});

		String sysout = new String(baos.toByteArray());
		assertThat(sysout, containsString("usage: java -jar"));
	}

	@Test
	public void usingWrongArgument() throws IOException {
		Main.main(new String[]{"-wrongarg"});

		String sysout = new String(baos.toByteArray());
		assertThat(sysout, containsString("Error reading arguments"));
		assertThat(sysout, containsString("Unrecognized option: -wrongarg"));
		assertThat(sysout, containsString("usage: java -jar"));
	}

	@Test
	public void printsHelpIfNoArguments() throws IOException {
		Main.main(new String[]{});

		String sysout = new String(baos.toByteArray());
		assertThat(sysout, containsString("usage: java -jar"));
	}

	@Test
	public void printsHelpIfNotAllNecessaryArgsArePresent() throws IOException {
		Main.main(new String[]{"-infile", "bla", "-outfile", "bla", "-informat", "bla"});

		String sysout = new String(baos.toByteArray());
		assertThat(sysout, containsString("usage: java -jar"));
	}

	@Test
	public void printsHelpIfUnknownInputFormat() throws IOException {
		Main.main(new String[]{"-infile", "bla", 
				"-outfile", "bla", 
				"-informat", "notvalid", 
				"-outformat", "tei"});

		String sysout = new String(baos.toByteArray());
		assertThat(sysout, containsString("Unknown input format: notvalid"));
		assertThat(sysout, containsString("usage: java -jar"));
	}

	@Test
	public void printsHelpIfUnknownOutputFormat() throws IOException {
		Main.main(new String[]{"-infile", "bla", 
				"-outfile", "bla", 
				"-informat", "abbyyxml", 
				"-outformat", "notexisting"});

		String sysout = new String(baos.toByteArray());
		assertThat(sysout, containsString("Unknown output format: notexisting"));
		assertThat(sysout, containsString("usage: java -jar"));
	}

	@Test(expected=FileNotFoundException.class)
	public void doesNotWorkIfInputNotFound() throws IOException {
		Main.main(new String[]{"-infile", "src/test/resources/isNotThere.xml", 
				"-outfile", "target/tei.xml", 
				"-informat", "abbyyxml", 
				"-outformat", "tei"});
	}

	@Test
	public void convertsAbbyyToTei() throws IOException {
		Main.main(new String[]{"-infile", "src/test/resources/abbyy10_textPage.xml", 
				"-outfile", "target/tei.xml", 
				"-informat", "abbyyxml", 
				"-outformat", "tei"});

		String sysout = new String(baos.toByteArray());
		assertThat(sysout, containsString("Starting conversion"));
		assertThat(sysout, containsString("Finished conversion"));
	}

	@Test
	public void convertsAbbyyToPdf() throws IOException {
		Main.main(new String[]{"-infile", "src/test/resources/abbyy10_textPage.xml", 
				"-outfile", "target/out.pdf", 
				"-informat", "abbyyxml", 
				"-outformat", "pdf"});

		String sysout = new String(baos.toByteArray());
		assertThat(sysout, containsString("Starting conversion"));
		assertThat(sysout, containsString("Finished conversion"));
	}

	@Test
	public void convertsAbbyyToPdfWithImage() throws IOException {
		Main.main(new String[]{"-infile", "src/test/resources/abbyy10_textPage.xml", 
				"-outfile", "target/outImage.pdf", 
				"-informat", "abbyyxml", 
				"-outformat", "pdf",
				"-outoptions", "scans=src/test/resources/withOneImage"});

		String sysout = new String(baos.toByteArray());
		assertThat(sysout, containsString("Starting conversion"));
		assertThat(sysout, containsString("Finished conversion"));
	}

	@Test
	public void convertsAbbyyToPdfWithOriginalSize() throws IOException {
		Main.main(new String[]{"-infile", "src/test/resources/abbyy10_textPage.xml", 
				"-outfile", "target/outOrigSize.pdf", 
				"-informat", "abbyyxml", 
				"-outformat", "pdf",
				"-outoptions", "pagesize=original"});

		String sysout = new String(baos.toByteArray());
		assertThat(sysout, containsString("Starting conversion"));
		assertThat(sysout, containsString("Finished conversion"));
	}

	@Test
	public void convertsAbbyyToPdfWithSubimageAndTextOnly() throws IOException {
		Main.main(new String[]{"-infile", "src/test/resources/abbyy10_coverPage.xml", 
				"-outfile", "target/outSubimage.pdf", 
				"-informat", "abbyyxml", 
				"-outformat", "pdf",
				"-outoptions", "scans=src/test/resources/coverImage,includescans=false"});

		String sysout = new String(baos.toByteArray());
		assertThat(sysout, containsString("Starting conversion"));
		assertThat(sysout, containsString("Finished conversion"));
	}

	@Test
	public void convertsAbbyyToEpub() throws IOException {
		Main.main(new String[]{"-infile", "src/test/resources/abbyy10_textPage.xml", 
				"-outfile", "target/out.epub", 
				"-informat", "abbyyxml", 
				"-outformat", "epub"});

		String sysout = new String(baos.toByteArray());
		assertThat(sysout, containsString("Starting conversion"));
		assertThat(sysout, containsString("Finished conversion"));
	}

	@Test
	public void convertsAbbyyToEpubWithImage() throws IOException {
		Main.main(new String[]{"-infile", "src/test/resources/abbyy10_textPage.xml", 
				"-outfile", "target/outImage.epub", 
				"-informat", "abbyyxml", 
				"-outformat", "epub",
				"-outoptions", "scans=src/test/resources/withOneImage"});

		String sysout = new String(baos.toByteArray());
		assertThat(sysout, containsString("Starting conversion"));
		assertThat(sysout, containsString("Finished conversion"));
	}

	@Test
	public void convertsAbbyyToEpubWithSubimageAndTextOnly() throws IOException {
		Main.main(new String[]{"-infile", "src/test/resources/abbyy10_coverPage.xml", 
				"-outfile", "target/outSubimage.epub", 
				"-informat", "abbyyxml", 
				"-outformat", "epub",
				"-outoptions", "scans=src/test/resources/coverImage,includescans=false"});

		String sysout = new String(baos.toByteArray());
		assertThat(sysout, containsString("Starting conversion"));
		assertThat(sysout, containsString("Finished conversion"));
	}

	@Test
	public void convertsAbbyyToTeiUsingXslt() throws IOException {
		Main.main(new String[]{"-infile", "src/test/resources/abbyy10_coverPage.xml", 
				"-outfile", "target/teiConvertedWithXslt.xml", 
				"-informat", "abbyyxml", 
				"-outformat", "xsltoutput",
				"-outoptions", "xslt=src/test/resources/toTei.xsl"});

		String sysout = new String(baos.toByteArray());
		assertThat(sysout, containsString("Starting conversion"));
		assertThat(sysout, containsString("Finished conversion"));
	}

	@Test(expected=IllegalArgumentException.class)
	public void convertsAbbyyToTeiUsingXsltNoScript() throws IOException {
		Main.main(new String[]{"-infile", "src/test/resources/abbyy10_coverPage.xml", 
				"-outfile", "target/teiConvertedWithXslt.xml", 
				"-informat", "abbyyxml", 
				"-outformat", "xsltoutput"});
	}

}
