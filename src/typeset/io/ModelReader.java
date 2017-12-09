package typeset.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.yaml.snakeyaml.Yaml;

import typeset.io.models.Model;

public class ModelReader {

	public static Model readModel(String inputDir) throws IOException {

		String modelFile = inputDir + File.separator + "model.yml";
		Yaml yaml = new Yaml();
		try (InputStream in = Files.newInputStream(Paths.get(modelFile))) {
			Model model = yaml.loadAs(in, Model.class);
			return model;

		}
	}

}
