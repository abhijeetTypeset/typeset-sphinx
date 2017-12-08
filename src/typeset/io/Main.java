package typeset.io;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

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
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.Multigraph;

import com.sun.codemodel.JClassAlreadyExistsException;

import typeset.io.generators.Generator;
import typeset.io.models.GraphNode;
import typeset.io.models.Model;
import typeset.io.models.NodeType;
import typeset.io.generators.TypesetGraph;

public class Main {

	private final static Map<String, String> paramerets = new HashMap<String, String>();

	public static void main(String[] args)
			throws IOException, IllegalAccessException, InvocationTargetException, JClassAlreadyExistsException {

		// get parameters
		getParameters(args);
		
		// clean the output directory
		cleanOutputDir(paramerets.get("output"));

		// read the model
		Model model = YamlReader.readModel(paramerets.get("input"));

		// initialize the graph
		TypesetGraph typesetGraph = new TypesetGraph(model, paramerets.get("output"));
		DefaultDirectedGraph<GraphNode, DefaultEdge> tgraph = typesetGraph.initialize();
		typesetGraph.toDot();


		typesetGraph.consistencyCheck();
		typesetGraph.addImplicitAssertions();

		// TODO: check if it is consistent
		// 1. Isolated nodes not allowed
		// 2. Referencing of non-existing nodes not allowed
		// 3. Nodes must have respective properties initialized

		// convert the model to Java classes
		Generator generator = new Generator(tgraph, paramerets.get("output"));
		generator.generateClasses();

	}

	private static void getParameters(String[] args) {
		Options options = new Options();

		Option input = new Option("i", "input", true, "input file path");
		input.setRequired(true);
		options.addOption(input);

		Option output = new Option("o", "output", true, "output file");
		output.setRequired(true);
		options.addOption(output);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("Model-Generator", options);

			System.exit(1);
			return;
		}

		String inputFilePath = cmd.getOptionValue("input");
		String outputFilePath = cmd.getOptionValue("output");

		paramerets.put("input", cmd.getOptionValue("input"));
		paramerets.put("output", cmd.getOptionValue("output"));

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
