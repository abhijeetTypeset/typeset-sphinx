package io.typeset.model;

import java.util.ArrayList;
import java.util.List;

import io.typeset.model.assertions.ExplicitAssertion;

/**
 * The Class GraphNode. 
 * Used to represetn all W.A.S.P. elements
 */
public class GraphNode extends Page{
	
	/** The node type. */
	private NodeType nodeType;
	
	/** The parsed pre condition. */
	private ExplicitAssertion parsedPreCondition;
	
	/** The no edges. */
	private List<GraphNode> noEdges = new ArrayList<GraphNode>();

	/**
	 * Gets the node type.
	 *
	 * @return the node type
	 */
	public NodeType getNodeType() {
		return nodeType;
	}

	/**
	 * Sets the node type.
	 *
	 * @param nodeType the new node type
	 */
	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}

	/**
	 * Gets the no edges.
	 *
	 * @return the no edges
	 */
	public List<GraphNode> getNoEdges() {
		return noEdges;
	}

	/**
	 * Adds the no edges.
	 *
	 * @param nodeNoEdge the node no edge
	 */
	public void addNoEdges(GraphNode nodeNoEdge) {
		this.noEdges.add(nodeNoEdge);
	}

	/**
	 * Gets the parsed pre condition.
	 *
	 * @return the parsed pre condition
	 */
	public ExplicitAssertion getParsedPreCondition() {
		return parsedPreCondition;
	}

	/**
	 * Sets the parsed pre condition.
	 *
	 * @param parsedPreCondition the new parsed pre condition
	 */
	public void setParsedPreCondition(ExplicitAssertion parsedPreCondition) {
		this.parsedPreCondition = parsedPreCondition;
	}
}
