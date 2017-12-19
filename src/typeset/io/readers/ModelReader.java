package typeset.io.readers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import typeset.io.model.Model;

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
			return model;

		}
	}

}
