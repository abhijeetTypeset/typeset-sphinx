package typeset.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.yaml.snakeyaml.Yaml;

import typeset.io.models.Model;

public class YamlReader {

	public static Model readModel(String filename) throws IOException {

		Yaml yaml = new Yaml();
		try (InputStream in = Files.newInputStream(Paths.get(filename))) {
			Model model = yaml.loadAs(in, Model.class);
			System.out.println(model.toString());
			return model;

		}
	}

}
