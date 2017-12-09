package typeset.io.generators;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.beanutils.BeanUtils;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.Multigraph;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.IntegerComponentNameProvider;
import org.jgrapht.io.StringComponentNameProvider;

import typeset.io.exceptions.InvalidClauseException;
import typeset.io.exceptions.InvalidLiteralException;
import typeset.io.exceptions.InvalidModelException;
import typeset.io.exceptions.InvalidNodeException;
import typeset.io.models.App;
import typeset.io.models.Control;
import typeset.io.models.GraphNode;
import typeset.io.models.Model;
import typeset.io.models.NodeType;
import typeset.io.models.Page;
import typeset.io.models.Screen;
import typeset.io.models.Widget;
import typeset.io.models.assertions.Clause;
import typeset.io.models.assertions.ExplicitAssertion;
import typeset.io.models.assertions.Literal;

public class GraphGenerator {
	private Model model;
	private Map<String, GraphNode> nameNodeMap;
	private DefaultDirectedGraph<GraphNode, DefaultEdge> graph;
	private GraphNode rootNode;
	private String targetDir;
	private List<GraphNode> nodesWithPrecondition;

	public GraphGenerator(Model model, String targetDir) {
		this.model = model;
		this.targetDir = targetDir;
	}

	public GraphNode getNodeByKey(String key) {
		GraphNode node = nameNodeMap.get(key);
		if (node != null) {
			return node;
		}

		throw new InvalidLiteralException("No such symbol as " + key);
	}

