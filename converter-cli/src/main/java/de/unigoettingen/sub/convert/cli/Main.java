package de.unigoettingen.sub.convert.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unigoettingen.sub.convert.api.ConvertReader;
import de.unigoettingen.sub.convert.api.ConvertWriter;
import de.unigoettingen.sub.convert.impl.AbbyyXMLReader;
import de.unigoettingen.sub.convert.impl.TeiP5Writer;

public class Main {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(Main.class);

	public static void main(String[] args) throws FileNotFoundException {

		Options options = new Options();
		options.addOption("help", false, "print help");
		options.addOption("infile", true, "input file");
		options.addOption("outfile", true, "output file");

		CommandLineParser parser = new GnuParser();
		CommandLine line = null;
		try {
			line = parser.parse(options, args);
		} catch (ParseException e) {
			printHelpAndExit(options);
		}

		if (line.hasOption("help") || !line.hasOption("infile")
				|| !line.hasOption("outfile")) {
			printHelpAndExit(options);
		}

		File infile = new File(line.getOptionValue("infile"));
		InputStream is = new FileInputStream(infile);

		File outfile = new File(line.getOptionValue("outfile"));
		OutputStream os = new FileOutputStream(outfile);

		ConvertReader reader = new AbbyyXMLReader();
		ConvertWriter writer = new TeiP5Writer();
		writer.setTarget(os);
		reader.setWriter(writer);

		LOGGER.info("Starting conversion");
		reader.read(is);

		LOGGER.info("Finished conversion");
	}

	private static void printHelpAndExit(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(" ", options);
		System.exit(0);
	}

}
