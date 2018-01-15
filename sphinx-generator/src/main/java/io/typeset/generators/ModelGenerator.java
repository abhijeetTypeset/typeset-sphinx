package io.typeset.generators;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.typeset.generators.util.GeneratorUtilities;
import io.typeset.model.GraphNode;
import io.typeset.model.NodeType;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

import io.typeset.exceptions.InconsistentGraphException;
import io.typeset.readers.ConfigReader;

/**
 * The Class ModelGenerator. Generate Java Classes representing product model
 * from the graph model
 */
public class ModelGenerator {
	private static final Logger logger = LogManager.getLogger("ModelGenerator");

	/** The tgraph. */
	private DefaultDirectedGraph<GraphNode, DefaultEdge> tgraph;

	/** The node class map. */
	private Map<GraphNode, JDefinedClass> nodeClassMap = new HashMap<>();

	/** The output dir. */
	private String outputDir;

	/** The defined abstract node. */
	private JDefinedClass definedAbstractNode;

	/** The action class. */
	private JDefinedClass actionClass;

	private Map<GraphNode, List<String>> implementedGetters = new HashMap<>();

	/**
	 * Instantiates a new model generator.
	 *
	 * @param tgraph
	 *            the tgraph
	 * @param outputDir
	 *            the output dir
	 */
	public ModelGenerator(DefaultDirectedGraph<GraphNode, DefaultEdge> tgraph) {
		this.tgraph = tgraph;
		this.outputDir = ConfigReader.outputDir;

	}

	/**
	 * Generate classes representing elements of the W.A.S.P. model.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JClassAlreadyExistsException
	 *             the j class already exists exception
	 */
	public void generateClasses() throws IOException, JClassAlreadyExistsException {

		if (tgraph == null) {
			logger.error("Cannot proceed with a null graph");
			throw new InconsistentGraphException("Cannot proceed with a null graph");
		}

		copyDirectoryStructure();

		generateAbstractClasses();

		// get all the nodes
		logger.info("Generating classes : ");

		// get all the nodes
		Set<GraphNode> allNodes = tgraph.vertexSet();

		// this is not the correct way to do , we need to build a
		// dependency graph and then generate the class using the
		// dependency graph. However, currently we shall improvise

		for (GraphNode gnode : allNodes) {
			if (gnode.getNodeType() == NodeType.CONTROL) {
				generateClassFile(gnode);
			}
		}
		for (GraphNode gnode : allNodes) {
			if (gnode.getNodeType() == NodeType.WIDGET) {
				generateClassFile(gnode);
			}
		}
		for (GraphNode gnode : allNodes) {
			if (gnode.getNodeType() == NodeType.APP) {
				generateClassFile(gnode);
			}
		}
		for (GraphNode gnode : allNodes) {
			if (gnode.getNodeType() == NodeType.SCREEN) {
				generateClassFile(gnode);
			}
		}
		for (GraphNode gnode : allNodes) {
			if (gnode.getNodeType() == NodeType.PAGE) {
				generateClassFile(gnode);
			}
		}

		generateAuxiliaryClasses();

	}

	/**
	 * Gets the action class.
	 *
	 * @return the action class
	 */
	public JDefinedClass getActionClass() {
		return actionClass;
	}