	public DefaultDirectedGraph<GraphNode, DefaultEdge> initialize()
			throws IllegalAccessException, InvocationTargetException {
		if (model == null) {
			throw new InvalidModelException("model cannot be null");
		}
		graph = new DefaultDirectedGraph<>(DefaultEdge.class);
		nameNodeMap = new HashMap<>();
		nodesWithPrecondition = new ArrayList<>();

		// add all the vertices

		for (String c : model.getControls().keySet()) {
			System.out.println("Adding control " + c);
			GraphNode v = createNewVertex(model.getControls().get(c), NodeType.CONTROL);
			graph.addVertex(v);
			nameNodeMap.put(c, v);
		}
		for (String w : model.getWidgets().keySet()) {
			System.out.println("Adding widget " + w);
			GraphNode v = createNewVertex(model.getWidgets().get(w), NodeType.WIDGET);
			graph.addVertex(v);
			nameNodeMap.put(w, v);
		}
		for (String a : model.getApps().keySet()) {
			System.out.println("Adding app " + a);
			GraphNode v = createNewVertex(model.getApps().get(a), NodeType.APP);
			graph.addVertex(v);
			nameNodeMap.put(a, v);
		}
		for (String s : model.getScreens().keySet()) {
			System.out.println("Adding screen " + s);
			GraphNode v = createNewVertex(model.getScreens().get(s), NodeType.SCREEN);
			graph.addVertex(v);
			nameNodeMap.put(s, v);
		}
		for (String p : model.getPages().keySet()) {
			System.out.println("Adding page " + p);
			GraphNode v = createNewVertex(model.getPages().get(p), NodeType.PAGE);
			graph.addVertex(v);
			nameNodeMap.put(p, v);
		}

		System.out.println("number of vertices : " + graph.vertexSet().size());

		// add the edges

		List<GraphNode> controlsThatLeadToParent = new ArrayList<>();

		for (String c : model.getControls().keySet()) {

			GraphNode controlNode = getNodeByKey(c);
			String leadsto = controlNode.getLeadsto();
			if (leadsto != null) {
				GraphNode screenNode = getNodeByKey(leadsto);
				System.out.println("Adding edge from control " + controlNode + " to screen " + screenNode);
				graph.addEdge(controlNode, screenNode);
			} else {
				// add edge back to parent
				controlsThatLeadToParent.add(controlNode);
			}
		}

		for (String w : model.getWidgets().keySet()) {
			GraphNode widgetNode = getNodeByKey(w);
			List<String> controlList = widgetNode.getControls();
			System.out.println("Widget " + w + " ; " + controlList);
			if (controlList != null) {
				for (String c : controlList) {
					GraphNode controlNode = getNodeByKey(c);
					System.out.println("Adding edge from widget " + widgetNode + " to control " + controlNode);
					graph.addEdge(widgetNode, controlNode);
				}
			}
		}
		for (String a : model.getApps().keySet()) {
			GraphNode appNode = getNodeByKey(a);
			List<String> controlList = appNode.getControls();
			List<String> widgetList = appNode.getWidgets();

			if (widgetList != null) {
				for (String w : widgetList) {
					GraphNode widgetNode = getNodeByKey(w);
					System.out.println("Adding edge from app " + appNode + " to widget " + appNode);
					graph.addEdge(appNode, widgetNode);
				}
			}

			if (controlList != null) {
				for (String c : controlList) {
					GraphNode controlNode = getNodeByKey(c);
					System.out.println("Adding edge from app " + appNode + " to control " + controlNode);
					graph.addEdge(appNode, controlNode);
				}
			}
		}
		for (String s : model.getScreens().keySet()) {
			GraphNode screenNode = getNodeByKey(s);

			List<String> controlList = screenNode.getControls();
			List<String> widgetList = screenNode.getWidgets();
			List<String> appList = screenNode.getApps();

			if (appList != null) {
				for (String a : appList) {
					GraphNode appNode = getNodeByKey(a);
					if (doesNotHaveIncomingEdges(appNode)) {
						System.out.println("Adding edge from screen " + screenNode + " to app " + appNode);
						graph.addEdge(screenNode, appNode);
					}
				}
			}

			if (widgetList != null) {
				for (String w : widgetList) {
					GraphNode widgetNode = getNodeByKey(w);
					if (doesNotHaveIncomingEdges(widgetNode)) {
						System.out.println("Adding edge from screen " + screenNode + " to widget " + widgetNode);
						graph.addEdge(screenNode, widgetNode);
					}
				}
			}

			if (controlList != null) {
				for (String c : controlList) {
					GraphNode controlNode = getNodeByKey(c);
					if (doesNotHaveIncomingEdges(controlNode)) {
						System.out.println("Adding edge from screen " + screenNode + " to control " + controlNode);
						graph.addEdge(screenNode, controlNode);
					}
				}
			}
		}

		for (String p : model.getPages().keySet()) {
			GraphNode pageNode = getNodeByKey(p);
			if (pageNode.getRoot()) {
				rootNode = pageNode;
			}

			List<String> controlList = pageNode.getControls();
			List<String> widgetList = pageNode.getWidgets();
			List<String> appList = pageNode.getApps();
			List<String> screenList = pageNode.getScreens();

			if (screenList != null) {
				for (String s : screenList) {
					GraphNode screenNode = getNodeByKey(s);
					if (doesNotHaveIncomingEdges(screenNode)) {
						System.out.println("Adding edge from page " + pageNode + " to screen " + screenNode);
						graph.addEdge(pageNode, screenNode);
					}
				}
			}

			if (appList != null) {
				for (String a : appList) {
					GraphNode appNode = getNodeByKey(a);
					if (doesNotHaveIncomingEdges(appNode)) {
						System.out.println("Adding edge from page " + pageNode + " to app " + appNode);
						graph.addEdge(pageNode, appNode);
					}
				}
			}

			if (widgetList != null) {
				for (String w : widgetList) {
					GraphNode widgetNode = getNodeByKey(w);
					if (doesNotHaveIncomingEdges(widgetNode)) {
						System.out.println("Adding edge from page " + pageNode + " to widget " + widgetNode);
						graph.addEdge(pageNode, widgetNode);
					}
				}
			}

			if (controlList != null) {
				for (String c : controlList) {
					GraphNode controlNode = getNodeByKey(c);
					if (doesNotHaveIncomingEdges(controlNode)) {
						System.out.println("Adding edge from page " + pageNode + " to control " + controlNode);
						graph.addEdge(pageNode, controlNode);
					}
				}
			}
		}

		// add edges to controls that haven't been assigned to any edges

		for (GraphNode controlNode : controlsThatLeadToParent) {
			// get incoming edges
			Set<DefaultEdge> edges = graph.incomingEdgesOf(controlNode);

			// note that the edges here should not be more that one

			for (DefaultEdge edge : edges) {
				System.out.println("Incoming edges to control " + controlNode + " from " + graph.getEdgeSource(edge));
				graph.addEdge(controlNode, graph.getEdgeSource(edge));
			}

		}

		// resolve preconditions

		resolvePreconditons();

		return graph;

	}

	private Literal parseLiteral(String literalString) {
		String[] parts = literalString.split("%");

		if (parts.length != 3) {
			throw new InvalidClauseException(literalString);
		}

		GraphNode node = getNodeByKey(parts[0]);
		Literal literal = new Literal(node, parts[2], parts[1].toLowerCase().equals("not"));
		return literal;
	}

	private Clause parseClasuse(String clauseString) {
		Clause clause = new Clause();
		String[] literals = clauseString.split(",");
		for (String literalString : literals) {
			Literal literal = parseLiteral(literalString);
			clause.addLiterals(literal);
		}

		return clause;
	}

	private ExplicitAssertion parsePrecondtion(List<String> precodtionString) {
		// System.out.println("pre-condition " + precodtionString);
		ExplicitAssertion explicitAssertion = new ExplicitAssertion();
		for (String precs : precodtionString) {
			Clause clause = parseClasuse(precs);
			explicitAssertion.addclauses(clause);
		}

		return explicitAssertion;

	}

	private void resolvePreconditons() {
		for (GraphNode node : nodesWithPrecondition) {
			List<String> precondtionString = node.getPrecondition();

			ExplicitAssertion precondition = parsePrecondtion(precondtionString);
			System.out.println(precondition);
		}
	}

