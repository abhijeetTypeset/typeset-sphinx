package typeset.io.models;
import java.util.Map;

public class Model {
	private Map<String, Control> controls;
	private Map<String, Widget> widgets;
	private Map<String, App> apps;
	private Map<String, Screen> screens;
	private Map<String, Page> pages;
	public Map<String, Control> getControls() {
		return controls;
	}
	public void setControls(Map<String, Control> controls) {
		this.controls = controls;
	}
	public Map<String, Widget> getWidgets() {
		return widgets;
	}
	public void setWidgets(Map<String, Widget> widgets) {
		this.widgets = widgets;
	}
	public Map<String, App> getApps() {
		return apps;
	}
	public void setApps(Map<String, App> apps) {
		this.apps = apps;
	}
	public Map<String, Screen> getScreens() {
		return screens;
	}
	public void setScreens(Map<String, Screen> screens) {
		this.screens = screens;
	}
	public Map<String, Page> getPages() {
		return pages;
	}
	public void setPages(Map<String, Page> pages) {
		this.pages = pages;
	}
	
	
}
