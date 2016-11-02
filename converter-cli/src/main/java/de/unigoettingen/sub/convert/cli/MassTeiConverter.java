package de.unigoettingen.sub.convert.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import de.unigoettingen.sub.convert.api.ConvertReader;
import de.unigoettingen.sub.convert.api.ConvertWriter;
import de.unigoettingen.sub.convert.input.abbyyxml.AbbyyXMLReader;
import de.unigoettingen.sub.convert.output.XsltWriter;

public class MassTeiConverter {

	private static String xsltPath;
	private static File inputDir;
	private static File outputDir;
	private static File logFile;
	private static int count = 0;

	public static void main(String[] args) throws Exception {
		if (args.length != 4) {
			System.out.println("Syntax: java -cp converter.jar de.unigoettingen.sub.convert.cli.MassTeiConverter <xslt> <input-dir> <output-dir> <log-file>");
			System.exit(1);
		}
		
		xsltPath = args[0];
		inputDir = new File(args[1]);
		outputDir = new File(args[2]);
		logFile = new File(args[3]);

		processFiles(inputDir);

	}
	
	private static void processFiles(File currentDir) throws IOException {
		if (currentDir.getName().equals("navxml")) {
			return;
		}
		count++;
		System.out.println(count + ": " + currentDir.getAbsolutePath());
		File[] currentDirChildren = currentDir.listFiles();
		for (File child : currentDirChildren) {
			if (child.isFile() && child.getName().endsWith(".xml")) {
				File abbyyFile = child;

				File currentOutputDir = prepareOutputDir(currentDir);
				File currentOutputFile = new File(currentOutputDir, abbyyFile.getName().replace(".xml", ".tei.xml"));
				if (currentOutputFile.exists()) {
					continue;
				}

				try {
					ConvertReader reader = new AbbyyXMLReader();
					ConvertWriter writer = new XsltWriter();
					writer.setTarget(new FileOutputStream(currentOutputFile));
					writer.addImplementationSpecificOption("xslt", xsltPath);
					reader.setWriter(writer);
					reader.read(new FileInputStream(abbyyFile));
				} catch(IllegalArgumentException e) {
					String error = "Error while reading file: " + abbyyFile.getAbsolutePath() + "\n";
					FileUtils.write(logFile, error + e.getMessage() + "\n", true);
					continue;
				}

			} else if (child.isDirectory()) {
				processFiles(child);
			}
		}
	}

	private static File prepareOutputDir(File currentInputDir) {
		int inputDirLength = inputDir.getAbsolutePath().length();
		String subPath = currentInputDir.getAbsolutePath().substring(inputDirLength);

		File currentOutputDir = null;
		if ("".equals(subPath)) {
			currentOutputDir = outputDir;
		} else {
			currentOutputDir = new File(outputDir.getAbsolutePath() + subPath);
		}
		if (!currentOutputDir.exists()) {
			currentOutputDir.mkdirs();
		}
		return currentOutputDir;
	}


}
