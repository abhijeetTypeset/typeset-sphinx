package io.typeset.sphinx.model.assertions;

import io.typeset.sphinx.model.GraphNode;


/**
 * The Class Literal.
 */
public class Literal {

	/** The node. */
	private GraphNode node;
	
	/** The action. */
	private String action;
	
	/** The is negation. */
	private boolean isNegation;
	
	/** The text data. */
	private String textData;
	
	private String literal_no;

	/**
	 * Instantiates a new literal.
	 *
	 * @param node the node
	 * @param action the action
	 * @param isNegation the is negation
	 */
	public Literal(GraphNode node, String literal_no, String action, boolean isNegation) {
		super();
		this.node = node;
		this.action = action;
		this.literal_no = literal_no;
		this.isNegation = isNegation;
		this.textData = null;
	}

	/**
	 * Instantiates a new literal.
	 *
	 * @param node the node
	 * @param action the action
	 * @param isNegation the is negation
	 * @param textData the text data
	 */
	public Literal(GraphNode node, String literal_no, String action,  boolean isNegation, String textData) {
		super();
		this.node = node;
		this.action = action;
		this.literal_no = literal_no;
		this.isNegation = isNegation;
		this.textData = textData;
	}

	/**
	 * Gets the node.
	 *
	 * @return the node
	 */
	public GraphNode getNode() {
		return node;
	}

	/**
	 * Gets the action.
	 *
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * Checks if is negation.
	 *
	 * @return true, if is negation
	 */
	public boolean isNegation() {
		return isNegation;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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

	/**
	 * Gets the text data.
	 *
	 * @return the text data
	 */
	public String getTextData() {
		return textData;
	}
	
	public String getLiteral_no() {
		return literal_no;
	}

	public void setLiteral_no(String literal_no) {
		this.literal_no = literal_no;
	}

}
