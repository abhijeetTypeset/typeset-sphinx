package typeset.io.model.spec;

import java.util.ArrayList;
import java.util.List;

import typeset.io.model.assertions.ExplicitAssertion;

public class State {
	
	private String screen;
	private List<String> assertions = new ArrayList<>();
	private ExplicitAssertion parsedAssertion;
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
	public ExplicitAssertion getParsedAssertion() {
		return parsedAssertion;
	}
	public void setParsedAssertion(ExplicitAssertion parsedAssertion) {
		this.parsedAssertion = parsedAssertion;
	}

}
