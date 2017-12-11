package typeset.io.model.assertions;

import typeset.io.model.GraphNode;

public class Literal {

	private GraphNode node;
	private String action;
	private boolean isNegation;

	public Literal(GraphNode node, String action, boolean isNegation) {
		super();
		this.node = node;
		this.action = action;
		this.isNegation = isNegation;
	}

	public GraphNode getNode() {
		return node;
	}

	public String getAction() {
		return action;
	}

	public boolean isNegation() {
		return isNegation;
	}

	@Override
	public String toString() {

		String lit = action + "(" + node + ")";
		if (isNegation) {
			lit = "~" + lit;
		}
		return lit;
	}

}
