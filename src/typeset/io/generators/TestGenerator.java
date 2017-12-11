package typeset.io.generators;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

import typeset.io.exceptions.InvalidNodeException;
import typeset.io.exceptions.InvalidPathException;
import typeset.io.models.GraphNode;
import typeset.io.models.NodeType;
import typeset.io.models.Spec;
import typeset.io.readers.SpecReader;

public class TestGenerator {
	private String outputDir;
	private GraphGenerator graphGenerator;
	private DefaultDirectedGraph<GraphNode, DefaultEdge> graph;
	private String inputDir;
	private Set<String> specFiles;
	private List<Spec> specList;

	public TestGenerator(DefaultDirectedGraph<GraphNode, DefaultEdge> graph, GraphGenerator graphGenerator,
			String inputDir, String outputDir) {
		this.graph = graph;
		this.graphGenerator = graphGenerator;
		this.inputDir = inputDir;
		this.outputDir = outputDir;
		this.specFiles = new TreeSet<String>();
		this.specList = new ArrayList<Spec>();
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

	public List<GraphPath<GraphNode, DefaultEdge>> getPaths(GraphNode sNode, GraphNode dNode, int maxLength) {

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
		GraphNode sNode = graphGenerator.getNodeByKey(srcNode);
		GraphNode dNode = graphGenerator.getNodeByKey(dstNode);

		List<GraphPath<GraphNode, DefaultEdge>> paths = getPaths(sNode, dNode, maxLength);
		for (GraphPath<GraphNode, DefaultEdge> path : paths) {
			System.out.println(path.getLength() + ":::" + path);
		}
	}

	public void getSpecs() {
		getSpecFiles();
		for (String sf : specFiles) {
			try {
				Spec spec = SpecReader.read(sf);
				specList.add(spec);
				System.out.println("Found spec : " + spec);
			} catch (IOException e) {
				System.out.println("Error parsing spec file : " + sf);
			}
		}
	}

	public GraphPath<GraphNode, DefaultEdge> getFeasiblePath(Spec spec) {
		String startScreen = spec.getGiven().getScreen();
		GraphNode rootNode = graphGenerator.getRootNode();
		GraphNode startNode = graphGenerator.getNodeByKey(startScreen);
		int minLength = 10;
		int maxLength = 15;

		for (int pathLength = minLength; pathLength <= maxLength; pathLength++) {
			List<GraphPath<GraphNode, DefaultEdge>> paths = getPaths(rootNode, startNode, pathLength);
			for (GraphPath<GraphNode, DefaultEdge> path : paths) {
				if(isPathViable(path, spec.getGiven().getAssertions())) {
					return path;
				}
			}
		}
		return null;
	}

	private boolean isPathViable(GraphPath<GraphNode, DefaultEdge> path, List<String> assertions) {
		System.out.println("Received path "+path.getLength()+" "+path);
		// DUMMY
		if(path.getLength()<12) {
			return false;
		}
		return true;
	}

	public void generateTest() throws IOException, JClassAlreadyExistsException {
		for (Spec spec : specList) {
			GraphPath<GraphNode, DefaultEdge> path = getFeasiblePath(spec);
			System.out.println("Resolving spec "+spec);
			if(path != null) {
				System.out.println("Feasible path found "+path);
				generateClasses(path, spec.getName());
				
			}else {
				System.out.println("No feasible path found");
			}
			
		}

	}

	private void generateClasses(GraphPath<GraphNode, DefaultEdge> path, String testName) throws IOException, JClassAlreadyExistsException {
		if (path == null) {
			throw new InvalidPathException();
		}
		System.out.println("===| Generated class for " + firstLetterCaptial(testName));
		JCodeModel cm = new JCodeModel();
		String packageName = "model.tests";
		String className = packageName + "." + firstLetterCaptial(testName);
		JDefinedClass definedClass = cm._class(className);
		//definedClass._extends(ActionClass.class);
		
		//assume all execution start at root node; TODO - put a check later
		System.out.println("Go to node "+graphGenerator.getRootNode());
		
		for(DefaultEdge e : path.getEdgeList()) {
			GraphNode srcNode = graph.getEdgeSource(e);
			GraphNode dstNode = graph.getEdgeTarget(e);
			if(srcNode.getNodeType()!=NodeType.CONTROL) {
				if(srcNode.getNodeType()!=NodeType.PAGE) {
					System.out.println("Assert can see "+srcNode);
				}else {
					System.out.println("Assert at page "+srcNode);
				}
			}else {
				System.out.println("Execute control "+srcNode+" "+srcNode.getActions());
			}
			

			//System.out.println(srcNode+" to "+dstNode);
			
		}
		
		String filepath = outputDir + File.separator + "FlyPaper" + File.separator + "test" + File.separator + "main"
				+ File.separator + "java";
		
		File file = new File(filepath);
		file.mkdirs();
		cm.build(file);
		
		
	}
	

	private String firstLetterCaptial(String name) {
		if (name.length() <= 1) {
			return name.toUpperCase();
		} else {
			return name.substring(0, 1).toUpperCase() + name.substring(1);
		}
	}

}
