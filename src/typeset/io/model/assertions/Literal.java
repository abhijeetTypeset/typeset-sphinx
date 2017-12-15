package typeset.io.model.assertions;

import typeset.io.model.GraphNode;

public class Literal {

	private GraphNode node;
	private String action;
	private boolean isNegation;
	private String textData;

	public Literal(GraphNode node, String action, boolean isNegation) {
		super();
		this.node = node;
		this.action = action;
		this.isNegation = isNegation;
		this.textData = null;
	}

	public Literal(GraphNode node, String action, boolean isNegation, String textData) {
		super();
		this.node = node;
		this.action = action;
		this.isNegation = isNegation;
		this.textData = textData;
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

		String lit = null;
		if (textData == null) {
			lit = action + "(" + node + ")";
		} else {
			lit = action + "(" + node + " , " + textData + ")";
		}
		if (isNegation) {
			lit = "~" + lit;
		}
		return lit;
	}

	public String getTextData() {
		return textData;
	}

}
