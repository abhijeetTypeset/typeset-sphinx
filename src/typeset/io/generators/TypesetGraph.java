package typeset.io.generators;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.Multigraph;

import typeset.io.models.App;
import typeset.io.models.Control;
import typeset.io.models.GraphNode;
import typeset.io.models.Model;
import typeset.io.models.NodeType;
import typeset.io.models.Page;
import typeset.io.models.Screen;
import typeset.io.models.Widget;

public class TypesetGraph {
	private Model model;
	private Map<String, GraphNode> nameNodeMap;
	private Multigraph<GraphNode, DefaultWeightedEdge> multiGraph;
	private GraphNode rootNode;

	public TypesetGraph(Model model) {
		this.model = model;
	}

	private Map<String, GraphNode> getNameNodeMap() {
		return nameNodeMap;
	}

	public Multigraph<GraphNode, DefaultWeightedEdge> initialize()
			throws IllegalAccessException, InvocationTargetException {
		if (model == null) {
			System.out.println("model cannot be null");
			System.exit(0);
		}
		multiGraph = new Multigraph<>(DefaultWeightedEdge.class);
		nameNodeMap = new HashMap<>();

		// add all the vertices
		for (String c : model.getControls().keySet()) {
			System.out.println("Adding control " + c);
			GraphNode v = createNewVertex(model.getControls().get(c), NodeType.CONTROL);
			multiGraph.addVertex(v);
			nameNodeMap.put(c, v);
		}
		for (String w : model.getWidgets().keySet()) {
			System.out.println("Adding widget " + w);
			GraphNode v = createNewVertex(model.getWidgets().get(w), NodeType.WIDGET);
			multiGraph.addVertex(v);
			nameNodeMap.put(w, v);
		}
		for (String a : model.getApps().keySet()) {
			System.out.println("Adding app " + a);
			GraphNode v = createNewVertex(model.getApps().get(a), NodeType.APP);
			multiGraph.addVertex(v);
			nameNodeMap.put(a, v);
		}
		for (String s : model.getScreens().keySet()) {
			System.out.println("Adding screen " + s);
			GraphNode v = createNewVertex(model.getScreens().get(s), NodeType.SCREEN);
			multiGraph.addVertex(v);
			nameNodeMap.put(s, v);
		}
		for (String p : model.getPages().keySet()) {
			System.out.println("Adding page " + p);
			GraphNode v = createNewVertex(model.getPages().get(p), NodeType.PAGE);
			multiGraph.addVertex(v);
			nameNodeMap.put(p, v);
		}

		System.out.println("number of vertices : " + multiGraph.vertexSet().size());

		// add the edges
		for (String c : model.getControls().keySet()) {

			GraphNode controlNode = nameNodeMap.get(c);
			String leadsto = controlNode.getLeadsto();
			if (leadsto != null) {
				GraphNode screenNode = nameNodeMap.get(leadsto);
				System.out.println("Adding edge from control " + c + " to screen " + screenNode);
				multiGraph.addEdge(controlNode, screenNode);
			}
		}

		for (String w : model.getWidgets().keySet()) {
			GraphNode widgetNode = nameNodeMap.get(w);
			List<String> controlList = widgetNode.getControls();
			System.out.println("Widget " + w + " ; " + controlList);
			if (controlList != null) {
				for (String c : controlList) {
					GraphNode controlNode = nameNodeMap.get(c);
					System.out.println("Adding edge from widget " + w + " to control " + c);
					multiGraph.addEdge(widgetNode, controlNode);
				}
			}
		}
		for (String a : model.getApps().keySet()) {
			GraphNode appNode = nameNodeMap.get(a);
			List<String> controlList = appNode.getControls();
			List<String> widgetList = appNode.getWidgets();

			if (widgetList != null) {
				for (String w : widgetList) {
					GraphNode widgetNode = nameNodeMap.get(w);
					System.out.println("Adding edge from app " + a + " to widget " + w);
					multiGraph.addEdge(appNode, widgetNode);
				}
			}

			if (controlList != null) {
				for (String c : controlList) {
					GraphNode controlNode = nameNodeMap.get(c);
					System.out.println("Adding edge from app " + a + " to control " + c);
					multiGraph.addEdge(appNode, controlNode);
				}
			}
		}
		for (String s : model.getScreens().keySet()) {
			GraphNode screenNode = nameNodeMap.get(s);

			List<String> controlList = screenNode.getControls();
			List<String> widgetList = screenNode.getWidgets();
			List<String> appList = screenNode.getApps();

			if (appList != null) {
				for (String a : appList) {
					GraphNode appNode = nameNodeMap.get(a);
					System.out.println("Adding edge from app " + s + " to app " + a);
					multiGraph.addEdge(screenNode, appNode);
				}
			}

			if (widgetList != null) {
				for (String w : widgetList) {
					GraphNode widgetNode = nameNodeMap.get(w);
					System.out.println("Adding edge from app " + s + " to widget " + w);
					multiGraph.addEdge(screenNode, widgetNode);
				}
			}

			if (controlList != null) {
				for (String c : controlList) {
					GraphNode controlNode = nameNodeMap.get(c);
					System.out.println("Adding edge from app " + s + " to control " + c);
					multiGraph.addEdge(screenNode, controlNode);
				}
			}
		}

		for (String p : model.getPages().keySet()) {
			GraphNode pageNode = nameNodeMap.get(p);
			if (pageNode.getRoot()) {
				rootNode = pageNode;
			}

			List<String> controlList = pageNode.getControls();
			List<String> widgetList = pageNode.getWidgets();
			List<String> appList = pageNode.getApps();
			List<String> screenList = pageNode.getScreens();

			if (screenList != null) {
				for (String s : screenList) {
					GraphNode screenNode = nameNodeMap.get(s);
					System.out.println("Adding edge from app " + p + " to app " + s);
					multiGraph.addEdge(pageNode, screenNode);
				}
			}

			if (appList != null) {
				for (String a : appList) {
					GraphNode appNode = nameNodeMap.get(a);
					System.out.println("Adding edge from app " + p + " to app " + a);
					multiGraph.addEdge(pageNode, appNode);
				}
			}

			if (widgetList != null) {
				for (String w : widgetList) {
					GraphNode widgetNode = nameNodeMap.get(w);
					System.out.println("Adding edge from app " + p + " to widget " + w);
					multiGraph.addEdge(pageNode, widgetNode);
				}
			}

			if (controlList != null) {
				for (String c : controlList) {
					GraphNode controlNode = nameNodeMap.get(c);
					System.out.println("Adding edge from app " + p + " to control " + c);
					multiGraph.addEdge(pageNode, controlNode);
				}
			}
		}

		// TODO: store the preconditions

		return multiGraph;

	}

