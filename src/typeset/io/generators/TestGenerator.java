package typeset.io.generators;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import typeset.io.exceptions.InvalidNodeException;
import typeset.io.models.GraphNode;
import typeset.io.models.Spec;
import typeset.io.readers.SpecReader;

public class TestGenerator {
	private String outputDir;
	private GraphGenerator graphGenerator;
	private DefaultDirectedGraph<GraphNode, DefaultEdge> graph;
	private String inputDir;
	private Set<String> specFiles;

	public TestGenerator(DefaultDirectedGraph<GraphNode, DefaultEdge> graph, GraphGenerator graphGenerator,
			String inputDir, String outputDir) {
		this.graph = graph;
		this.graphGenerator = graphGenerator;
		this.inputDir = inputDir;
		this.outputDir = outputDir;
		this.specFiles = new TreeSet<String>();
	}

	public void parseSpecFile(String filename) {
		File file = new File(filename);
		if (!file.exists()) {
			System.out.println("Spec file " + filename + " does not exist, skipping.");
		}
	}

	public void getSpecFiles() {
		String specDir = inputDir + File.separator + "specs";
		System.out.println(specDir);
		File folder = new File(specDir);
		for (final File file : folder.listFiles()) {
			if (file.isFile()) {
				if (file.getAbsolutePath().endsWith(".yml")) {
					System.out.println(file.getName() + " : " + file.getAbsolutePath());
					specFiles.add(file.getAbsolutePath());
				}
			} else {
				System.out.println("sub-directories not monitored at the moment");
			}
		}
		System.out.println("Found " + specFiles.size() + " spec files");
	}

	public List<GraphPath<GraphNode, DefaultEdge>> getPaths(String srcNode, String dstNode, int maxLength) {
		GraphNode sNode = graphGenerator.getNodeByKey(srcNode);
		GraphNode dNode = graphGenerator.getNodeByKey(dstNode);

		System.out.println("sNode " + sNode);
		System.out.println("dNode " + dNode);

		if (sNode == null || dNode == null) {
			throw new InvalidNodeException("node null cannot proceed");
		}

		AllDirectedPaths<GraphNode, DefaultEdge> allDirectedPath = new AllDirectedPaths<>(graph);

		List<GraphPath<GraphNode, DefaultEdge>> paths = allDirectedPath.getAllPaths(sNode, dNode, false, maxLength);
		return paths;

	}

	public void testPath() {
		String srcNode = "page_1";
		String dstNode = "page_5";
		int maxLength = 12;

		List<GraphPath<GraphNode, DefaultEdge>> paths = getPaths(srcNode, dstNode, maxLength);
		for (GraphPath<GraphNode, DefaultEdge> path : paths) {
			System.out.println(path.getLength() + ":::" + path);
		}

	}

	public void getSpecs() {
		getSpecFiles();
		for (String sf : specFiles) {
			try {
				Spec spec = SpecReader.read(sf);
				System.out.println("Found spec : "+spec);
			} catch (IOException e) {
				System.out.println("Error parsing spec file : "+sf);
			}
		}
		
	}

}
