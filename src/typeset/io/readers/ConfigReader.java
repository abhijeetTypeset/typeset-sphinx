package typeset.io.readers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import typeset.io.exceptions.InvalidConfigException;

public class ConfigReader {

	public static String inputDir = null;
	public static String outputDir = null;
	public static boolean generateClasses = false;

	public static void read(String filename) {

		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(filename);

			// load a properties file
			prop.load(input);

			inputDir = prop.getProperty("input-dir").trim();
			outputDir = prop.getProperty("output-dir").trim();
			String generateClassesStr = prop.getProperty("generate-classes").trim();
			if(generateClassesStr!=null && generateClassesStr.toLowerCase().equals("true")) {
				generateClasses = true;
			}
			
			if(inputDir!=null) {
				File file = new File(inputDir);
				if(!file.isDirectory()) {
					throw new InvalidConfigException("Invalid input directory "+inputDir);
				}
			}else {
				throw new InvalidConfigException("Input directory null");
			}
			
			if(outputDir!=null) {
				File file = new File(outputDir);
				if(!file.isDirectory()) {
					throw new InvalidConfigException("Invalid output directory "+outputDir);
				}
			}else {
				throw new InvalidConfigException("Input directory null");
			}
			
			// get the property value and print it out
			System.out.println(inputDir);
			System.out.println(outputDir);
			System.out.println(generateClasses);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
