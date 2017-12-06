package typeset.io.models;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;

public class Node {
	private String name;
	private String url;
	private Map<String, String> id;
	private List<String> actions;
	private List<String> assertions;
	private Map<String, String> precondition;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getActions() {
		return actions;
	}

	public void setActions(List<String> actions) {
		this.actions = actions;
	}

	public List<String> getAssertions() {
		return assertions;
	}

	public void setAssertions(List<String> assertions) {
		this.assertions = assertions;
	}

	public Map<String, String> getPrecondition() {
		return precondition;
	}

	public void setPrecondition(Map<String, String> precondition) {
		this.precondition = precondition;
	}

	public Map<String, String> getId() {
		return id;
	}

	public void setId(Map<String, String> id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return name;
	}

}
