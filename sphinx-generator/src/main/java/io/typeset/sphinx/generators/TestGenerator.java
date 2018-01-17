package io.typeset.sphinx.generators;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;

import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import io.typeset.sphinx.generators.ds.ScaffolingData;
import io.typeset.sphinx.generators.util.GeneratorUtilities;
import io.typeset.sphinx.model.GraphNode;
import io.typeset.sphinx.model.NodeType;
import io.typeset.sphinx.model.spec.Action;
import io.typeset.sphinx.model.spec.Spec;
import io.typeset.sphinx.model.spec.State;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import io.typeset.sphinx.exceptions.InvalidLiteralException;
import io.typeset.sphinx.exceptions.InvalidNodeException;
import io.typeset.sphinx.exceptions.InvalidPathException;
import io.typeset.sphinx.exceptions.InvalidPostSpec;
import io.typeset.sphinx.exceptions.InvalidStackStateException;
import io.typeset.sphinx.exceptions.TooComplexExpression;
import io.typeset.sphinx.model.assertions.Clause;
import io.typeset.sphinx.model.assertions.ExplicitAssertion;
import io.typeset.sphinx.model.assertions.Literal;
import io.typeset.sphinx.readers.ConfigReader;
import io.typeset.sphinx.readers.SpecReader;

public class TestGenerator {
	private String outputDir;
	private GraphGenerator graphGenerator;
	private DefaultDirectedGraph<GraphNode, DefaultEdge> graph;
	private String inputDir;

	private JFieldRef outVar;
	private ModelGenerator classGenerator;

	// program state
	private Map<GraphNode, JFieldVar> definedPages = null;
	private Map<JFieldVar, GraphNode> fieldVarToNodeMap = null;
	private Stack<GraphNode> stack = null;
	private JFieldVar activePageVariable = null;
	private Map<String, GraphNode> usedPages = null;

	private AllDirectedPaths<GraphNode, DefaultEdge> allDirectedPath;
	private static final Logger logger = LogManager.getLogger("TestGenerator");
	private Map<String, Spec> specMap = new HashMap<String, Spec>();
	private Map<String, String> generatedTests = new HashMap<>();
	private int specChainCounter = 0;
	private Set<String> enabledSpecs;

	// TODO: get this some other way
	private int MAX_LENGTH = 25;
	private String defaultElementNumber = "0";

	private Set<String> getEnabledSpecs() {
		Set<String> enabledSpecs = new HashSet<String>();
		String enabledSpecsDir = inputDir + File.separator + "specs-enabled";
		File folder = new File(enabledSpecsDir);
		for (final File file : folder.listFiles()) {
			if (file.getAbsolutePath().endsWith(".yml")) {
				logger.info(file.getName() + ":" + file.getAbsolutePath());
				enabledSpecs.add(file.getName());
			}
		}
		logger.info("Found the following enabled specs: " + enabledSpecs);
		return enabledSpecs;
	}

	public TestGenerator(DefaultDirectedGraph<GraphNode, DefaultEdge> graph, GraphGenerator graphGenerator,
			ModelGenerator classGenerator) {
		this.graph = graph;
		this.graphGenerator = graphGenerator;
		this.inputDir = ConfigReader.inputDir;
		this.outputDir = ConfigReader.outputDir;
		this.classGenerator = classGenerator;
		this.allDirectedPath = new AllDirectedPaths<>(graph);
		this.enabledSpecs = getEnabledSpecs();
	}

	public void parseSpecFile(String filename) {
		File file = new File(filename);
		if (!file.exists()) {
			logger.info("Spec file " + filename + " does not exist, skipping.");
		}
	}

	public Map<String, String> getSpecFiles() {
		Map<String, String> specFiles = new HashMap<>();
		String specDir = inputDir + File.separator + "specs";
		logger.info(specDir);
		File folder = new File(specDir);
		for (final File file : folder.listFiles()) {
			if (file.isFile()) {
				if (file.getAbsolutePath().endsWith(".yml")) {
					logger.info(file.getName() + " : " + file.getAbsolutePath());
					specFiles.put(file.getName(), file.getAbsolutePath());
				}
			} else {
				logger.info("sub-directories not monitored at the moment");
			}
		}
		logger.info("Found " + specFiles.size() + " spec files");
		return specFiles;
	}

