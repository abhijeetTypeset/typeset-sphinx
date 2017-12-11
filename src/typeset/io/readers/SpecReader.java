package typeset.io.readers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.yaml.snakeyaml.Yaml;

import typeset.io.model.spec.Spec;

public class SpecReader {

	public static Spec read(String filename) throws IOException {

		Yaml yaml = new Yaml();
		try (InputStream in = Files.newInputStream(Paths.get(filename))) {
			Spec spec = yaml.loadAs(in, Spec.class);
			return spec;

		}
	}

	
}
