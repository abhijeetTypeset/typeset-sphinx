package typeset.io;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import com.sun.codemodel.JClassAlreadyExistsException;

import typeset.io.generators.ModelGenerator;
import typeset.io.exceptions.InvalidConfigException;
import typeset.io.generators.GraphGenerator;
import typeset.io.generators.TestGenerator;
import typeset.io.generators.TestNGGenerator;
import typeset.io.model.GraphNode;
import typeset.io.model.Model;

import typeset.io.model.spec.Spec;
import typeset.io.readers.ConfigReader;
import typeset.io.readers.ModelReader;

public class Main {
	private static final Logger logger = LogManager.getLogger("Log");

	public static void main(String[] args) throws IOException, IllegalAccessException, InvocationTargetException,
			JClassAlreadyExistsException, ParserConfigurationException, TransformerException, InvalidKeySpecException {

		// get path to config file
		String configFile = getParameters(args);
		if (configFile == null || !new File(configFile).isFile()) {
			logger.error("Config file invalid");
			throw new InvalidConfigException("Config file invalid");
		}

		// read configuration
		ConfigReader.read("config.properties");

		// clean the output directory
		cleanOutputDir();

		// read the model
		Model model = ModelReader.read();

		// initialize the graph
		GraphGenerator graphGenerator = new GraphGenerator(model);
		DefaultDirectedGraph<GraphNode, DefaultEdge> tgraph = graphGenerator.initialize();
		graphGenerator.toDot();

		// adding implicit assertions
		graphGenerator.addImplicitAssertions();

		// consistency checks on the graph
		graphGenerator.consistencyCheck();

		if (!ConfigReader.generateClasses) {
			logger.info("Stopping after graph generation. Classes will be not generated");
			System.exit(0);
		}

		// convert the model to Java classes
		ModelGenerator classGenerator = new ModelGenerator(tgraph);
		classGenerator.generateClasses();

		// covert specification to feasible paths; and then eventually into classes
		TestGenerator testGenerator = new TestGenerator(tgraph, graphGenerator, classGenerator);
		List<Spec> specList = testGenerator.getSpecs();
		testGenerator.generateTest();

		// generate test classes
		TestNGGenerator testNGGenerator = new TestNGGenerator(specList, "FlyPaper", "https://typeset.io");
		testNGGenerator.generateXML();

	}

	private static String getParameters(String[] args) {
		Options options = new Options();
		logger.debug("getting parameters");

		Option input = new Option("c", "config", true, "config file");
		input.setRequired(true);
		options.addOption(input);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			logger.info(e.getMessage());
			formatter.printHelp("Sphinx", options);
			System.exit(0);
			return null;
		}

		return cmd.getOptionValue("config");
	}

	public static void cleanOutputDir() {
		logger.debug("Cleaning output directory");
		// clean the output directory
		try {
			FileUtils.deleteDirectory(new File(ConfigReader.outputDir));
		} catch (Exception e) {
			logger.debug("Error in cleaning directory");

		}
	}

}
