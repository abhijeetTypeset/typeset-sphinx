package typeset.io.model;
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
	private String action_type;
	private String action_data;
	private List<String> precondition;
	private String wait_time;
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

	public String getAction_data() {
		return action_data;
	}

	public void setAction_data(String action_data) {
		this.action_data = action_data;
	}

	public String getAction_type() {
		return action_type;
	}

	public void setAction_type(String action_type) {
		this.action_type = action_type;
	}

	public String getWait_time() {
		return wait_time;
	}

	public void setWait_time(String wait_time) {
		this.wait_time = wait_time;
	}

}
