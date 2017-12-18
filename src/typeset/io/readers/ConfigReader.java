package typeset.io.readers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import typeset.io.exceptions.InvalidConfigException;

public class ConfigReader {

	public static String inputDir = null;
	public static String outputDir = null;
	public static boolean generateClasses = false;
	public static List<String> pageImplictFunc = new ArrayList<String>();
	public static List<String> intermImplictFunc = new ArrayList<String>();
	public static List<String> controlImplictFunc = new ArrayList<String>();

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
			if (generateClassesStr != null && generateClassesStr.toLowerCase().equals("true")) {
				generateClasses = true;
			}

			if (inputDir != null) {
				File file = new File(inputDir);
				if (!file.isDirectory()) {
					throw new InvalidConfigException("Invalid input directory " + inputDir);
				}
			} else {
				throw new InvalidConfigException("Input directory null");
			}

			if (outputDir == null) {
				throw new InvalidConfigException("Input directory null");
			}

			String implicit_str = prop.getProperty("page-implicit").trim();
			List<String> tempList = listify(implicit_str);
			if (tempList != null) {
				pageImplictFunc.addAll(tempList);
			}
			if (pageImplictFunc.size() <= 0) {
				throw new InvalidConfigException("Insufficient number of page assertions provided");
			}

			implicit_str = prop.getProperty("interm-implicit").trim();
			tempList = listify(implicit_str);
			if (tempList != null) {
				intermImplictFunc.addAll(tempList);
			}
			if (intermImplictFunc.size() <= 0) {
				throw new InvalidConfigException("Insufficient number of screen/app/widget assertions provided");
			}

			implicit_str = prop.getProperty("control-implicit").trim();
			tempList = listify(implicit_str);
			if (tempList != null) {
				controlImplictFunc.addAll(tempList);
			}
			if (controlImplictFunc.size() <= 0) {
				throw new InvalidConfigException("Insufficient number of control assertions provided");
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (NullPointerException ex) {
			throw new InvalidConfigException();
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

	private static List<String> listify(String implicit_str) {
		if (implicit_str == null) {
			return null;
		}

		List<String> tempList = new ArrayList<String>();
		String[] list = implicit_str.split(",");
		for (String l : list) {
			tempList.add(l.trim());
		}
		return tempList;
	}

}
