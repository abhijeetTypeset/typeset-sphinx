package typeset.io.generators;


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
import java.util.List;
import java.util.Map;

import java.util.Stack;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.openqa.selenium.InvalidElementStateException;

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

import typeset.io.exceptions.InvalidLiteralException;
import typeset.io.exceptions.InvalidNodeException;
import typeset.io.exceptions.InvalidPathException;
import typeset.io.exceptions.InvalidPostSpec;
import typeset.io.exceptions.InvalidStackStateException;
import typeset.io.exceptions.TooComplexExpression;
import typeset.io.generators.ds.ScaffolingData;
import typeset.io.generators.util.GeneratorUtilities;
import typeset.io.model.GraphNode;
import typeset.io.model.NodeType;
import typeset.io.model.assertions.Clause;
import typeset.io.model.assertions.ExplicitAssertion;
import typeset.io.model.assertions.Literal;
import typeset.io.model.spec.*;
import typeset.io.readers.ConfigReader;
import typeset.io.readers.SpecReader;

public class TestGenerator {
	private String outputDir;
	private GraphGenerator graphGenerator;
	private DefaultDirectedGraph<GraphNode, DefaultEdge> graph;
	private String inputDir;


	private JFieldRef outVar;
	private ModelGenerator classGenerator;

	// program state
	private Map<GraphNode, JFieldVar> definedPages = null;
	private Stack<GraphNode> stack = null;
	private JFieldVar activePageVariable = null;
	private Map<String, GraphNode> usedPages = null;

	private AllDirectedPaths<GraphNode, DefaultEdge> allDirectedPath;
	private static final Logger logger = LogManager.getLogger("TestGenerator");
	private Map<String, Spec> specMap = new HashMap<String, Spec>();
	private int specChainCounter = 0;

	// TODO: get this some other way
	private int MAX_LENGTH = 25;

