package typeset.io.models;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;

public class Widget extends Control{

	private List<String> controls;

	public List<String> getControls() {
		return controls;
	}

	public void setControls(List<String> controls) {
		this.controls = controls;
	}

}
