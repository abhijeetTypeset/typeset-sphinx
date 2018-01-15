package io.typeset.sphinx.model.spec;

import java.util.List;
import java.util.Map;


/**
 * The Class for Specification
 */
public class Spec {
	
	/** The given. */
	private State given;
	
	/** The wait. */
	private String wait;
	
	/** The when. */
	private Map<String, Action> when ;
	
	/** The then. */
	private State then;
	
	/** The name. */
	private String name;
	
	private List<String> post;
	
	/**
	 * Gets the given.
	 *
	 * @return the given
	 */
	public State getGiven() {
		return given;
	}

	/**
	 * Sets the given.
	 *
	 * @param given the new given
	 */
	public void setGiven(State given) {
		this.given = given;
	}

	/**
	 * Gets the wait.
	 *
	 * @return the wait
	 */
	public String getWait() {
		return wait;
	}

	/**
	 * Sets the wait.
	 *
	 * @param wait the new wait
	 */
	public void setWait(String wait) {
		this.wait = wait;
	}

	/**
	 * Gets the then.
	 *
	 * @return the then
	 */
	public State getThen() {
		return then;
	}

	/**
	 * Sets the then.
	 *
	 * @param then the new then
	 */
	public void setThen(State then) {
		this.then = then;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String spec = "";
		spec += "\nName : " + name + "\n";
		spec += "\n\tGiven "+given+"\n";
		spec += "\tWhen "+when +"\n";
		spec += "\tWait "+wait+"\n";
		spec += "\tThen "+then+"\n";
		spec += "\tPost "+post+"\n";
		return spec;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the when.
	 *
	 * @return the when
	 */
	public Map<String, Action> getWhen() {
		return when;
	}

	/**
	 * Sets the when.
	 *
	 * @param when the when
	 */
	public void setWhen(Map<String, Action> when) {
		this.when = when;
	}

	public List<String> getPost() {
		return post;
	}

	public void setPost(List<String> postTest) {
		this.post = postTest;
	}
}
