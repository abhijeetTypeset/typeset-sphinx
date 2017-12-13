package typeset.io.generators;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.Multigraph;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JArray;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;

import typeset.io.exceptions.InconsistentGraphException;
import typeset.io.generators.util.GeneratorUtilities;
import typeset.io.model.App;
import typeset.io.model.GraphNode;
import typeset.io.model.Model;
import typeset.io.model.NodeType;
import typeset.io.model.Page;
import typeset.io.model.Screen;
import typeset.io.model.Widget;

import org.openqa.selenium.By;

public class ModelGenerator {
	private DefaultDirectedGraph<GraphNode, DefaultEdge> tgraph;
	private static Map<GraphNode, JDefinedClass> nodeClassMap = new HashMap<>();
	private String outputDir;
	private JDefinedClass definedAbstractNode;
	private JDefinedClass actionClass;

	public ModelGenerator(DefaultDirectedGraph<GraphNode, DefaultEdge> tgraph, String outputDir) {
		this.tgraph = tgraph;
		this.outputDir = outputDir;

	}

	public void generateClasses() throws IOException, JClassAlreadyExistsException {

		if (tgraph == null) {
			throw new InconsistentGraphException("Cannot proceed with a null graph");
		}

		copyBaseClasses();

		generateAbstractClasses();

		// get all the nodes
		System.out.println("Generating classes : ");

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

		generateTestExecutorClasses();

	}

	public JDefinedClass getActionClass() {
		return actionClass;
	}

	private void generateTestExecutorClasses() throws JClassAlreadyExistsException, IOException {
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

	private void copyBaseClasses() throws IOException {

		String baseDirStructure = "res" + File.separator + "baseDirStructure";

		FileUtils.copyDirectory(new File(baseDirStructure), new File(outputDir));
		System.out.println("Copying base directory structure from " + baseDirStructure + " to " + outputDir);
	}

	private void generateClassFile(GraphNode gnode) throws JClassAlreadyExistsException, IOException {
		System.out.println("===| Generated class for " + gnode);
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
			JExpression init = cm.ref(org.openqa.selenium.By.class).staticInvoke(gnode.getId().get("by"))
					.arg(JExpr.lit(gnode.getId().get("locator")));
			field.init(init);
			JMethod getterMethod = definedClass.method(JMod.PUBLIC, org.openqa.selenium.By.class, "getId");
			JBlock block = getterMethod.body();
			block._return(field);
		}
		// add action
		if (gnode.getAction_type() != null) {
			JFieldVar propertyField = definedClass.field(JMod.PRIVATE, String.class, "actionType");
			JExpression init = JExpr.lit(gnode.getAction_type());
			propertyField.init(init);
			JMethod getterMethod = definedClass.method(JMod.PUBLIC, cm.ref(String.class).array(), "getActionType");
			JBlock block = getterMethod.body();
			block._return(propertyField);
		}

		// add default data
		if (gnode.getAction_data() != null) {
			JFieldVar propertyField = definedClass.field(JMod.PRIVATE, String.class, "actionData");
			JExpression init = JExpr.lit(gnode.getAction_type());
			propertyField.init(init);
			JMethod getterMethod = definedClass.method(JMod.PUBLIC, cm.ref(String.class).array(), "getActionData");
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

				System.out.println("target node " + targetNode);
				JDefinedClass exitingClassNode = nodeClassMap.get(targetNode);
				JFieldVar field = definedClass.field(JMod.PRIVATE, exitingClassNode, "var" + targetNode.getName());
				JExpression init = JExpr._new(exitingClassNode);
				field.init(init);
				JMethod getterMethod = definedClass.method(JMod.PUBLIC, exitingClassNode,
						GeneratorUtilities.getGetterName(targetNode.getName()));
				JBlock block = getterMethod.body();
				block._return(field);

			}
		}

		// add it to pool of defined classes
		nodeClassMap.put(gnode, definedClass);

		String filepath = outputDir + File.separator + "FlyPaper" + File.separator + "src" + File.separator + "main"
				+ File.separator + "java";
		// System.out.println("writing file to "+filepath);
		File file = new File(filepath);
		file.mkdirs();
		cm.build(file);
	}

	public static Map<GraphNode, JDefinedClass> getNodeClassMap() {
		return nodeClassMap;
	}

	public static void setNodeClassMap(Map<GraphNode, JDefinedClass> nodeClassMap) {
		ModelGenerator.nodeClassMap = nodeClassMap;
	}

}
