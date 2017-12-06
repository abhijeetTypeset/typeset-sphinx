package typeset.io.generators;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.Multigraph;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

import typeset.io.models.App;
import typeset.io.models.GraphNode;
import typeset.io.models.Model;
import typeset.io.models.NodeType;
import typeset.io.models.Page;
import typeset.io.models.Screen;
import typeset.io.models.Widget;

import org.openqa.selenium.By;

public class Generator {
	private Multigraph<GraphNode, DefaultWeightedEdge> tgraph;
	private static Map<GraphNode, JDefinedClass> nodeClassMap = new HashMap<>();

	public Generator(Multigraph<GraphNode, DefaultWeightedEdge> tgraph) {
		this.tgraph = tgraph;
	}

	public void generateClasses(String outputDir) throws IOException, JClassAlreadyExistsException {

		if (tgraph == null) {
			System.out.println("Cannot proceed with a null graph");
			System.exit(0);
		}

		copyBaseClasses(outputDir);

		// get all the nodes
		System.out.println("Generating classes : ");

		// get all the nodes
		Set<GraphNode> allNodes = tgraph.vertexSet();

		// this is not the correct way to do , we need to build a
		// dependency graph and then generate the class using the
		// dependency graph. However, currently we shall improvise

		// first go through all the widgets
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

	}

	private void copyBaseClasses(String outputDir) throws IOException {
		// clean the output directory
		try {
			FileUtils.deleteDirectory(new File(outputDir));
		} catch (Exception e) {

		}
		String baseDirStructure = "res/baseDirStructure";

		FileUtils.copyDirectory(new File(baseDirStructure), new File(outputDir));
		System.out.println("Copying base directory structure from " + baseDirStructure + " to " + outputDir);
	}

	private void generateClassFile(GraphNode gnode) throws JClassAlreadyExistsException, IOException {
		System.out.println("===| Generated class for " + gnode);
		JCodeModel cm = new JCodeModel();
		String packageName = "model" + "." + gnode.getNodeType() + "s";
		String className = packageName + "." + gnode.getName();

		JDefinedClass definedClass = cm._class(className);
		// add a name
		if (gnode.getName() != null) {
			JFieldVar propertyField = definedClass.field(JMod.PRIVATE, String.class, "name");
			JExpression init = JExpr.lit(gnode.getName());
			propertyField.init(init);
		}
		// add a url
		if (gnode.getUrl() != null) {
			JFieldVar propertyField = definedClass.field(JMod.PRIVATE, String.class, "url");
			JExpression init = JExpr.lit(gnode.getUrl());
			propertyField.init(init);
		}

		// controls, widgets, apps and screens
		Set<DefaultWeightedEdge> edges = tgraph.edgesOf(gnode);
		for (DefaultWeightedEdge edge : edges) {
			GraphNode targetNode = tgraph.getEdgeTarget(edge);

			if (targetNode == gnode) {
				// self loop
				continue;
			}

			System.out.println("target node " + targetNode);
			if (targetNode.getNodeType() == NodeType.SCREEN) {

				JDefinedClass exitingClassNode = nodeClassMap.get(targetNode);
				JFieldVar field = definedClass.field(JMod.PRIVATE, exitingClassNode, "var" + targetNode.getName());
				JExpression init = JExpr._new(exitingClassNode);
				field.init(init);
				JMethod getterMethod = definedClass.method(JMod.PUBLIC, exitingClassNode, "get" + targetNode.getName());
				JBlock block = getterMethod.body();
				block._return(field);
			} else if (targetNode.getNodeType() == NodeType.APP) {

				JDefinedClass exitingClassNode = nodeClassMap.get(targetNode);
				JFieldVar field = definedClass.field(JMod.PRIVATE, exitingClassNode, "var" + targetNode.getName());
				JExpression init = JExpr._new(exitingClassNode);
				field.init(init);
				JMethod getterMethod = definedClass.method(JMod.PUBLIC, exitingClassNode, "get" + targetNode.getName());
				JBlock block = getterMethod.body();
				block._return(field);
			} else if (targetNode.getNodeType() == NodeType.WIDGET) {

				JDefinedClass exitingClassNode = nodeClassMap.get(targetNode);
				JFieldVar field = definedClass.field(JMod.PRIVATE, exitingClassNode, "var" + targetNode.getName());
				JExpression init = JExpr._new(exitingClassNode);
				field.init(init);
				JMethod getterMethod = definedClass.method(JMod.PUBLIC, exitingClassNode, "get" + targetNode.getName());
				JBlock block = getterMethod.body();
				block._return(field);
			} else {

				JFieldVar field = definedClass.field(JMod.PRIVATE, org.openqa.selenium.By.class,
						"var" + targetNode.getName());
				JExpression init = cm.ref(org.openqa.selenium.By.class).staticInvoke(targetNode.getId().get("by"))
						.arg(JExpr.lit(targetNode.getId().get("locator")));
				field.init(init);
				JMethod getterMethod = definedClass.method(JMod.PUBLIC, org.openqa.selenium.By.class,
						"get" + targetNode.getName());
				JBlock block = getterMethod.body();
				block._return(field);
			}

			// add it to pool of defined classes
			nodeClassMap.put(gnode, definedClass);

			String filepath = "output" + File.separator + "FlyPaper";
			System.out.println("writing file");
			File file = new File(filepath);
			file.mkdirs();
			cm.build(file);

		}
	}

}
