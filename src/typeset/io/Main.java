package typeset.io;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.plaf.synth.SynthSplitPaneUI;

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
		
		typesetGraph.testPath();		
		typesetGraph.consistencyCheck();
		typesetGraph.addImplicitAssertions();
		
		if (!paramerets.get("generate").toLowerCase().equals("true")){
			System.out.println("Stopping after graph generation. Classes will be not generated");
			System.exit(0);
		}

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
		if (cmd.hasOption("g")){
			paramerets.put("generate", "true");
		}else {
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
