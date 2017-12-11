package typeset.io.model.spec;

import java.util.Map;

public class Action {
	private String action_type;
	private String action_name;
	private String action_data;


	@Override
	public String toString() {
		return "[" + action_name + " " + action_type + "]";
	}


	public String getAction_type() {
		return action_type;
	}


	public void setAction_type(String action_type) {
		this.action_type = action_type;
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
