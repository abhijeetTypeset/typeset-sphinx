package typeset.io.models;

import java.util.Map;

public class Action {
	private String control;
	private String action_name;
	private String action_data;


	@Override
	public String toString() {
		return "[" + action_name + " " + control + "]";
	}


	public String getControl() {
		return control;
	}


	public void setControl(String control) {
		this.control = control;
	}


	public String getAction_name() {
		return action_name;
	}


	public void setAction_name(String action_name) {
		this.action_name = action_name;
	}


	public String getAction_data() {
		return action_data;
	}


	public void setAction_data(String action_data) {
		this.action_data = action_data;
	}

}
