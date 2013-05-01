package de.unigoettingen.sub.convert.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

	private static PrintStream out = System.out;
	private boolean exited;
	
	static void setOutputTarget(PrintStream stream) {
		out = stream;
	}
	
	public static void main(String[] args) throws IOException {

		new Main().execute(args);
		
	}

	private void execute(String[] args) throws IOException {
		
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
		String specificOptionsForWriters = converter.constructHelpForOutputOptions();
		options.addOption("outoptions", true, "(optional) implementation-specific options, comma-separated\n" + specificOptionsForWriters);
		CommandLineParser parser = new GnuParser();
		CommandLine line = null;
		try {
			line = parser.parse(options, args);
		} catch (ParseException e) {
			printHelpAndSetToExit(options);
		}

		if (helpNeeded(line)) {
			printHelpAndSetToExit(options);
		}
		
		String inFormat = line.getOptionValue("informat");
		String outFormat = line.getOptionValue("outformat");
		if (converter.unknownInput(inFormat)) {
			out.println("Unknown input format: " + inFormat);
			printHelpAndSetToExit(options);
		}
		if (converter.unknownOutput(outFormat)) {
			out.println("Unknown output format: " + outFormat);
			printHelpAndSetToExit(options);
		}

		if (!exited) {
			File infile = new File(line.getOptionValue("infile"));
			InputStream is = new FileInputStream(infile);
	
			File outfile = new File(line.getOptionValue("outfile"));
			OutputStream os = new FileOutputStream(outfile);
	
			Map<String, String> writerOptions = parseWriterOptions(line);
			
			out.println("Starting conversion, input file: " + infile.getCanonicalPath());
			Date startTime = new Date();
			converter.convert(inFormat, is, outFormat, os, writerOptions);
			Date finishTime = new Date();
			long time = (finishTime.getTime()-startTime.getTime()) / 1000;
			out.println("Finished conversion in " + time + " seconds, output file: " + outfile.getCanonicalPath());
		}

	}
	
	private boolean helpNeeded(CommandLine line) {
		return line.hasOption("help") || !line.hasOption("infile")
				|| !line.hasOption("outfile") || !line.hasOption("informat")
				|| !line.hasOption("outformat");
	}

	private void printHelpAndSetToExit(Options options) {
		PrintWriter pw = new PrintWriter(out);
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(pw, HelpFormatter.DEFAULT_WIDTH, "java -jar converter.jar <options>", "", options,
				HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD,
				"");
		pw.close();
		exited = true;
	}

	private Map<String, String> parseWriterOptions(CommandLine line) {
		if (!line.hasOption("outoptions")) {
			return new HashMap<String, String>();
		}
		
		Map<String, String> outputOptions = new HashMap<String, String>();
		String allOptions = line.getOptionValue("outoptions");
		String[] allTokenized = allOptions.split(",");
		for (String oneOption : allTokenized) {
			String[] oneTokenized = oneOption.split("=");
			outputOptions.put(oneTokenized[0], oneTokenized[1]);
		}
		
		return outputOptions;
	}

}
