package typeset.io.generators;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JVar;

import typeset.io.exceptions.InvalidNodeException;
import typeset.io.exceptions.InvalidPathException;
import typeset.io.generators.helper.ScaffolingData;
import typeset.io.model.GraphNode;
import typeset.io.model.NodeType;
import typeset.io.model.spec.*;
import typeset.io.readers.SpecReader;

public class TestGenerator {
	private String outputDir;
	private GraphGenerator graphGenerator;
	private DefaultDirectedGraph<GraphNode, DefaultEdge> graph;
	private String inputDir;
	private Set<String> specFiles;
	private List<Spec> specList;
	private JCodeModel codeModel;
	private JDefinedClass definedClass;
	private JFieldRef outVar;

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

	private String getFunctionName(GraphNode srcNode, GraphNode dstNode) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String name = srcNode.getName() + "_" + dstNode.getName() + "_" + timestamp.getTime();

		return name;
	}

	private String firstLetterCaptial(String name) {
		if (name.length() <= 1) {
			return name.toUpperCase();
		} else {
			return name.substring(0, 1).toUpperCase() + name.substring(1);
		}
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

				if (isValidSpec(spec)) {
					System.out.println("Added spec : " + spec);
					specList.add(spec);
				} else {
					System.out.println("Invalid spec " + spec);
				}

			} catch (IOException e) {
				System.out.println("Error parsing spec file : " + sf);
			}
		}
	}

	private boolean isValidSpec(Spec spec) {
		// TODO implement spec validity checking here
		return true;
	}

	public GraphPath<GraphNode, DefaultEdge> getFeasiblePath(Spec spec) {
		String startScreen = spec.getGiven().getScreen();
		GraphNode rootNode = graphGenerator.getRootNode();
		GraphNode startNode = graphGenerator.getNodeByKey(startScreen);
		int minLength = 3;
		int maxLength = 15;

		for (int pathLength = minLength; pathLength <= maxLength; pathLength++) {
			List<GraphPath<GraphNode, DefaultEdge>> paths = getPaths(rootNode, startNode, pathLength);
			for (GraphPath<GraphNode, DefaultEdge> path : paths) {
				if (isPathViable(path, spec)) {
					return path;
				}
			}
		}
		return null;
	}

	private boolean isPathViable(GraphPath<GraphNode, DefaultEdge> path, Spec spec) {
		System.out.println("Received path " + path.getLength() + " " + path);

		// TODO: get this some other way
		int MAX_LENGTH = 12;

		List<String> assertions = spec.getGiven().getAssertions();
		Map<String, Action> actions = spec.getWhen();

		// condition for viability of a path
		// it should be of less than or equal to max length
		// it should invoke all assertions

		// path length check
		if (path.getLength() > MAX_LENGTH) {
			return false;
		}

		// invoke assertion check
		if (assertions != null) {
			// TODO: implement assertion checking here
		}

		return true;
	}

	public void generateTest() throws IOException, JClassAlreadyExistsException {
		for (Spec spec : specList) {
			GraphPath<GraphNode, DefaultEdge> path = getFeasiblePath(spec);
			System.out.println("Resolving spec " + spec);
			if (path != null) {
				System.out.println("Feasible path found " + path);
				generateClasses(spec, path, spec.getName());

			} else {
				System.out.println("No feasible path found");
			}

		}

	}

	private void writeTestToFile() throws IOException {
		String filepath = outputDir + File.separator + "FlyPaper" + File.separator + "src" + File.separator + "test"
				+ File.separator + "java";
		System.out.println("Generating class file " + filepath);

		File file = new File(filepath);
		file.mkdirs();
		this.codeModel.build(file);

	}

	private void generateUserSpecifiedTestScenario(Spec spec) {
		ScaffolingData sdata = createMethodScaffolding("testSpec");

		// code for executing spec action
		genearteSpecActions(sdata.getBlock(), spec.getWhen());

		// code for wait
		generateWait(sdata.getBlock(), spec.getWait());

		// code for asserting postcondition
		generatePostCondition(sdata.getBlock(), spec.getThen());
		
		addClosingAssert(sdata);

	}

	private ScaffolingData createMethodScaffolding(String methodName) {
		JMethod method = definedClass.method(JMod.PUBLIC, Void.class, methodName);
		method._throws(InterruptedException.class);
		method._throws(IOException.class);

		JBlock block = method.body();
		JVar assertVar = block.decl(this.codeModel._ref(org.testng.asserts.SoftAssert.class), "sAssert");
		JExpression init = JExpr._new(this.codeModel._ref(org.testng.asserts.SoftAssert.class));
		assertVar.init(init);

		return new ScaffolingData(method, block, assertVar);
	}

	private void navigateToRootNode(GraphNode rootNode) {
		System.out.println("Go to node " + rootNode);

		ScaffolingData sdata = createMethodScaffolding("goToRootNode");

		addClosingAssert(sdata);
	}

	private void addClosingAssert(ScaffolingData sdata) {
		JStatement statement = sdata.getAssertVar().invoke("assertAll");
		sdata.getBlock().add(statement);
		sdata.getBlock().invoke(outVar, "println").arg("=============" + sdata.getMethod().name() + "=============");
	}

	private void generatePreCondition(GraphPath<GraphNode, DefaultEdge> path) {

		System.out.println("===| Generating precondition");

		for (DefaultEdge e : path.getEdgeList()) {
			GraphNode srcNode = graph.getEdgeSource(e);
			GraphNode dstNode = graph.getEdgeTarget(e);

			String funcName = getFunctionName(srcNode, dstNode);
			ScaffolingData sdata = createMethodScaffolding(funcName);

			if (srcNode.getNodeType() == NodeType.PAGE || srcNode.getNodeType() == NodeType.SCREEN) {
				if (srcNode.getNodeType() != NodeType.PAGE) {
					System.out.println("Assert can see " + srcNode);
				} else {
					System.out.println("Assert at page " + srcNode);
				}
			} else {
				String defaultData = "";
				if (srcNode.getAction_type().toLowerCase().equals("type")) {
					defaultData = srcNode.getAction_data();
				}
				System.out.println(
						"Invoke control/widget " + srcNode + " " + srcNode.getAction_type() + " " + defaultData);
			}
			addClosingAssert(sdata);
		}
	}

	private void genearteSpecActions(JBlock jBlock, Map<String, Action> actions) {
		System.out.println("===| Generating spec action");

		for (String action_tag : actions.keySet()) {

			Action action = actions.get(action_tag);
			String actionData = "";
			if (action.getAction_type().toLowerCase().equals("type")) {
				String userProvidedData = action.getAction_data();

				if (userProvidedData != null && userProvidedData.trim().length() > 0) {
					actionData = action.getAction_data();
				} else {
					actionData = graphGenerator.getNodeByKey(action.getAction_name()).getAction_data();
				}
			}
			System.out.println("Execute " + action_tag + " " + action + " " + actionData);
		}

	}

	private void generatePostCondition(JBlock jBlock, State then) {
		System.out.println("===| Generating precondition");

		System.out.println("Assert at screen " + then.getScreen());
		List<String> explicitAssertions = then.getAssertions();
		if (explicitAssertions != null) {
			// TODO: add code for explicit assertions
		}

	}

	private void generateWait(JBlock jBlock, String wait) {
		int waitTime = 0;
		if (wait != null) {
			if (wait.toLowerCase().equals("short")) {
				waitTime = 3;
			} else if (wait.toLowerCase().equals("normal")) {
				waitTime = 5;
			} else if (wait.toLowerCase().equals("long")) {
				waitTime = 15;
			}
		}
		if (waitTime > 0) {
			System.out.println("Wait for " + waitTime + " seconds ");
		}

	}

	private void generateClasses(Spec spec, GraphPath<GraphNode, DefaultEdge> path, String testName)
			throws IOException, JClassAlreadyExistsException {
		if (path == null) {
			throw new InvalidPathException();
		}
		System.out.println("===| Generated class for " + firstLetterCaptial(testName));
		this.codeModel = new JCodeModel();
		String packageName = "tests";
		String className = packageName + "." + firstLetterCaptial(testName);
		this.definedClass = this.codeModel._class(className);

		outVar = codeModel.ref(System.class).staticRef("out");

		// TODO: extend action class
		// definedClass._extends(ActionClass.class);

		// assume all execution start at root node; TODO - put a check later

		navigateToRootNode(graphGenerator.getRootNode());

		generatePreCondition(path);

		generateUserSpecifiedTestScenario(spec);

		writeTestToFile();

	}

}
