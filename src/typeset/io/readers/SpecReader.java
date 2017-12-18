package typeset.io.readers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.yaml.snakeyaml.Yaml;

import typeset.io.model.spec.Spec;

/**
 * The Class for Specification Reader.
 */
public class SpecReader {

	/**
	 * Read.
	 *
	 * @param filename the filename
	 * @return the spec
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Spec read(String filename) throws IOException {

		Yaml yaml = new Yaml();
		try (InputStream in = Files.newInputStream(Paths.get(filename))) {
			Spec spec = yaml.loadAs(in, Spec.class);
			return spec;

		}
	}

	
}
