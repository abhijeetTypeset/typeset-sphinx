package typeset.io.models;

import java.util.ArrayList;
import java.util.List;

public class State {
	
	private String screen;
	private List<String> assertions = new ArrayList<>();
	public String getScreen() {
		return screen;
	}
	public void setScreen(String screen) {
		this.screen = screen;
	}
	public List<String> getAssertions() {
		return assertions;
	}
	public void setAssertions(List<String> assertions) {
		this.assertions = assertions;
	}
	
	@Override
	public String toString() {
		return "[Screen: "+screen+"; assertions: "+assertions+"]";
	}

}