	/**
	 * Generate a bunch of auxiliary classes useful for test execution
	 *
	 * @throws JClassAlreadyExistsException
	 *             the j class already exists exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void generateAuxiliaryClasses() throws JClassAlreadyExistsException, IOException {
		JCodeModel cm = new JCodeModel();
		String packageName = "utils";
		String className = packageName + "." + "ConfigClass";

		JDefinedClass configClass = cm._class(className);
		String filepath = outputDir + File.separator + "FlyPaper" + File.separator + "src" + File.separator + "main"
				+ File.separator + "java";
		File file = new File(filepath);
		file.mkdirs();
		cm.build(file);

		packageName = "controller";
		className = packageName + "." + "ActionClass";
		actionClass = cm._class(className);
		actionClass._extends(configClass);
		filepath = outputDir + File.separator + "FlyPaper" + File.separator + "src" + File.separator + "main"
				+ File.separator + "java";
		file = new File(filepath);
		file.mkdirs();
		cm.build(file);

	}

	/**
	 * Generate abstract classes representing W.A.S.P. elements
	 *
	 * @throws JClassAlreadyExistsException
	 *             the j class already exists exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void generateAbstractClasses() throws JClassAlreadyExistsException, IOException {
		JCodeModel cm = new JCodeModel();
		String packageName = "model";
		String className = packageName + "." + "Node";

		ClassType t = ClassType.CLASS;
		definedAbstractNode = cm._class(JMod.PUBLIC | JMod.ABSTRACT, className, t);

		definedAbstractNode.method(JMod.PUBLIC | JMod.ABSTRACT, org.openqa.selenium.By.class, "getId");
		definedAbstractNode.method(JMod.PUBLIC | JMod.ABSTRACT, String.class, "getName");

		String filepath = outputDir + File.separator + "FlyPaper" + File.separator + "src" + File.separator + "main"
				+ File.separator + "java";
		File file = new File(filepath);
		file.mkdirs();
		cm.build(file);
	}

	/**
	 * Copy directory structure and base classes.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void copyDirectoryStructure() throws IOException {

		String baseDirStructure = "res" + File.separator + "baseDirStructure";

		FileUtils.copyDirectory(new File(baseDirStructure), new File(outputDir));
		logger.info("Copying base directory structure from " + baseDirStructure + " to " + outputDir);
	}

	/**
	 * Generate class file for a given graph node
	 *
	 * @param gnode
	 *            the gnode
	 * @throws JClassAlreadyExistsException
	 *             the j class already exists exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void generateClassFile(GraphNode gnode) throws JClassAlreadyExistsException, IOException {
		logger.info("===| Generated class for " + gnode);
		JCodeModel cm = new JCodeModel();
		String packageName = "model" + "." + gnode.getNodeType() + "s";
		String className = packageName + "." + GeneratorUtilities.firstLetterCaptial(gnode.getName());

		JDefinedClass definedClass = cm._class(className);
		definedClass._extends(definedAbstractNode);

		// add a name
		if (gnode.getName() != null) {
			JFieldVar propertyField = definedClass.field(JMod.PRIVATE, String.class, "name");
			JExpression init = JExpr.lit(gnode.getName());
			propertyField.init(init);
			JMethod getterMethod = definedClass.method(JMod.PUBLIC, String.class, "getName");
			JBlock block = getterMethod.body();
			block._return(propertyField);
		}
		// add an url
		if (gnode.getUrl() != null) {
			JFieldVar propertyField = definedClass.field(JMod.PRIVATE, String.class, "url");
			JExpression init = JExpr.lit(gnode.getUrl());
			propertyField.init(init);
			JMethod getterMethod = definedClass.method(JMod.PUBLIC, String.class, "getUrl");
			JBlock block = getterMethod.body();
			block._return(propertyField);
		}
		// add an id
		if (gnode.getId() != null) {
			JFieldVar field = definedClass.field(JMod.PRIVATE, org.openqa.selenium.By.class, "id");

			if (gnode.getId().get("by") != null && gnode.getId().get("locator") != null) {
				JExpression init = cm.ref(org.openqa.selenium.By.class).staticInvoke(gnode.getId().get("by"))
						.arg(JExpr.lit(gnode.getId().get("locator")));
				field.init(init);
			} else {
				field.init(JExpr._null());
			}

			JMethod getterMethod = definedClass.method(JMod.PUBLIC, org.openqa.selenium.By.class, "getId");
			JBlock block = getterMethod.body();
			block._return(field);
		}
		// add action
		if (gnode.getAction_type() != null) {
			JFieldVar propertyField = definedClass.field(JMod.PRIVATE, String.class, "actionType");
			JExpression init = JExpr.lit(gnode.getAction_type());
			propertyField.init(init);
			JMethod getterMethod = definedClass.method(JMod.PUBLIC, String.class, "getActionType");
			JBlock block = getterMethod.body();
			block._return(propertyField);
		}

		// add default data
		if (gnode.getAction_data() != null) {
			JFieldVar propertyField = definedClass.field(JMod.PRIVATE, String.class, "actionData");
			JExpression init = JExpr.lit(gnode.getAction_type());
			propertyField.init(init);
			JMethod getterMethod = definedClass.method(JMod.PUBLIC, String.class, "getActionData");
			JBlock block = getterMethod.body();
			block._return(propertyField);
		}

		if (gnode.getNodeType() != NodeType.CONTROL) {
			Set<DefaultEdge> edges = tgraph.edgesOf(gnode);
			for (DefaultEdge edge : edges) {
				GraphNode targetNode = tgraph.getEdgeTarget(edge);

				if (targetNode == gnode) {
					// self loop
					continue;
				}

				logger.info("target node " + targetNode);
				JDefinedClass exitingClassNode = nodeClassMap.get(targetNode);
				JFieldVar field = definedClass.field(JMod.PRIVATE, exitingClassNode, "var" + targetNode.getName());
				JExpression init = JExpr._new(exitingClassNode);
				field.init(init);
				
				String getterName = GeneratorUtilities.getGetterName(targetNode.getName());
				
				JMethod getterMethod = definedClass.method(JMod.PUBLIC, exitingClassNode, getterName);
				JBlock block = getterMethod.body();
				block._return(field);

				addGetter(gnode, getterName);
			}

			// TODO: add missing variables
			for (GraphNode noEdgeNode : gnode.getNoEdges()) {
				logger.info("to add edges : " + noEdgeNode.getName());

				String getterName = GeneratorUtilities.getGetterName(noEdgeNode.getName());

				JDefinedClass exitingClassNode = nodeClassMap.get(noEdgeNode);
				JFieldVar field = definedClass.field(JMod.PRIVATE, exitingClassNode, "var" + noEdgeNode.getName());
				JExpression init = JExpr._new(exitingClassNode);
				field.init(init);
				JMethod getterMethod = definedClass.method(JMod.PUBLIC, exitingClassNode, getterName);
				JBlock block = getterMethod.body();
				block._return(field);

				addGetter(gnode, getterName);
			}
		}

		// add it to pool of defined classes
		nodeClassMap.put(gnode, definedClass);

		String filepath = outputDir + File.separator + "FlyPaper" + File.separator + "src" + File.separator + "main"
				+ File.separator + "java";
		// logger.info("writing file to "+filepath);
		File file = new File(filepath);
		file.mkdirs();
		cm.build(file);
	}

	private void addGetter(GraphNode gnode, String getterName) {
		if (implementedGetters.containsKey(gnode)) {
			List<String> temp = implementedGetters.get(gnode);
			temp.add(getterName);
			implementedGetters.put(gnode, temp);
		}else {
			List<String> temp = new ArrayList<>();
			temp.add(getterName);
			implementedGetters.put(gnode, temp);
		}
	}
	
	public boolean containsGetter(GraphNode gnode, String getterName) {
		List<String> temp = implementedGetters.get(gnode);
		if(temp.contains(getterName)){
			return true;
		}
		return false;
	}

	/**
	 * Gets the node class map.
	 *
	 * @return the node class map
	 */
	public Map<GraphNode, JDefinedClass> getNodeClassMap() {
		return nodeClassMap;
	}

	/**
	 * Sets the node class map.
	 *
	 * @param nodeClassMap
	 *            the node class map
	 */
	public void setNodeClassMap(Map<GraphNode, JDefinedClass> nodeClassMap) {
		this.nodeClassMap = nodeClassMap;
	}

}
