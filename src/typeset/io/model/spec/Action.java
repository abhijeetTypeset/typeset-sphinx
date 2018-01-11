package typeset.io.model.spec;

import typeset.io.exceptions.InvalidLiteralException;

/**
 * The Class for Specification Action.
 */
public class Action {

	/** The action type. */
	private String action_type;

	/** The action name. */
	private String action_name;

	/** The action data. */
	private String action_data;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + action_name + " " + action_type + "]";
	}

	/**
	 * Gets the action type.
	 *
	 * @return the action type
	 */
	public String getAction_type() {
		return action_type;
	}

	/**
	 * Sets the action type.
	 *
	 * @param action_type
	 *            the new action type
	 */
	public void setAction_type(String action_type) {
		this.action_type = action_type;
	}

	/**
	 * Gets the action name.
	 *
	 * @return the action name
	 */
	public String getAction_name() {
		String[] temp = action_name.split(";");
		if (temp.length > 2) {
			throw new InvalidLiteralException("only provide action number and/or name");
		}

		return temp[0].trim();
	}

	/**
	 * Sets the action name.
	 *
	 * @param action_name
	 *            the new action name
	 */
	public void setAction_name(String action_name) {
		this.action_name = action_name;
	}

	/**
	 * Gets the action data.
	 *
	 * @return the action data
	 */
	public String getAction_data() {
		return action_data;
	}

	/**
	 * Sets the action data.
	 *
	 * @param action_data
	 *            the new action data
	 */
	public void setAction_data(String action_data) {
		this.action_data = action_data;
	}

	public String getAction_no() {
		String[] temp = action_name.split(";");
		if (temp.length > 2) {
			throw new InvalidLiteralException("only provide action number and/or name");
		}
		if (temp.length == 2) {
			return temp[1].trim();
		} else {
			return "0";
		}

	}

}