	public TestGenerator(DefaultDirectedGraph<GraphNode, DefaultEdge> graph, GraphGenerator graphGenerator,
			ModelGenerator classGenerator) {
		this.graph = graph;
		this.graphGenerator = graphGenerator;
		this.inputDir = ConfigReader.inputDir;
		this.outputDir = ConfigReader.outputDir;
		this.classGenerator = classGenerator;
		this.allDirectedPath = new AllDirectedPaths<>(graph);
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
					if(isTopLevelTest(sf)) {
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

	private boolean isTopLevelTest(String sf) {
		for(String test : ConfigReader.tests) {
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

	private void writeTestToFile(JCodeModel cModel) throws IOException {
		String filepath = outputDir + File.separator + "FlyPaper" + File.separator + "src" + File.separator + "test"
				+ File.separator + "java";
		logger.info("Generating class file " + filepath);

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

	private ScaffolingData generateSpecActions(JCodeModel codeModel, JDefinedClass definedClass,
			Map<String, Action> actions, String methodName) {
		ScaffolingData sdata = createMethodScaffolding(codeModel, definedClass, methodName, true);

		if (actions != null && !actions.isEmpty()) {
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
					logger.info("Execute " + action_tag + " " + action + " with " + actionData);
				} else {

					invoke_element(sdata, actionNode, actionNode.getAction_data());
					logger.info("Execute " + action_tag + " " + action);
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

	private ScaffolingData generatePostCondition(JCodeModel codeModel, JDefinedClass definedClass, State then,
			String methodName) {
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

		assert_element(sdata, screenNode, null, null);

		List<String> explicitAssertions = then.getAssertions();
		if (explicitAssertions != null) {
			ExplicitAssertion parsedEXplicit = graphGenerator.parsePrecondition(explicitAssertions);
			generatePostExplicitAssertions(sdata, parsedEXplicit);
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

	private void generatePostExplicitAssertions(ScaffolingData sdata, ExplicitAssertion parsedEXplicit) {

		Literal literal = parsedEXplicit.getclauses().get(0).getLiterals().get(0);
		logger.info("explicit assertion :  " + literal);

		assert_element(sdata, literal.getNode(), literal.getAction(), literal.getTextData());

	}

	private void assert_element(ScaffolingData sdata, String functionName) {
		JInvocation assertStatement = sdata.getBlock().invoke(sdata.getAssertVar(), "assertTrue");
		JExpression getUrlExpr = JExpr.invoke(activePageVariable, "getUrl");
		JExpression atPageExpr = JExpr.invoke(functionName).arg(getUrlExpr);
		assertStatement.arg(atPageExpr);
	}

	private void assert_element(ScaffolingData sdata, GraphNode activeNode, String specAssertFunction,
			String specAssertData) {
		JInvocation assertStatement = sdata.getBlock().invoke(sdata.getAssertVar(), "assertTrue");

		logger.info("asserting for element " + activeNode);
		JExpression argumentExpr = null;
		boolean flag = true;

		for (GraphNode stackNode : stack) {
			String getterName = GeneratorUtilities.getGetterName(stackNode.getName());
			if (flag) {

				argumentExpr = JExpr.invoke(activePageVariable, getterName);
				flag = false;
			} else {
				argumentExpr = JExpr.invoke(argumentExpr, getterName);
			}
			logger.info("Obtained from the stack " + stackNode + " getter name " + getterName);

		}
		String getterName = GeneratorUtilities.getGetterName(activeNode.getName());
		if (flag) {
			argumentExpr = JExpr.invoke(activePageVariable, getterName);
		}

		if (activeNode.getNodeType() == NodeType.CONTROL) {
			argumentExpr = JExpr.invoke(argumentExpr, getterName);
		}

		argumentExpr = JExpr.invoke(argumentExpr, "getId");

		if (specAssertFunction == null) {
			specAssertFunction = activeNode.getImplictAssertions().get(0);
		}

		JExpression assertExpr = null;
		if (requiresDataArgument(specAssertFunction)) {
			assertExpr = JExpr.invoke(specAssertFunction).arg(argumentExpr).arg(specAssertData);
		} else {
			assertExpr = JExpr.invoke(specAssertFunction).arg(argumentExpr);
		}
		assertStatement.arg(assertExpr);
	}

	private boolean requiresDataArgument(String specAssertFunction) {

		String[] requiresDataArg = { "contains" };

		for (String func : requiresDataArg) {
			if (func.toLowerCase().equals(specAssertFunction.toLowerCase())) {
				return true;
			}
		}

		return false;
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

		logger.info("Invoked " + activeNode);
		if (activeNode.getWait_time() != null) {
			generateWait(sdata, activeNode.getWait_time());
		}

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
			sdata.getBlock().invoke("waitTime").arg(expr);
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
				logger.info("adding field variable " + pageField);
			}
		}
	}

	private ScaffolingData generatePrecondition(JCodeModel codeModel, JDefinedClass definedClass,
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

				assert_element(sdata, srcNode, null, null);

			} else if (srcNode.getNodeType() == NodeType.APP) {

				assert_element(sdata, srcNode, null, null);

			} else if (srcNode.getNodeType() == NodeType.WIDGET) {

				assert_element(sdata, srcNode, null, null);

			} else {

				invoke_element(sdata, srcNode, srcNode.getAction_data());
			}
		}

		setActive(lastNode);

		assert_element(sdata, lastNode, null, null);

		// add closing asserts
		addClosingAssert(sdata);

		return sdata;
	}

	private void call(ScaffolingData sdata, ScaffolingData givenSdata) {

		sdata.getBlock().invoke(givenSdata.getMethod());
	}

	private void updateAuxiliaryClasses() throws IOException {
		String dirpath = outputDir + File.separator + "FlyPaper" + File.separator + "src" + File.separator + "main"
				+ File.separator + "java";

		FileUtils.copyFile(new File("res" + File.separator + "auxiliaryClasses" + File.separator + "ConfigClass.java"),
				new File(dirpath + File.separator + "utils" + File.separator + "ConfigClass.java"));
		FileUtils.copyFile(new File("res" + File.separator + "auxiliaryClasses" + File.separator + "ActionClass.java"),
				new File(dirpath + File.separator + "controller" + File.separator + "ActionClass.java"));

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
		ScaffolingData thenSdata = generatePostCondition(codeModel, definedClass, spec.getThen(), methodName);

		call(sdata, thenSdata);
		logger.info("=========== post condtion generated ===========");

		return sdata;
	}

	private void generatePostSpec(JCodeModel codeModelOrg, JDefinedClass definedClassOrg, ScaffolingData sdataOrg,
			String post, State thenState) throws InvalidKeySpecException, IllegalAccessException,
			InvocationTargetException, JClassAlreadyExistsException, CloneNotSupportedException, ClassNotFoundException, IOException {
		Spec postSpec = specMap.get(post);

		if (postSpec == null) {
			throw new InvalidKeySpecException("Spec " + post + " not found");
		}

		// postSpec given should be equal to spec thenState
		if (!postSpec.getGiven().getScreen().equals(thenState.getScreen())) {
			throw new InvalidPostSpec("Post spec " + postSpec.getName() + " must have a start screen "
					+ thenState.getScreen() + " but found " + postSpec.getGiven().getScreen());
		}

		// used for method naming
		specChainCounter += 1;

		// get any additional pages used in the postSpec
		Map<String, GraphNode> pagesUsedPost = getUsedPages(null, postSpec);
		usedPages.putAll(pagesUsedPost);

		String newFullname = definedClassOrg.fullName() + GeneratorUtilities.firstLetterCaptial(postSpec.getName());
		System.out.println("Must change name to " + newFullname);

		
		// clone objects
		JCodeModel codeModel = cloneCodeModel(codeModelOrg);
		JDefinedClass definedClass = cloneDefinedClass(definedClassOrg);
		definedClass.setName(definedClassOrg.name() + GeneratorUtilities.firstLetterCaptial(postSpec.getName()));
		codeModel.addClass(newFullname, definedClass);
		
		JMethod returnedMethod = definedClass.getMethod(sdataOrg.getMethod().name(), sdataOrg.getMethod().typeParams());
		if (returnedMethod==null) {
			throw new InvalidStackStateException("Method not found");
		}
		
		ScaffolingData sdata = createMethodScaffolding(codeModel, definedClass, "post" + GeneratorUtilities.firstLetterCaptial(postSpec.getName()), false);

		sdata.getBlock().invoke(outVar, "println")
		.arg("=============" + "Post specifcation " + postSpec.getName() + " =============");
		
		returnedMethod.body().invoke(sdata.getMethod());
		generateFieldVariables(definedClass);

		sdata = generateTestCode(codeModel, definedClass, sdata, postSpec);

		if (postSpec.getPost() == null || postSpec.getPost().size() == 0) {
			
		} else {
			for (String postPost : postSpec.getPost()) {
				
				// save context
				Stack<GraphNode> originalStack = (Stack<GraphNode>) stack.clone();
				Map<GraphNode, JFieldVar> originalDefinedPages = new HashMap();
				originalDefinedPages.putAll(definedPages);
				JFieldVar originalActivePageVariable = activePageVariable;
				Map<String, GraphNode> originalUsedPages = new HashMap();
				originalUsedPages.putAll(usedPages);
				
				
				generatePostSpec(codeModel, definedClass, sdata, postPost, postSpec.getThen());
				
				// restore context
				stack = originalStack;
				usedPages = originalUsedPages;
				definedPages = originalDefinedPages;
				activePageVariable = originalActivePageVariable;
				
			}
		}
		
		writeTestToFile(codeModel);
		//return codeModel;
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
		JDefinedClass copy =  (JDefinedClass) new ObjectInputStream(bais).readObject();
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
		JCodeModel copy =  (JCodeModel) new ObjectInputStream(bais).readObject();
		return copy;
	}

	private void generateClasses(Spec spec, GraphPath<GraphNode, DefaultEdge> path, String testName)
			throws IOException, JClassAlreadyExistsException, InvalidKeySpecException, IllegalAccessException,
			InvocationTargetException, CloneNotSupportedException, ClassNotFoundException {
		if (path == null) {
			throw new InvalidPathException();
		}

		definedPages = new HashMap<>();
		stack = new Stack<GraphNode>();
		activePageVariable = null;
		specChainCounter = 0;

		logger.info("===| Generating class for " + GeneratorUtilities.firstLetterCaptial(testName));
		JCodeModel codeModel = new JCodeModel();
		String packageName = "tests";
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
		ScaffolingData givenSdata = generatePrecondition(codeModel, definedClass, path, "given");

		call(sdata, givenSdata);
		logger.info("=========== pre condtion generated ===========");

		sdata = generateTestCode(codeModel, definedClass, sdata, spec);

		if (spec.getPost() == null || spec.getPost().size() == 0) {
			writeTestToFile(codeModel);
		} else {
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
		updateAuxiliaryClasses();

	}

	public void generateTest(List<Spec> specList) throws IOException, JClassAlreadyExistsException, InvalidKeySpecException,
			IllegalAccessException, InvocationTargetException, CloneNotSupportedException, ClassNotFoundException {
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

	}

}
