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

import typeset.io.models.App;
import typeset.io.models.GraphNode;
import typeset.io.models.Model;
import typeset.io.models.NodeType;
import typeset.io.models.Page;
import typeset.io.models.Screen;
import typeset.io.models.Widget;

import org.openqa.selenium.By;

public class Generator {
	private DefaultDirectedGraph<GraphNode, DefaultEdge> tgraph;
	private static Map<GraphNode, JDefinedClass> nodeClassMap = new HashMap<>();
	private String outputDir;
	private JDefinedClass definedAbstractNode;


	public Generator(DefaultDirectedGraph<GraphNode, DefaultEdge> tgraph, String outputDir) {
		this.tgraph = tgraph;
		this.outputDir = outputDir;
		
	}

	public void generateClasses() throws IOException, JClassAlreadyExistsException {

		if (tgraph == null) {
			System.out.println("Cannot proceed with a null graph");
			System.exit(0);
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

	}

	private void generateAbstractClasses() throws JClassAlreadyExistsException, IOException {
		JCodeModel cm = new JCodeModel();
		String packageName = "model";
		String className = packageName + "." + "Node";
		

		ClassType t = ClassType.CLASS;
		definedAbstractNode = cm._class(JMod.PUBLIC|JMod.ABSTRACT, className, t );
		
		definedAbstractNode.method(JMod.PUBLIC|JMod.ABSTRACT, org.openqa.selenium.By.class, "getId");
		definedAbstractNode.method(JMod.PUBLIC|JMod.ABSTRACT, String.class, "getName");
		
		String filepath = outputDir + File.separator + "FlyPaper" + File.separator + "src" + File.separator + "main"
				+ File.separator + "java";
		File file = new File(filepath);
		file.mkdirs();
		cm.build(file);
	}

	private void copyBaseClasses() throws IOException {
		
		String baseDirStructure = "res/baseDirStructure";

		FileUtils.copyDirectory(new File(baseDirStructure), new File(outputDir));
		System.out.println("Copying base directory structure from " + baseDirStructure + " to " + outputDir);
	}

	private void generateClassFile(GraphNode gnode) throws JClassAlreadyExistsException, IOException {
		System.out.println("===| Generated class for " + gnode);
		JCodeModel cm = new JCodeModel();
		String packageName = "model" + "." + gnode.getNodeType() + "s";
		String className = packageName + "." + firstLetterCaptial(gnode.getName());

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
		// add actions
		if (gnode.getActions() != null) {
			JFieldVar propertyField = definedClass.field(JMod.PRIVATE, cm.ref(String.class).array(),
					"permissibleActions");
			JArray array = JExpr.newArray(cm.ref(String.class));
			for (String action : gnode.getActions()) {
				array.add(JExpr.lit(action));
			}
			propertyField.init(array);
			JMethod getterMethod = definedClass.method(JMod.PUBLIC, cm.ref(String.class).array(), "getActions");
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
				JMethod getterMethod = definedClass.method(JMod.PUBLIC, exitingClassNode, "get" + firstLetterCaptial(targetNode.getName()));
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

	private String firstLetterCaptial(String name) {
		if (name.length() <= 1) {
			return name.toUpperCase();
		} else {
			return name.substring(0, 1).toUpperCase() + name.substring(1);
		}
	}


}
