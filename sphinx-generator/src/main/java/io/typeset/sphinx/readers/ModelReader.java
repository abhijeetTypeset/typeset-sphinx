package io.typeset.sphinx.readers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;

import io.typeset.sphinx.exceptions.InvalidModelException;
import io.typeset.sphinx.model.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

/**
 * The Class for YML Model Reader.
 */
public class ModelReader {
	private static final Logger logger = LogManager.getLogger("ModelReader");

	/**
	 * Read.
	 *
	 * @param inputDir
	 *            the input directory
	 * @return the model
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static Model read() throws IOException {

		String modelFile = ConfigReader.inputDir + File.separator + "model.yml";
		logger.debug("Reading model from file " + modelFile);
		Yaml yaml = new Yaml();
		try (InputStream in = Files.newInputStream(Paths.get(modelFile))) {
			Model model = yaml.loadAs(in, Model.class);
			validateModel(model);
			return model;

		}
	}

	public static void validateModel(Model model) {
		if (model == null) {
			logger.error("model cannot be null");
			throw new InvalidModelException("model cannot be null");
		}

		Set<String> allKeys = new TreeSet();
		for (String c : model.getControls().keySet()) {
			if (allKeys.contains(c)) {
				logger.error(c + " key already used in the model");
				throw new InvalidModelException(c + " key already used in the model");
			} else {
				allKeys.add(c);
			}
		}
		for (String w : model.getWidgets().keySet()) {
			if (allKeys.contains(w)) {
				logger.error(w + " key already used in the model");
				throw new InvalidModelException(w + " key already used in the model");
			} else {
				allKeys.add(w);
			}
		}
		for (String a : model.getApps().keySet()) {
			if (allKeys.contains(a)) {
				logger.error(a + " key already used in the model");
				throw new InvalidModelException(a + " key already used in the model");
			} else {
				allKeys.add(a);
			}
		}
		for (String s : model.getScreens().keySet()) {
			if (allKeys.contains(s)) {
				logger.error(s + " key already used in the model");
				throw new InvalidModelException(s + " key already used in the model");
			} else {
				allKeys.add(s);
			}
		}
		for (String p : model.getPages().keySet()) {
			if (allKeys.contains(p)) {
				logger.error(p + " key already used in the model");
				throw new InvalidModelException(p + " key already used in the model");
			} else {
				allKeys.add(p);
			}
		}
	}

}
