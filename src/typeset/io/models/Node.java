package typeset.io.models;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.xpath.XPath;

public class Node {
	private String name;
	private String url;
	private Map<String, String> id;
	private List<String> actions;
	private List<String> precondition;
	private List<String> implictAssertions = new ArrayList<>();

	public List<String> getImplictAssertions() {
		return implictAssertions;
	}
	
	public void addImplicitAssertion(String assertion) {
		implictAssertions.add(assertion);
	}
	
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

	public List<String> getPrecondition() {
		return precondition;
	}

	public void setPrecondition(List<String> precondition) {
		this.precondition = precondition;
	}

}