	private GraphNode createNewVertex(Control control, NodeType nodeType)
			throws IllegalAccessException, InvocationTargetException {
		GraphNode graphNode = new GraphNode();
		BeanUtils.copyProperties(graphNode, control);
		graphNode.setNodeType(nodeType);
		return graphNode;
	}

	private GraphNode createNewVertex(Widget widget, NodeType nodeType)
			throws IllegalAccessException, InvocationTargetException {
		GraphNode graphNode = new GraphNode();
		BeanUtils.copyProperties(graphNode, widget);
		graphNode.setNodeType(nodeType);
		return graphNode;
	}

	private GraphNode createNewVertex(App app, NodeType nodeType)
			throws IllegalAccessException, InvocationTargetException {
		GraphNode graphNode = new GraphNode();
		BeanUtils.copyProperties(graphNode, app);
		graphNode.setNodeType(nodeType);
		return graphNode;
	}

	private GraphNode createNewVertex(Screen screen, NodeType nodeType)
			throws IllegalAccessException, InvocationTargetException {
		GraphNode graphNode = new GraphNode();
		BeanUtils.copyProperties(graphNode, screen);
		graphNode.setNodeType(nodeType);
		return graphNode;
	}

	private GraphNode createNewVertex(Page page, NodeType nodeType)
			throws IllegalAccessException, InvocationTargetException {
		GraphNode graphNode = new GraphNode();
		BeanUtils.copyProperties(graphNode, page);
		graphNode.setNodeType(nodeType);
		return graphNode;
	}

	public void addImplicitAssertions() {
		// get all the nodes
		Set<GraphNode> allNodes = multiGraph.vertexSet();
		for (GraphNode node : allNodes) {
			if(node.getNodeType() == NodeType.PAGE) {
				node.addImplicitAssertion("atPage");
			}else {
				node.addImplicitAssertion("canSee");
			}

			if(node.getNodeType() == NodeType.CONTROL && node.getActions().contains("type")) {
				node.addImplicitAssertion("contains");
				node.addImplicitAssertion("equals");
				node.addImplicitAssertion("startsWith");
				node.addImplicitAssertion("endsWith");
				node.addImplicitAssertion("empty");
				node.addImplicitAssertion("notEmpty");
			}
			System.out.println("Node "+node+" has assertions : "+node.getImplictAssertions());
		}
		
	}

}
