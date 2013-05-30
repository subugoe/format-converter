package de.unigoettingen.sub.convert.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
	private boolean helpNeeded;
	private Converter converter;
	private Options options;
	private CommandLine line;
	
	/**
	 * Used for testing
	 * 
	 */
	static void redirectSystemOutputTo(PrintStream stream) {
		out = stream;
	}
	
	public static void main(String[] args) throws IOException {
		new Main().execute(args);
	}

	private void execute(String[] args) throws IOException {
		initConverter();
		initArguments(args);
		verifyArguments();
		if (helpNeeded()) {
			printHelp();
		} else {
			convertUsingArguments();
		}
	}
	
	private void initConverter() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"context.xml");
		converter = ctx.getBean("converter", Converter.class);
		converter.setSysOut(out);
	}

	private void initArguments(String[] args) {
		Set<String> readerNames = converter.getReaderNames();
		Set<String> writerNames = converter.getWriterNames();
		String specificOptionsForWriters = converter.constructHelpForOutputOptions();
		
		options = new Options();
		options.addOption("help", false, "print help");
		options.addOption("infile", true, "input file");
		options.addOption("outfile", true, "output file");
		options.addOption("informat", true, "input format, possible values: "
				+ readerNames);
		options.addOption("outformat", true, "output format, possible values: "
				+ writerNames);
		options.addOption("outoptions", true, "(optional) implementation-specific options, comma-separated\n" 
				+ specificOptionsForWriters);
		CommandLineParser parser = new GnuParser();
		try {
			line = parser.parse(options, args);
		} catch (ParseException e) {
			out.println("Error reading arguments: " + e.getMessage());
			setHelpNeeded();
		}
	}

	private void verifyArguments() {
		if (askingForHelp()) {
			setHelpNeeded();
		} else if (argumentsIncomplete()) {
			out.println("You must at least provide -infile, -outfile, -informat, and -outformat.");
			setHelpNeeded();
		} else {
			String inFormat = line.getOptionValue("informat");
			String outFormat = line.getOptionValue("outformat");
			if (converter.unknownInput(inFormat)) {
				out.println("Unknown input format: " + inFormat);
				setHelpNeeded();
			}
			if (converter.unknownOutput(outFormat)) {
				out.println("Unknown output format: " + outFormat);
				setHelpNeeded();
			}
		}
	}

	private void convertUsingArguments() throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			String inFormat = line.getOptionValue("informat");
			String outFormat = line.getOptionValue("outformat");
			File infile = new File(line.getOptionValue("infile"));
			is = new FileInputStream(infile);
	
			File outfile = new File(line.getOptionValue("outfile"));
			os = new FileOutputStream(outfile);
	
			Map<String, String> writerOptions = parseWriterOptions();
			
			out.println("Starting conversion, input file: " + infile.getCanonicalPath());
			Date startTime = new Date();
			converter.convert(inFormat, is, outFormat, os, writerOptions);
			Date finishTime = new Date();
			long time = (finishTime.getTime()-startTime.getTime()) / 1000;
			out.println("Finished conversion in " + time + " seconds, output file: " + outfile.getCanonicalPath());
		} finally {
			if (is != null)
				is.close();
			if (os != null)
				os.close();
		}
	}

	private boolean askingForHelp() {
		return line.hasOption("help");
	}
	
	private boolean argumentsIncomplete() {
		return !line.hasOption("infile")
				|| !line.hasOption("outfile") || !line.hasOption("informat")
				|| !line.hasOption("outformat");
	}

	private void setHelpNeeded() {
		helpNeeded = true;
	}
	
	private boolean helpNeeded() {
		return helpNeeded;
	}
	
	private void printHelp() throws UnsupportedEncodingException {
		OutputStreamWriter osw = new OutputStreamWriter(out, "UTF8");
		PrintWriter pw = new PrintWriter(osw);
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(pw, HelpFormatter.DEFAULT_WIDTH, "java -jar converter.jar <options>", "", options,
				HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD,
				"");
		pw.close();
	}

	private Map<String, String> parseWriterOptions() {
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
