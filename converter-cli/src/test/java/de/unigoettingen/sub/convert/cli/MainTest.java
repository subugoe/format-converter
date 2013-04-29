package de.unigoettingen.sub.convert.cli;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.ByteArrayOutputStream;
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
		Main.setOutputTarget(out);
	}

	@Test
	public void printsHelp() throws IOException {
		Main.main(new String[]{"-help"});

		String sysout = new String(baos.toByteArray());
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
		Main.main(new String[]{"-infile", "bla", "-outfile", "bla", "-informat", "notvalid", "-outformat", "tei"});

		String sysout = new String(baos.toByteArray());
		assertThat(sysout, containsString("Unknown input format: notvalid"));
		assertThat(sysout, containsString("usage: java -jar"));
	}

	@Test
	public void printsHelpIfUnknownOutputFormat() throws IOException {
		Main.main(new String[]{"-infile", "bla", "-outfile", "bla", "-informat", "abbyyxml", "-outformat", "notexisting"});

		String sysout = new String(baos.toByteArray());
		assertThat(sysout, containsString("Unknown output format: notexisting"));
		assertThat(sysout, containsString("usage: java -jar"));
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
				"-outoptions", "images=src/test/resources/withOneImage"});

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


}
