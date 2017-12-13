package typeset.io.generators;

import java.io.File;
import java.io.IOException;

import java.sql.Timestamp;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
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
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

import typeset.io.exceptions.InvalidNodeException;
import typeset.io.exceptions.InvalidPathException;
import typeset.io.generators.helper.ScaffolingData;
import typeset.io.generators.util.GeneratorUtilities;
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
	private ModelGenerator classGenerator;
	private Map<GraphNode, JFieldVar> definedPages = new HashMap<>();
	private Stack<GraphNode> stack = new Stack<GraphNode>();
	private JFieldVar activePageVariable = null;
	private Map<String, GraphNode> usedPages;

	public TestGenerator(DefaultDirectedGraph<GraphNode, DefaultEdge> graph, GraphGenerator graphGenerator,
			ModelGenerator classGenerator, String inputDir, String outputDir) {
		this.graph = graph;
		this.graphGenerator = graphGenerator;
		this.inputDir = inputDir;
		this.outputDir = outputDir;
		this.classGenerator = classGenerator;
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

	private ScaffolingData createMethodScaffolding(String methodName, boolean addAssert) {
		JMethod method = definedClass.method(JMod.PUBLIC, JType.parse(codeModel, "void"), methodName);
		method._throws(InterruptedException.class);
		method._throws(IOException.class);
		JVar assertVar = null;
		JBlock block = method.body();
		if (addAssert) {
			assertVar = block.decl(this.codeModel._ref(org.testng.asserts.SoftAssert.class), "sAssert");
			JExpression init = JExpr._new(this.codeModel._ref(org.testng.asserts.SoftAssert.class));
			assertVar.init(init);
		}

		return new ScaffolingData(method, block, assertVar);
	}

	private void addClosingAssert(ScaffolingData sdata) {
		JStatement statement = sdata.getAssertVar().invoke("assertAll");
		sdata.getBlock().add(statement);
		sdata.getBlock().invoke(outVar, "println").arg("=============" + sdata.getMethod().name() + "=============");
	}

	private ScaffolingData genearteSpecActions(Map<String, Action> actions) {
		ScaffolingData sdata = createMethodScaffolding("when", true);

		for (String action_tag : actions.keySet()) {

			Action action = actions.get(action_tag);
			GraphNode actionNode = graphGenerator.getNodeByKey(action.getAction_name());

			String actionData = "";
			if (action.getAction_type().toLowerCase().equals("type")) {
				String userProvidedData = action.getAction_data();

				if (userProvidedData != null && userProvidedData.trim().length() > 0) {
					actionData = action.getAction_data();
				} else {
					actionData = graphGenerator.getNodeByKey(action.getAction_name()).getAction_data();
				}

				invoke_element(sdata, actionNode, actionData);
				System.out.println("Execute " + action_tag + " " + action + " with " + actionData);
			} else {

				invoke_element(sdata, actionNode, actionNode.getAction_data());
				System.out.println("Execute " + action_tag + " " + action);
			}
		}
		// add closing asserts
		addClosingAssert(sdata);

		return sdata;
	}

	private void setActivePage(GraphNode pageNode) {
		activePageVariable = definedPages.get(pageNode);
		lightenStack(NodeType.PAGE);
	}

	private ScaffolingData generatePostCondition(State then) {
		ScaffolingData sdata = createMethodScaffolding("then", true);

		GraphNode pageNode = usedPages.get(then.getScreen());
		setActivePage(pageNode);

		// assert that we are on page
		assert_element(sdata, pageNode.getImplictAssertions().get(0));

		// assert that we are on screen
		GraphNode screenNode = graphGenerator.getNodeByKey(then.getScreen());
		assert_element(sdata, screenNode);

		List<String> explicitAssertions = then.getAssertions();
		if (explicitAssertions != null) {
			// TODO: add code for explicit assertions
		}

		// add closing asserts
		addClosingAssert(sdata);

		return sdata;

	}

	private void assert_element(ScaffolingData sdata, String functionName) {
		JExpression jexpr = JExpr.invoke(activePageVariable, "getId");
		sdata.getBlock().invoke(functionName).arg(jexpr);
	}

	private void assert_element(ScaffolingData sdata, GraphNode activeNode) {
		JInvocation assertStatement = sdata.getBlock().invoke(sdata.getAssertVar(), "assertTrue");
		JInvocation invokeStatement = sdata.getBlock().invoke(activeNode.getImplictAssertions().get(0));
		JExpression argumentExpr = null;
		boolean flag = true;

		for (GraphNode stackNode : stack) {
			if (flag) {
				argumentExpr = JExpr.invoke(activePageVariable, GeneratorUtilities.getGetterName(stackNode.getName()));
				flag = false;
			} else {
				argumentExpr = JExpr.invoke(argumentExpr, GeneratorUtilities.getGetterName(stackNode.getName()));
			}
		}
		String getterName = GeneratorUtilities.getGetterName(activeNode.getName());
		if (flag) {
			argumentExpr = JExpr.invoke(activePageVariable, getterName);

		} else {
			argumentExpr = JExpr.invoke(argumentExpr, getterName);
		}
		argumentExpr = JExpr.invoke(argumentExpr, "getId");
		invokeStatement.arg(argumentExpr);
		assertStatement.arg(invokeStatement);
		updateStack(activeNode);
	}

	private boolean isTypeText(String actionType) {
		if (actionType.toLowerCase().equals("type")) {
			return true;
		}
		return false;
	}

	private void invoke_element(ScaffolingData sdata, GraphNode activeNode, String actionData) {
		JInvocation invokeStatement = sdata.getBlock().invoke(activeNode.getAction_type());
		JExpression argumentExpr = null;
		boolean flag = true;

		for (GraphNode stackNode : stack) {
			if (flag) {
				argumentExpr = JExpr.invoke(activePageVariable, GeneratorUtilities.getGetterName(stackNode.getName()));
				flag = false;
			} else {
				argumentExpr = JExpr.invoke(argumentExpr, GeneratorUtilities.getGetterName(stackNode.getName()));
			}
		}
		String getterName = GeneratorUtilities.getGetterName(activeNode.getName());
		if (flag) {
			argumentExpr = JExpr.invoke(activePageVariable, getterName);

		} else {
			argumentExpr = JExpr.invoke(argumentExpr, getterName);
		}
		argumentExpr = JExpr.invoke(argumentExpr, "getId");
		if (isTypeText(activeNode.getAction_type())) {
			invokeStatement.arg(argumentExpr).arg(actionData);
		} else {
			invokeStatement.arg(argumentExpr);
		}
		updateStack(activeNode);
		sdata.getBlock().invoke(outVar, "println")
				.arg("=============" + activeNode.getAction_type() + " " + activeNode.getName() + "=============");

	}

	private void generateWait(ScaffolingData sdata, String wait) {
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
			sdata.getBlock().invoke(outVar, "println")
					.arg("=============" + "Waiting for " + waitTime + " seconds " + "=============");
			JExpression expr = JExpr.lit(waitTime);
			sdata.getBlock().invoke("wait").arg(expr);
		}

	}

	private Map<String, GraphNode> getUsedPages(GraphPath<GraphNode, DefaultEdge> path, Spec spec) {
		Map<String, GraphNode> usedPages = new HashMap<>();

		for (DefaultEdge e : path.getEdgeList()) {
			GraphNode srcNode = graph.getEdgeSource(e);
			GraphNode dstNode = graph.getEdgeTarget(e);

			if (srcNode.getNodeType() == NodeType.PAGE) {
				usedPages.put(dstNode.getName(), srcNode);
			}
		}
		String lastScreen = spec.getThen().getScreen();
		String lastPage = graphGenerator.getScreenToPage().get(lastScreen);

		System.out.println("last page node is " + lastPage);
		usedPages.put(lastScreen, graphGenerator.getNodeByKey(lastPage));

		return usedPages;

	}

	private void generateFieldVariables() {
		for (String key : usedPages.keySet()) {

			GraphNode pageNode = usedPages.get(key);
			JDefinedClass pageClass = classGenerator.getNodeClassMap().get(pageNode);

			JFieldVar pageField = this.definedClass.field(JMod.FINAL, pageClass, "field" + pageNode.getName(),
					JExpr._new(pageClass));
			definedPages.put(pageNode, pageField);
		}

	}

	private void lightenStack(NodeType nodeType) {
		while (!stack.isEmpty() && GeneratorUtilities.getNodeType(stack.peek().getNodeType()) > GeneratorUtilities
				.getNodeType(nodeType)) {
			GraphNode popedNode = stack.pop();
			System.out.println("Popped " + popedNode);
		}
	}

	private void updateStack(GraphNode graphNode) {
		if (graphNode.getNodeType() == NodeType.CONTROL) {
			return;
		}
		lightenStack(graphNode.getNodeType());
		stack.push(graphNode);
	}

	private ScaffolingData generatePrecondition(GraphPath<GraphNode, DefaultEdge> path) {

		ScaffolingData sdata = createMethodScaffolding("given", true);

		// go to homepage
		JExpression url = JExpr.lit(graphGenerator.getRootNode().getUrl());
		sdata.getBlock().invoke("goToPage").arg(url);

		GraphNode lastNode = null;
		for (DefaultEdge e : path.getEdgeList()) {
			GraphNode srcNode = graph.getEdgeSource(e);
			lastNode = graph.getEdgeTarget(e);
			if (srcNode.getNodeType() == NodeType.PAGE) {
				setActivePage(srcNode);
				assert_element(sdata, srcNode.getImplictAssertions().get(0));
			} else if (srcNode.getNodeType() == NodeType.SCREEN) {
				assert_element(sdata, srcNode);
			} else if (srcNode.getNodeType() == NodeType.APP) {
				assert_element(sdata, srcNode);
			} else if (srcNode.getNodeType() == NodeType.WIDGET) {
				assert_element(sdata, srcNode);
			} else {
				invoke_element(sdata, srcNode, srcNode.getAction_data());
			}
		}
		assert_element(sdata, lastNode);

		// add closing asserts
		addClosingAssert(sdata);

		return sdata;
	}

	private void call(ScaffolingData sdata, ScaffolingData givenSdata) {

		sdata.getBlock().invoke(givenSdata.getMethod());
	}

	private void updateTestExecutorClasses() throws IOException {
		String dirpath = outputDir + File.separator + "FlyPaper" + File.separator + "src" + File.separator + "main"
				+ File.separator + "java" ;
		
		FileUtils.copyFile(new File(dirpath + File.separator + "utils" + File.separator + "ConfigClass.java"), new File(dirpath + File.separator + "utils" + File.separator + "Config.java"));
		FileUtils.copyFile(new File(dirpath + File.separator + "controller" + File.separator + "ActionClass.java"), new File(dirpath + File.separator + "controller" + File.separator + "Action.java"));

	}

	private void generateClasses(Spec spec, GraphPath<GraphNode, DefaultEdge> path, String testName)
			throws IOException, JClassAlreadyExistsException {
		if (path == null) {
			throw new InvalidPathException();
		}
		System.out.println("===| Generated class for " + GeneratorUtilities.firstLetterCaptial(testName));
		this.codeModel = new JCodeModel();
		String packageName = "tests";
		String className = packageName + "." + GeneratorUtilities.firstLetterCaptial(testName);
		this.definedClass = this.codeModel._class(className);
		definedClass._extends(classGenerator.getActionClass());

		outVar = codeModel.ref(System.class).staticRef("out");
		usedPages = getUsedPages(path, spec);
		generateFieldVariables();
		ScaffolingData sdata = createMethodScaffolding(spec.getName(), false);

		// generate GIVEN
		ScaffolingData givenSdata = generatePrecondition(path);

		call(sdata, givenSdata);

		// generate WHEN
		ScaffolingData whenSdata = genearteSpecActions(spec.getWhen());

		call(sdata, whenSdata);

		// generate WAIT
		generateWait(sdata, spec.getWait());

		// generate THEN
		ScaffolingData thenSdata = generatePostCondition(spec.getThen());

		call(sdata, thenSdata);

		writeTestToFile();

		updateTestExecutorClasses();

	}

}
