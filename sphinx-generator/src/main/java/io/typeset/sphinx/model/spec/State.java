package io.typeset.sphinx.model.spec;

import java.util.ArrayList;
import java.util.List;

import io.typeset.sphinx.model.assertions.ExplicitAssertion;

/**
 * The Class for Specification State.
 */
public class State {
	
	/** The screen. */
	private String screen;
	
	/** The assertions. */
	private List<String> assertions = new ArrayList<>();
	
	/** The parsed assertion. */
	private ExplicitAssertion parsedAssertion;
	
	/**
	 * Gets the screen.
	 *
	 * @return the screen
	 */
	public String getScreen() {
		return screen;
	}
	
	/**
	 * Sets the screen.
	 *
	 * @param screen the new screen
	 */
	public void setScreen(String screen) {
		this.screen = screen;
	}
	
	/**
	 * Gets the assertions.
	 *
	 * @return the assertions
	 */
	public List<String> getAssertions() {
		return assertions;
	}
	
	/**
	 * Sets the assertions.
	 *
	 * @param assertions the new assertions
	 */
	public void setAssertions(List<String> assertions) {
		this.assertions = assertions;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[Screen: "+screen+"; assertions: "+assertions+"]";
	}
	
	/**
	 * Gets the parsed assertion.
	 *
	 * @return the parsed assertion
	 */
	public ExplicitAssertion getParsedAssertion() {
		return parsedAssertion;
	}
	
	/**
	 * Sets the parsed assertion.
	 *
	 * @param parsedAssertion the new parsed assertion
	 */
	public void setParsedAssertion(ExplicitAssertion parsedAssertion) {
		this.parsedAssertion = parsedAssertion;
	}

}