	public List<GraphPath<GraphNode, DefaultEdge>> getPaths(GraphNode sNode, GraphNode dNode, int maxLength) {

		if (sNode == null || dNode == null) {
			throw new InvalidNodeException("node null cannot proceed");
		}

		// AllDirectedPaths<GraphNode, DefaultEdge> allDirectedPath = new
		// AllDirectedPaths<>(graph);

		List<GraphPath<GraphNode, DefaultEdge>> paths = allDirectedPath.getAllPaths(sNode, dNode, false, maxLength);
		return paths;

	}

	public List<Spec> getSpecs() {

		List<Spec> specList = new ArrayList<>();
		Map<String, String> specFiles = getSpecFiles();
		for (String skey : specFiles.keySet()) {
			String sf = specFiles.get(skey);
			try {
				Spec spec = SpecReader.read(sf);

				ExplicitAssertion eassertThen = graphGenerator.parsePrecondition(spec.getThen().getAssertions());
				spec.getThen().setParsedAssertion(eassertThen);

				ExplicitAssertion eassertGiven = graphGenerator.parsePrecondition(spec.getGiven().getAssertions());
				spec.getGiven().setParsedAssertion(eassertGiven);

				if (isValidSpec(spec)) {
					logger.info("Found spec : " + spec);
					if (isSpecEnabled(skey)) {
						logger.info("Adding enabled spec to specList: " + sf);
						specList.add(spec);
					}

				} else {
					logger.info("Invalid spec " + spec);
				}
				specMap.put(skey, spec);
			} catch (IOException e) {
				logger.info("Error parsing spec file : " + sf);
			}
		}

		return specList;
	}

	/**
	 * Checks if the spec has been enabled for testing or not. Checks if the passed
	 * in file name is found in the specs-enabled folder.
	 * 
	 * @param specFileName
	 *            - the spec file name
	 * @return whether spec is enabled or not
	 */
	private boolean isSpecEnabled(String specFileName) {
		return enabledSpecs.contains(specFileName);
	}

	private boolean isTopLevelTest(String sf) {
		for (String test : ConfigReader.tests) {
			if (test.trim().length() <= 0) {
				continue;
			}
			System.out.println(test + " : " + sf);
			if (sf.endsWith(test)) {
				return true;
			}
		}
		return false;
	}

	private boolean isValidSpec(Spec spec) {
		// check if pre-condition is valid
		State given = spec.getGiven();

		// this will throw an error if invalid screen provided
		graphGenerator.getNodeByKey(given.getScreen());

		if (given.getParsedAssertion() != null) {
			for (Clause cls : given.getParsedAssertion().getclauses()) {
				for (Literal ltl : cls.getLiterals()) {
					if (!graphGenerator.isValidAction(ltl.getNode(), ltl.getAction())) {
						throw new InvalidLiteralException(
								"The action " + ltl.getAction() + " is not defined for " + ltl.getNode());
					}
				}
			}

		}

		// check is actions is valid
		for (String key : spec.getWhen().keySet()) {
			Action action = spec.getWhen().get(key);
			if (action.getAction_no() == null) {
				throw new InvalidLiteralException("no action number provided");
			}
			try {
				int actionNo = Integer.parseInt(action.getAction_no());
			} catch (Exception e) {
				throw new InvalidLiteralException("action no is not parsable");
			}
		}

		// check if post condition is valid
		State then = spec.getThen();

		// this will throw an error if invalid screen provided
		graphGenerator.getNodeByKey(then.getScreen());

		if (then.getParsedAssertion() != null) {
			for (Clause cls : then.getParsedAssertion().getclauses()) {
				for (Literal ltl : cls.getLiterals()) {
					if (!graphGenerator.isValidAction(ltl.getNode(), ltl.getAction())) {
						throw new InvalidLiteralException(
								"The action " + ltl.getAction() + " is not defined for " + ltl.getNode());
					}

				}
			}

		}

		return true;
	}

	public GraphPath<GraphNode, DefaultEdge> getFeasiblePath(Spec spec) {
		String startScreen = spec.getGiven().getScreen();
		GraphNode rootNode = graphGenerator.getRootNode();
		GraphNode startNode = graphGenerator.getNodeByKey(startScreen);
		int minLength = 3;

		for (int pathLength = minLength; pathLength <= MAX_LENGTH; pathLength++) {
			logger.info("Checking paths for length " + pathLength);
			List<GraphPath<GraphNode, DefaultEdge>> paths = getPaths(rootNode, startNode, pathLength);
			if (paths != null) {

				for (GraphPath<GraphNode, DefaultEdge> path : paths) {
					// logger.info(path.getLength()+" paths "+path);
					if (isPathViable(path, spec)) {
						return path;
					}
				}
			}
		}

		return null;
	}