	private boolean doesNotHaveIncomingEdges(GraphNode node) {
		Set<DefaultEdge> edges = graph.incomingEdgesOf(node);

		if (edges.size() > 0) {
			return false;
		} else {
			return true;
		}
	}

	private GraphNode createNewVertex(Control control, NodeType nodeType)
			throws IllegalAccessException, InvocationTargetException {
		GraphNode graphNode = new GraphNode();
		BeanUtils.copyProperties(graphNode, control);
		graphNode.setNodeType(nodeType);
		if (graphNode.getPrecondition() != null) {
			nodesWithPrecondition.add(graphNode);
		}
		return graphNode;
	}

	private GraphNode createNewVertex(Widget widget, NodeType nodeType)
			throws IllegalAccessException, InvocationTargetException {
		GraphNode graphNode = new GraphNode();
		BeanUtils.copyProperties(graphNode, widget);
		graphNode.setNodeType(nodeType);
		if (graphNode.getPrecondition() != null) {
			nodesWithPrecondition.add(graphNode);
		}
		return graphNode;
	}

	private GraphNode createNewVertex(App app, NodeType nodeType)
			throws IllegalAccessException, InvocationTargetException {
		GraphNode graphNode = new GraphNode();
		BeanUtils.copyProperties(graphNode, app);
		graphNode.setNodeType(nodeType);
		if (graphNode.getPrecondition() != null) {
			nodesWithPrecondition.add(graphNode);
		}
		return graphNode;
	}

	private GraphNode createNewVertex(Screen screen, NodeType nodeType)
			throws IllegalAccessException, InvocationTargetException {
		GraphNode graphNode = new GraphNode();
		BeanUtils.copyProperties(graphNode, screen);
		graphNode.setNodeType(nodeType);
		if (graphNode.getPrecondition() != null) {
			nodesWithPrecondition.add(graphNode);
		}
		return graphNode;
	}

	private GraphNode createNewVertex(Page page, NodeType nodeType)
			throws IllegalAccessException, InvocationTargetException {
		GraphNode graphNode = new GraphNode();
		BeanUtils.copyProperties(graphNode, page);
		graphNode.setNodeType(nodeType);
		if (graphNode.getPrecondition() != null) {
			nodesWithPrecondition.add(graphNode);
		}
		return graphNode;
	}

	public void addImplicitAssertions() {
		// get all the nodes
		Set<GraphNode> allNodes = graph.vertexSet();
		for (GraphNode node : allNodes) {
			if (node.getNodeType() == NodeType.PAGE) {
				node.addImplicitAssertion("atPage");
			} else {
				node.addImplicitAssertion("canSee");
			}

			if (node.getNodeType() == NodeType.CONTROL && node.getActions().contains("type")) {
				node.addImplicitAssertion("contains");
				node.addImplicitAssertion("equals");
				node.addImplicitAssertion("startsWith");
				node.addImplicitAssertion("endsWith");
				node.addImplicitAssertion("empty");
			}
			System.out.println("Node " + node + " has assertions : " + node.getImplictAssertions());
		}

	}

	public void consistencyCheck() {
		
		// TODO: check if it is consistent
		// 1. Isolated nodes not allowed
		// 2. Referencing of non-existing nodes not allowed
		// 3. Nodes must have respective properties initialized
		// 4. A control can have only one parent
		// 5. A control can only lead to one location
		
		Set<GraphNode> allNodes = graph.vertexSet();
		for (GraphNode node : allNodes) {

			GraphPath<GraphNode, DefaultEdge> path = DijkstraShortestPath.findPathBetween(graph, rootNode, node);
			System.out.println("path between " + rootNode + " and " + node + " is " + path);

		}

	}


	public void toDot() throws IOException {

		String graphOutputDir = targetDir + File.separator + "graphs";
		File file = new File(graphOutputDir);
		file.mkdirs();

		String graphFilePath = graphOutputDir + file.separator + "graph.dot";
		ComponentNameProvider<GraphNode> vertexIDProvider = new IntegerComponentNameProvider<GraphNode>();
		ComponentNameProvider<GraphNode> vertexLabelProvider = new StringComponentNameProvider<GraphNode>();

		DOTExporter<GraphNode, DefaultEdge> exporter = new DOTExporter<GraphNode, DefaultEdge>(vertexIDProvider,
				vertexLabelProvider, null);

		System.out.println("Writing graph to file " + graphFilePath);
		exporter.exportGraph(graph, new FileWriter(graphFilePath));

		try {
			// String pngFilePath = graphOutputDir + file.separator + "graph.png";
			String pngFilePath = "/home/ub16/share" + file.separator + "graph.png";

			String[] command = { "dot", "-Tpng", graphFilePath, "-o", pngFilePath };
			System.out.println("Png conversion  : " + Arrays.toString(command));
			ProcessBuilder probuilder = new ProcessBuilder(command);
			Process process = probuilder.start();
		} catch (Exception e) {
			System.out.println("Error while generating the graph png");
		}

	}

	public Map<String, GraphNode> getNameNodeMap() {
		return nameNodeMap;
	}

}
