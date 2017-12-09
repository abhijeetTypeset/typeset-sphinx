package typeset.io.models;

import java.util.Map;

public class Action {
	private String control;
	private String func;

	public String getControl() {
		return control;
	}

	public void setControl(String control) {
		this.control = control;
	}

	public String getFunc() {
		return func;
	}

	public void setFunc(String func) {
		this.func = func;
	}

	@Override
	public String toString() {
		return "[" + func + " " + control + "]";
	}

}