	private boolean isPathViable(GraphPath<GraphNode, DefaultEdge> path, Spec spec) {

		List<String> assertions = spec.getGiven().getAssertions();

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

		// check precondition on paths
		List<GraphNode> nodesToHere = new ArrayList<GraphNode>();
		for (DefaultEdge e : path.getEdgeList()) {
			GraphNode srcNode = graph.getEdgeSource(e);

			if (srcNode.getNodeType() == NodeType.CONTROL) {
				ExplicitAssertion precondition = srcNode.getParsedPreCondition();
				// logger.info("Constrain on " + srcNode + " ; " + precondition);
				if (!satisfiesPrecondition(nodesToHere, precondition)) {
					// logger.info(path + " does not satisfies precondition on node " +
					// srcNode);
					return false;
				}
			}
			nodesToHere.add(srcNode);
		}

		return true;
	}

	private boolean satisfiesPrecondition(List<GraphNode> nodesToHere, ExplicitAssertion precondition) {
		if (precondition != null) {
			checkExpressionComplexity(precondition);
			// currently we only support very simple (single clause, single literal)
			// preconditions

			GraphNode constrainingNode = precondition.getclauses().get(0).getLiterals().get(0).getNode();
			if (nodesToHere.contains(constrainingNode)) {
				logger.info("Contraint satisfied found " + constrainingNode);
				return true;
			}
			return false;

		} else {
			return true;
		}
	}

	private void checkExpressionComplexity(ExplicitAssertion precondition) {
		List<Clause> clauses = precondition.getclauses();
		if (clauses.size() > 1) {
			throw new TooComplexExpression(precondition.toString());
		}
		List<Literal> literals = clauses.get(0).getLiterals();
		if (literals.size() > 1) {
			throw new TooComplexExpression(precondition.toString());
		}
	}

	private void writeTestToFile(JCodeModel cModel, JDefinedClass definedClass) throws IOException {
		String filepath = outputDir + File.separator + "java";
		logger.info("Generating class file " + filepath);
		System.out.println("Generating class " + definedClass.name() + " ; " + definedClass.fullName());

		generatedTests.put(definedClass.fullName(), "execute");
		File file = new File(filepath);
		file.mkdirs();
		cModel.build(file);

	}

	private ScaffolingData createMethodScaffolding(JCodeModel codeModel, JDefinedClass definedClass, String methodName,
			boolean addAssert) {
		JMethod method = definedClass.method(JMod.PUBLIC, JType.parse(codeModel, "void"), methodName);
		method._throws(InterruptedException.class);
		method._throws(IOException.class);
		JVar assertVar = null;
		JBlock block = method.body();
		if (addAssert) {
			assertVar = block.decl(codeModel._ref(org.testng.asserts.SoftAssert.class), "sAssert");
			JExpression init = JExpr._new(codeModel._ref(org.testng.asserts.SoftAssert.class));
			assertVar.init(init);
		}

		return new ScaffolingData(method, block, assertVar);
	}

	private void addClosingAssert(ScaffolingData sdata) {
		JStatement statement = sdata.getAssertVar().invoke("assertAll");
		sdata.getBlock().add(statement);
		sdata.getBlock().invoke(outVar, "println").arg("=============" + sdata.getMethod().name() + "=============");
	}

