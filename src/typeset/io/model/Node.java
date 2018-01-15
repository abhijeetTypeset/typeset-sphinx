package typeset.io.model;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



/**
 * YML model for Node.
 */
public class Node {
	
	/** The name. */
	private String name;
	
	/** The url. */
	private String url;
	
	/** The id. */
	private Map<String, String> id;
	
	/** The action type. */
	private String action_type;
	
	/** The action data. */
	private String action_data;
	
	/** The precondition. */
	private List<String> precondition;
	
	/** The wait time. */
	private String wait_time;
	
	private boolean defaultComponent;
	
	/** The implict assertions. */
	private List<String> implictAssertions = new ArrayList<>();

	/**
	 * Gets the implict assertions.
	 *
	 * @return the implict assertions
	 */
	public List<String> getImplictAssertions() {
		return implictAssertions;
	}
	
	/**
	 * Adds the implicit assertion.
	 *
	 * @param assertion the assertion
	 */
	public void addImplicitAssertion(String assertion) {
		implictAssertions.add(assertion);
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
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Map<String, String> getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the id
	 */
	public void setId(Map<String, String> id) {
		this.id = id;
	}

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the url.
	 *
	 * @param url the new url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Gets the precondition.
	 *
	 * @return the precondition
	 */
	public List<String> getPrecondition() {
		return precondition;
	}

	/**
	 * Sets the precondition.
	 *
	 * @param precondition the new precondition
	 */
	public void setPrecondition(List<String> precondition) {
		this.precondition = precondition;
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
	 * @param action_data the new action data
	 */
	public void setAction_data(String action_data) {
		this.action_data = action_data;
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
	 * @param action_type the new action type
	 */
	public void setAction_type(String action_type) {
		this.action_type = action_type;
	}

	/**
	 * Gets the wait time.
	 *
	 * @return the wait time
	 */
	public String getWait_time() {
		return wait_time;
	}

	/**
	 * Sets the wait time.
	 *
	 * @param wait_time the new wait time
	 */
	public void setWait_time(String wait_time) {
		this.wait_time = wait_time;
	}

	public boolean isDefaultComponent() {
		return defaultComponent;
	}

	public void setDefaultComponent(boolean defaultComponent) {
		this.defaultComponent = defaultComponent;
	}

}
