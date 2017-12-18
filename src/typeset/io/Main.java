package typeset.io;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import com.sun.codemodel.JClassAlreadyExistsException;

import typeset.io.generators.ModelGenerator;
import typeset.io.generators.GraphGenerator;
import typeset.io.generators.TestGenerator;
import typeset.io.generators.TestNGGenerator;
import typeset.io.model.GraphNode;
import typeset.io.model.Model;

import typeset.io.model.spec.Spec;
import typeset.io.readers.ModelReader;

public class Main {

	private final static Map<String, String> paramerets = new HashMap<String, String>();

	public static void main(String[] args) throws IOException, IllegalAccessException, InvocationTargetException,
			JClassAlreadyExistsException, ParserConfigurationException, TransformerException {

		// get parameters
		getParameters(args);

		// clean the output directory
		cleanOutputDir(paramerets.get("output"));

		// read the model
		Model model = ModelReader.read(paramerets.get("input"));

		// initialize the graph
		GraphGenerator graphGenerator = new GraphGenerator(model, paramerets.get("output"));
		DefaultDirectedGraph<GraphNode, DefaultEdge> tgraph = graphGenerator.initialize();
		graphGenerator.toDot();

		// consistency checks on the graph
		graphGenerator.consistencyCheck();

		// adding implict assertions
		graphGenerator.addImplicitAssertions();

		if (!paramerets.get("generate").toLowerCase().equals("true")) {
			System.out.println("Stopping after graph generation. Classes will be not generated");
			System.exit(0);
		}

		// convert the model to Java classes
		ModelGenerator classGenerator = new ModelGenerator(tgraph, paramerets.get("output"));
		classGenerator.generateClasses();

		// covert specification to feasible paths; and then eventually into classes
		TestGenerator testGenerator = new TestGenerator(tgraph, graphGenerator, classGenerator, paramerets.get("input"),
				paramerets.get("output"));
		List<Spec> specList = testGenerator.getSpecs();
		testGenerator.generateTest();
		// testGenerator.testPath();

		// generate test classes
		TestNGGenerator testNGGenerator = new TestNGGenerator(paramerets.get("output"), specList, "FlyPaper",
				"https://typeset.io");
		testNGGenerator.generateXML();

	}

	private static void getParameters(String[] args) {
		Options options = new Options();

		Option input = new Option("i", "input", true, "input directory");
		input.setRequired(true);
		options.addOption(input);

		Option output = new Option("o", "output", true, "output directory");
		output.setRequired(true);
		options.addOption(output);

		Option generateClasses = new Option("g", "generate", false, "generate classes");
		generateClasses.setRequired(false);
		options.addOption(generateClasses);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("Sphinx", options);
			System.exit(0);
			return;
		}

		String inputFilePath = cmd.getOptionValue("input");
		String outputFilePath = cmd.getOptionValue("output");

		paramerets.put("input", inputFilePath);
		paramerets.put("output", outputFilePath);
		if (cmd.hasOption("g")) {
			paramerets.put("generate", "true");
		} else {
			paramerets.put("generate", "false");
		}

		// TODO : do sanity test of parameters here

		System.out.println(inputFilePath);
		System.out.println(outputFilePath);

	}

	public static void cleanOutputDir(String outputDir) {
		// clean the output directory
		try {
			FileUtils.deleteDirectory(new File(outputDir));
		} catch (Exception e) {

		}
	}

}
