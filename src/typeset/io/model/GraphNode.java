package typeset.io.model;

import java.util.ArrayList;
import java.util.List;

import typeset.io.model.assertions.ExplicitAssertion;

public class GraphNode extends Page{
	
	private NodeType nodeType;
	
	private ExplicitAssertion parsedPreCondition;
	
	private List<GraphNode> noEdges = new ArrayList<GraphNode>();

	public NodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}

	public List<GraphNode> getNoEdges() {
		return noEdges;
	}

	public void addNoEdges(GraphNode nodeNoEdge) {
		this.noEdges.add(nodeNoEdge);
	}

	public ExplicitAssertion getParsedPreCondition() {
		return parsedPreCondition;
	}

	public void setParsedPreCondition(ExplicitAssertion parsedPreCondition) {
		this.parsedPreCondition = parsedPreCondition;
	}
}
