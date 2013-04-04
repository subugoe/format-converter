package de.unigoettingen.sub.convert.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

	private final static Logger LOGGER = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws IOException {

		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"context.xml");
		Converter converter = ctx.getBean("converter", Converter.class);

		Set<String> readerNames = converter.getReaderNames();
		Set<String> writerNames = converter.getWriterNames();

		Options options = new Options();
		options.addOption("help", false, "print help");
		options.addOption("infile", true, "input file");
		options.addOption("outfile", true, "output file");
		options.addOption("informat", true, "input format, possible values: "
				+ readerNames);
		options.addOption("outformat", true, "output format, possible values: "
				+ writerNames);

		CommandLineParser parser = new GnuParser();
		CommandLine line = null;
		try {
			line = parser.parse(options, args);
		} catch (ParseException e) {
			printHelpAndExit(options);
		}

		if (helpNeeded(line)) {
			printHelpAndExit(options);
		}
		
		String inFormat = line.getOptionValue("informat");
		String outFormat = line.getOptionValue("outformat");
		if (!readerNames.contains(inFormat)) {
			System.out.println("Unknown input format: " + inFormat);
			printHelpAndExit(options);
		}
		if (!writerNames.contains(outFormat)) {
			System.out.println("Unknown output format: " + outFormat);
			printHelpAndExit(options);
		}

		File infile = new File(line.getOptionValue("infile"));
		InputStream is = new FileInputStream(infile);

		File outfile = new File(line.getOptionValue("outfile"));
		OutputStream os = new FileOutputStream(outfile);

		LOGGER.info("Starting conversion");
		converter.convert(inFormat, is, outFormat, os);
		LOGGER.info("Finished conversion, input file: " + infile.getCanonicalPath());

	}

	private static boolean helpNeeded(CommandLine line) {
		return line.hasOption("help") || !line.hasOption("infile")
				|| !line.hasOption("outfile") || !line.hasOption("informat")
				|| !line.hasOption("outformat");
	}

	private static void printHelpAndExit(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(" ", options);
		System.exit(0);
	}

}