	private boolean requiresData(String actionType) {
		for (String rd : ConfigReader.requiresData) {
			if (actionType.toLowerCase().equals(rd.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	private ScaffolingData generateSpecActions(JCodeModel codeModel, JDefinedClass definedClass,
			Map<String, Action> actions, String methodName) {
		ScaffolingData sdata = createMethodScaffolding(codeModel, definedClass, methodName, true);

		if (actions != null && !actions.isEmpty()) {
			for (String action_tag : actions.keySet()) {

				Action action = actions.get(action_tag);
				String invokeFunction = action.getAction_type();
				GraphNode actionNode = graphGenerator.getNodeByKey(action.getAction_name());

				String actionData = "";
				String actionNumber = action.getAction_no();
				if (requiresData(action.getAction_type())) {
					String userProvidedData = action.getAction_data();

					if (userProvidedData != null && userProvidedData.trim().length() > 0) {
						actionData = action.getAction_data();
					} else {
						actionData = graphGenerator.getNodeByKey(action.getAction_name()).getAction_data();
					}

					invoke_element(sdata, actionNode, actionData, actionNumber, invokeFunction);
					logger.info("Execute " + action_tag + " " + action + " with " + actionData);
				} else {

					invoke_element(sdata, actionNode, actionNode.getAction_data(), actionNumber, invokeFunction);
					logger.info("Execute " + action_tag + " " + action);
				}

				if (actionNode.getNodeType() != NodeType.CONTROL) {
					setActive(actionNode);
				}

				// in case action leads to somewhere, update the stack
				String leadsToNodeString = actionNode.getLeadsto();
				if (leadsToNodeString != null) {
					GraphNode leadsToNode = graphGenerator.getNodeByKey(leadsToNodeString);
					setActive(leadsToNode);
				}
			}
		}
		// add closing asserts
		addClosingAssert(sdata);

		return sdata;
	}

	private void lightenStack(NodeType nodeType) {
		while (!stack.isEmpty() && GeneratorUtilities.getNodeType(stack.peek().getNodeType()) >= GeneratorUtilities
				.getNodeType(nodeType)) {
			GraphNode popedNode = stack.pop();
			logger.info("Popped " + popedNode);
		}
		if (ConfigReader.debugMode) {
			checkStackState();
		}
	}

	private void checkStackState() {
		logger.debug("running in debug mode, checking stack");
		int lastSeen = -1;
		for (GraphNode node : stack) {
			int current = GeneratorUtilities.getNodeType(node.getNodeType());
			if (lastSeen >= current) {
				throw new InvalidStackStateException();
			} else {
				lastSeen = current;
			}
		}

	}

	private void setActive(GraphNode graphNode) {

		if (graphNode.getNodeType() == NodeType.CONTROL) {
			return;
		}

		JFieldVar pageVariable = definedPages.get(graphNode);
		if (graphNode.getNodeType() == NodeType.PAGE) {
			if (pageVariable != activePageVariable) {
				activePageVariable = definedPages.get(graphNode);
				stack.clear();
			}
			return;
		}

		lightenStack(graphNode.getNodeType());

		logger.info("Pushed to stack " + graphNode);
		stack.push(graphNode);
		logger.info("Contents of stack " + stack);
	}

	private ScaffolingData generatePostCondition(Spec spec, JCodeModel codeModel, JDefinedClass definedClass,
			State then, String methodName) {
		ScaffolingData sdata = createMethodScaffolding(codeModel, definedClass, methodName, true);

		GraphNode pageNode = usedPages.get(graphGenerator.getNodeByKey(then.getScreen()).getName());
		setActive(pageNode);
		// assert that we are on page
		assert_element(sdata, pageNode.getImplictAssertions().get(0));

		GraphNode screenNode = graphGenerator.getNodeByKey(then.getScreen());
		// assert that we are on screen
		if (needsUpdatingScreen(screenNode)) {
			setActive(screenNode);
		}

		assert_element(sdata, screenNode, null, null, defaultElementNumber);

		ExplicitAssertion explicitAssertion = then.getParsedAssertion();
		if (explicitAssertion != null) {
			generateExplicitAssertions(sdata, explicitAssertion);
		}

		// add closing asserts
		addClosingAssert(sdata);

		return sdata;

	}

	private boolean needsUpdatingScreen(GraphNode screenNode) {

		for (GraphNode node : stack) {
			if (node == screenNode) {
				return false;
			}
		}

		return true;
	}

	private void generateExplicitAssertions(ScaffolingData sdata, ExplicitAssertion parsedEXplicit) {

		for (Clause clause : parsedEXplicit.getclauses()) {
			for (Literal literal : clause.getLiterals()) {
				logger.info("explicit assertion :  " + literal);
				assert_element(sdata, literal.getNode(), literal.getAction(), literal.getTextData(),
						literal.getLiteral_no());
			}

		}

	}

	private void assert_element(ScaffolingData sdata, String functionName) {
		JInvocation assertStatement = sdata.getBlock().invoke(sdata.getAssertVar(), "assertTrue");
		JExpression getUrlExpr = JExpr.invoke(activePageVariable, "getUrl");
		JExpression atPageExpr = JExpr.invoke(functionName).arg(getUrlExpr);
		assertStatement.arg(atPageExpr);
	}

	private void assert_element(ScaffolingData sdata, GraphNode activeNode, String specAssertFunction,
			String specAssertData, String elementNumber) {
		JInvocation assertStatement = sdata.getBlock().invoke(sdata.getAssertVar(), "assertTrue");

		logger.info("asserting for element " + activeNode);
		JExpression argumentExpr = null;
		boolean flag = true;
		GraphNode lastNodePoped = null;
		for (GraphNode stackNode : stack) {
			if (stackNode == activeNode) {
				System.out.println("stack node is active node, breaking");
				break;
			}
			String getterName = GeneratorUtilities.getGetterName(stackNode.getName());
			if (flag) {
				checkInvocation(activePageVariable, getterName);
				argumentExpr = JExpr.invoke(activePageVariable, getterName);
				flag = false;
			} else {
				checkInvocation(lastNodePoped, getterName);
				argumentExpr = JExpr.invoke(argumentExpr, getterName);
			}
			logger.info("Obtained from the stack " + stackNode + " getter name " + getterName);
			lastNodePoped = stackNode;
		}
		String getterName = GeneratorUtilities.getGetterName(activeNode.getName());
		if (flag) {
			checkInvocation(activePageVariable, getterName);
			argumentExpr = JExpr.invoke(activePageVariable, getterName);
		}

		if (activeNode.getNodeType() == NodeType.CONTROL || activeNode.getNodeType() == NodeType.WIDGET) {
			checkInvocation(lastNodePoped, getterName);
			argumentExpr = JExpr.invoke(argumentExpr, getterName);
		}

		argumentExpr = JExpr.invoke(argumentExpr, "getId");

		if (specAssertFunction == null) {
			specAssertFunction = activeNode.getImplictAssertions().get(0);
		}

		JExpression assertExpr = null;
		if (requiresDataArgument(specAssertFunction)) {
			assertExpr = JExpr.invoke(specAssertFunction).arg(argumentExpr).arg(specAssertData).arg(elementNumber);
		} else {
			assertExpr = JExpr.invoke(specAssertFunction).arg(argumentExpr).arg(elementNumber);
		}
		assertStatement.arg(assertExpr);
	}

	private void checkInvocation(GraphNode lastNodePoped, String getterName) {
		if (lastNodePoped == null) {
			String message = "cannot invoke " + getterName + " on node null";
			throw new InvalidPathException(message);
		}

		if (!classGenerator.containsGetter(lastNodePoped, getterName)) {
			String message = lastNodePoped.getName() + " does not have any getter named " + getterName;
			throw new InvalidPathException(message);
		} else {
			System.out.println(lastNodePoped.getName() + " has a getter named " + getterName);
		}

	}

	private void checkInvocation(JFieldVar varName, String getterName) {
		GraphNode activeNode = fieldVarToNodeMap.get(varName);
		if (!classGenerator.containsGetter(activeNode, getterName)) {
			String message = activeNode.getName() + " does not have any getter named " + getterName;
			throw new InvalidPathException(message);
		} else {
			System.out.println(activeNode.getName() + " has a getter named " + getterName);
		}
	}

	private boolean requiresDataArgument(String actionType) {
		for (String rd : ConfigReader.requiresData) {
			if (actionType.toLowerCase().equals(rd.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	private void invoke_element(ScaffolingData sdata, GraphNode activeNode, String actionData, String elementNumber,
			String invokeFunction) {
		JInvocation invokeStatement = sdata.getBlock().invoke(invokeFunction);
		JExpression argumentExpr = null;
		boolean flag = true;
		GraphNode lastNodePoped = null;
		for (GraphNode stackNode : stack) {
			if (stackNode == activeNode) {
				System.out.println("stack node is active node, breaking");
				break;
			}
			if (flag) {
				checkInvocation(activePageVariable, GeneratorUtilities.getGetterName(stackNode.getName()));
				argumentExpr = JExpr.invoke(activePageVariable, GeneratorUtilities.getGetterName(stackNode.getName()));
				flag = false;
			} else {
				checkInvocation(lastNodePoped, GeneratorUtilities.getGetterName(stackNode.getName()));
				argumentExpr = JExpr.invoke(argumentExpr, GeneratorUtilities.getGetterName(stackNode.getName()));
			}
			lastNodePoped = stackNode;
		}
		String getterName = GeneratorUtilities.getGetterName(activeNode.getName());
		if (flag) {
			checkInvocation(activePageVariable, getterName);
			argumentExpr = JExpr.invoke(activePageVariable, getterName);

		} else {
			checkInvocation(lastNodePoped, getterName);
			argumentExpr = JExpr.invoke(argumentExpr, getterName);
		}
		argumentExpr = JExpr.invoke(argumentExpr, "getId");
		if (requiresDataArgument(invokeFunction)) {
			invokeStatement.arg(argumentExpr).arg(actionData).arg(elementNumber);
		} else {
			invokeStatement.arg(argumentExpr).arg(elementNumber);
		}

		logger.info("Invoked " + activeNode);
		if (activeNode.getWait_time() != null) {
			generateWait(sdata, activeNode.getWait_time());
		}

		sdata.getBlock().invoke(outVar, "println")
				.arg("=============" + invokeFunction + " " + activeNode.getName() + "=============");

	}

	private void generateWait(ScaffolingData sdata, String wait) {

		if (wait != null) {
			if (wait.toLowerCase().equals("short")) {
				sdata.getBlock().invoke(outVar, "println")
						.arg("=============" + "Waiting for a short interval" + "=============");
				sdata.getBlock().invoke("waitForAShortWhile");
			} else if (wait.toLowerCase().equals("normal")) {
				sdata.getBlock().invoke(outVar, "println")
						.arg("=============" + "Waiting for a while " + "=============");
				sdata.getBlock().invoke("waitForAWhile");
			} else if (wait.toLowerCase().equals("long")) {
				sdata.getBlock().invoke(outVar, "println")
						.arg("=============" + "Waiting for a long interval" + "=============");
				sdata.getBlock().invoke("waitForALongWhile");
			}
		}
	}

	private Map<String, GraphNode> getUsedPages(GraphPath<GraphNode, DefaultEdge> path, Spec spec) {
		Map<String, GraphNode> usedPages = new HashMap<>();

		if (path != null) {
			for (DefaultEdge e : path.getEdgeList()) {
				GraphNode srcNode = graph.getEdgeSource(e);
				GraphNode dstNode = graph.getEdgeTarget(e);

				if (srcNode.getNodeType() == NodeType.PAGE) {
					usedPages.put(dstNode.getName(), srcNode);
				}
			}
		}
		String lastScreen = spec.getThen().getScreen();
		String lastPage = graphGenerator.getScreenToPage().get(lastScreen);

		logger.info("last page node is " + lastPage);
		usedPages.put(graphGenerator.getNodeByKey(lastScreen).getName(), graphGenerator.getNodeByKey(lastPage));

		for (String screen : usedPages.keySet()) {
			logger.info("used screen " + screen + " used page " + usedPages.get(screen));
		}

		return usedPages;

	}

	private void generateFieldVariables(JDefinedClass definedClass) {

		for (String key : usedPages.keySet()) {
			GraphNode pageNode = usedPages.get(key);

			if (!definedPages.containsKey(pageNode)) {
				JDefinedClass pageClass = classGenerator.getNodeClassMap().get(pageNode);

				JFieldVar pageField = definedClass.field(JMod.FINAL, pageClass, "field" + pageNode.getName(),
						JExpr._new(pageClass));
				definedPages.put(pageNode, pageField);

				fieldVarToNodeMap.put(pageField, pageNode);

				logger.info("adding field variable " + pageField.name());
			}
		}
	}

	private ScaffolingData generatePrecondition(Spec spec, JCodeModel codeModel, JDefinedClass definedClass,
			GraphPath<GraphNode, DefaultEdge> path, String methodName) {

		ScaffolingData sdata = createMethodScaffolding(codeModel, definedClass, methodName, true);

		// go to homepage
		sdata.getBlock().invoke("goToHomePage");

		GraphNode lastNode = null;
		for (DefaultEdge e : path.getEdgeList()) {
			GraphNode srcNode = graph.getEdgeSource(e);
			lastNode = graph.getEdgeTarget(e);

			setActive(srcNode);

			if (srcNode.getNodeType() == NodeType.PAGE) {

				assert_element(sdata, srcNode.getImplictAssertions().get(0));

			} else if (srcNode.getNodeType() == NodeType.SCREEN) {

				assert_element(sdata, srcNode, null, null, defaultElementNumber);

			} else if (srcNode.getNodeType() == NodeType.APP) {

				assert_element(sdata, srcNode, null, null, defaultElementNumber);

			} else {

				invoke_element(sdata, srcNode, srcNode.getAction_data(), defaultElementNumber,
						srcNode.getAction_type());
			}
		}

		setActive(lastNode);

		assert_element(sdata, lastNode, null, null, defaultElementNumber);

		// ExplicitAssertion eassert = spec.getGiven().getParsedAssertion();
		// for (Clause clause : eassert.getclauses()) {
		// for(Literal literal : clause.getLiterals()) {
		// assert_element(sdata, literal.getNode(), literal.getAction(),
		// literal.getTextData(), defaultElementNumber);
		// }
		// }

		// add closing asserts
		addClosingAssert(sdata);

		return sdata;
	}

	private void call(ScaffolingData sdata, ScaffolingData givenSdata) {

		sdata.getBlock().invoke(givenSdata.getMethod());
	}

	private ScaffolingData generateTestCode(JCodeModel codeModel, JDefinedClass definedClass, ScaffolingData sdata,
			Spec spec) {

		// generate WHEN
		String methodName = "when";
		if (specChainCounter > 0) {
			methodName += "_" + specChainCounter;
		}
		ScaffolingData whenSdata = generateSpecActions(codeModel, definedClass, spec.getWhen(), methodName);

		call(sdata, whenSdata);
		logger.info("=========== action generated ===========");

		// generate WAIT
		generateWait(sdata, spec.getWait());
		logger.info("=========== wait generated ===========");

		// generate THEN
		methodName = "then";
		if (specChainCounter > 0) {
			methodName += "_" + specChainCounter;
		}
		ScaffolingData thenSdata = generatePostCondition(spec, codeModel, definedClass, spec.getThen(), methodName);

		call(sdata, thenSdata);
		logger.info("=========== post condtion generated ===========");

		return sdata;
	}

	private void generatePostSpec(JCodeModel codeModelOrg, JDefinedClass definedClassOrg, ScaffolingData sdataOrg,
			String post, State thenState)
			throws InvalidKeySpecException, IllegalAccessException, InvocationTargetException,
			JClassAlreadyExistsException, CloneNotSupportedException, ClassNotFoundException, IOException {

		Spec postSpec = specMap.get(post);

		if (postSpec == null) {
			throw new InvalidKeySpecException("Spec " + post + " not found");
		}

		// postSpec given should be equal to spec thenState
		if (!postSpec.getGiven().getScreen().equals(thenState.getScreen())) {
			throw new InvalidPostSpec("Post spec " + postSpec.getName() + " must have a start screen "
					+ thenState.getScreen() + " but found " + postSpec.getGiven().getScreen());
		}
		logger.info("Chaining with " + postSpec.getName());

		// used for method naming - should use something better
		specChainCounter += 1;

		// get any additional pages used in the postSpec
		Map<String, GraphNode> pagesUsedPost = getUsedPages(null, postSpec);
		usedPages.putAll(pagesUsedPost);

		// get a new name for the chained test class
		String newFullname = definedClassOrg.fullName() + GeneratorUtilities.firstLetterCaptial(postSpec.getName());
		logger.info("Creating class " + newFullname + " by duplication");

		// clone objects
		JCodeModel codeModel = cloneCodeModel(codeModelOrg);
		JDefinedClass definedClass = cloneDefinedClass(definedClassOrg);
		definedClass.setName(definedClassOrg.name() + GeneratorUtilities.firstLetterCaptial(postSpec.getName()));

		// show the class, as the parent would have been hidden
		definedClass.show();

		// add new class to package
		codeModel.addClass(newFullname, definedClass);

		// get a reference to the last method
		JMethod returnedMethod = definedClass.getMethod(sdataOrg.getMethod().name(), sdataOrg.getMethod().typeParams());
		if (returnedMethod == null) {
			throw new InvalidStackStateException("Method not found");
		}

		// create a new method that will call post specification
		ScaffolingData sdata = createMethodScaffolding(codeModel, definedClass,
				"post" + GeneratorUtilities.firstLetterCaptial(postSpec.getName()), false);

		sdata.getBlock().invoke(outVar, "println")
				.arg("=============" + "Post specifcation " + postSpec.getName() + " =============");

		// invoke the post-specification method in the last method
		returnedMethod.body().invoke(sdata.getMethod());
		generateFieldVariables(definedClass);

		sdata = generateTestCode(codeModel, definedClass, sdata, postSpec);

		// in case the post specification has post specifications of its own
		if (postSpec.getPost() != null && postSpec.getPost().size() > 0) {

			// hide this class as its children would be written
			definedClass.hide();

			for (String postPost : postSpec.getPost()) {

				// save context
				Stack<GraphNode> originalStack = (Stack<GraphNode>) stack.clone();
				Map<GraphNode, JFieldVar> originalDefinedPages = new HashMap();
				originalDefinedPages.putAll(definedPages);
				JFieldVar originalActivePageVariable = activePageVariable;
				Map<String, GraphNode> originalUsedPages = new HashMap();
				originalUsedPages.putAll(usedPages);
				logger.info("Saved context");

				generatePostSpec(codeModel, definedClass, sdata, postPost, postSpec.getThen());

				// restore context
				stack = originalStack;
				usedPages = originalUsedPages;
				definedPages = originalDefinedPages;
				activePageVariable = originalActivePageVariable;
				logger.info("Restored context");
			}
		} else {
			// write class to file
			writeTestToFile(codeModel, definedClass);
		}
	}

	private JDefinedClass cloneDefinedClass(JDefinedClass jDefinedClass) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(jDefinedClass);
		oos.flush();
		oos.close();
		bos.close();
		byte[] byteData = bos.toByteArray();

		ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
		JDefinedClass copy = (JDefinedClass) new ObjectInputStream(bais).readObject();
		return copy;
	}

	private JCodeModel cloneCodeModel(JCodeModel jCodeModel) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(jCodeModel);
		oos.flush();
		oos.close();
		bos.close();
		byte[] byteData = bos.toByteArray();

		ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
		JCodeModel copy = (JCodeModel) new ObjectInputStream(bais).readObject();
		return copy;
	}

	private void generateClasses(Spec spec, GraphPath<GraphNode, DefaultEdge> path, String testName)
			throws IOException, JClassAlreadyExistsException, InvalidKeySpecException, IllegalAccessException,
			InvocationTargetException, CloneNotSupportedException, ClassNotFoundException {
		if (path == null) {
			throw new InvalidPathException();
		}

		definedPages = new HashMap<>();
		fieldVarToNodeMap = new HashMap<>();
		stack = new Stack<GraphNode>();
		activePageVariable = null;
		specChainCounter = 0;

		logger.info("===| Generating class for " + GeneratorUtilities.firstLetterCaptial(testName));
		JCodeModel codeModel = new JCodeModel();
		String packageName = "io.typeset.sphinx.tests";
		String className = packageName + "." + GeneratorUtilities.firstLetterCaptial(testName);
		JDefinedClass definedClass = codeModel._class(className);
		definedClass._extends(classGenerator.getActionClass());

		outVar = codeModel.ref(System.class).staticRef("out");
		usedPages = getUsedPages(path, spec);

		// generate field variables
		generateFieldVariables(definedClass);

		// generate method scaffolding
		ScaffolingData sdata = createMethodScaffolding(codeModel, definedClass, "execute", false);

		// add testng annotation
		JMethod method = sdata.getMethod();
		method.annotate(org.testng.annotations.Test.class);

		// generate GIVEN
		ScaffolingData givenSdata = generatePrecondition(spec, codeModel, definedClass, path, "given");

		call(sdata, givenSdata);
		logger.info("=========== pre condtion generated ===========");

		sdata = generateTestCode(codeModel, definedClass, sdata, spec);

		if (spec.getPost() == null || spec.getPost().size() == 0) {
			writeTestToFile(codeModel, definedClass);
		} else {
			// hide this class as its children would be written
			definedClass.hide();
			for (String post : spec.getPost()) {

				logger.info("Generating chained tests for " + post);

				// save context
				Stack<GraphNode> originalStack = (Stack<GraphNode>) stack.clone();
				Map<GraphNode, JFieldVar> originalDefinedPages = new HashMap();
				originalDefinedPages.putAll(definedPages);
				JFieldVar originalActivePageVariable = activePageVariable;
				Map<String, GraphNode> originalUsedPages = new HashMap();
				originalUsedPages.putAll(usedPages);

				generatePostSpec(codeModel, definedClass, sdata, post, spec.getThen());

				// restore context
				stack = originalStack;
				usedPages = originalUsedPages;
				definedPages = originalDefinedPages;
				activePageVariable = originalActivePageVariable;
			}
		}
	}

	public Map<String, String> generateTest(List<Spec> specList)
			throws IOException, JClassAlreadyExistsException, InvalidKeySpecException, IllegalAccessException,
			InvocationTargetException, CloneNotSupportedException, ClassNotFoundException {
		if (specList.isEmpty()) {
			logger.info("Spec list is empty, not generating tests");
		}

		for (Spec spec : specList) {
			GraphPath<GraphNode, DefaultEdge> path = getFeasiblePath(spec);
			logger.info("Resolving spec " + spec);
			if (path != null) {
				logger.info("Feasible path found " + path);
				generateClasses(spec, path, spec.getName());

			} else {
				logger.info("No feasible path found");
			}
		}
		return generatedTests;
	}

}
