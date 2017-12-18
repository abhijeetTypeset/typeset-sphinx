package typeset.io.model;
import java.util.Map;

/**
 * Used to hold the YML Model.
 */
public class Model {
	
	/** The controls. */
	private Map<String, Control> controls;
	
	/** The widgets. */
	private Map<String, Widget> widgets;
	
	/** The apps. */
	private Map<String, App> apps;
	
	/** The screens. */
	private Map<String, Screen> screens;
	
	/** The pages. */
	private Map<String, Page> pages;
	
	/**
	 * Gets the controls.
	 *
	 * @return the controls
	 */
	public Map<String, Control> getControls() {
		return controls;
	}
	
	/**
	 * Sets the controls.
	 *
	 * @param controls the controls
	 */
	public void setControls(Map<String, Control> controls) {
		this.controls = controls;
	}
	
	/**
	 * Gets the widgets.
	 *
	 * @return the widgets
	 */
	public Map<String, Widget> getWidgets() {
		return widgets;
	}
	
	/**
	 * Sets the widgets.
	 *
	 * @param widgets the widgets
	 */
	public void setWidgets(Map<String, Widget> widgets) {
		this.widgets = widgets;
	}
	
	/**
	 * Gets the apps.
	 *
	 * @return the apps
	 */
	public Map<String, App> getApps() {
		return apps;
	}
	
	/**
	 * Sets the apps.
	 *
	 * @param apps the apps
	 */
	public void setApps(Map<String, App> apps) {
		this.apps = apps;
	}
	
	/**
	 * Gets the screens.
	 *
	 * @return the screens
	 */
	public Map<String, Screen> getScreens() {
		return screens;
	}
	
	/**
	 * Sets the screens.
	 *
	 * @param screens the screens
	 */
	public void setScreens(Map<String, Screen> screens) {
		this.screens = screens;
	}
	
	/**
	 * Gets the pages.
	 *
	 * @return the pages
	 */
	public Map<String, Page> getPages() {
		return pages;
	}
	
	/**
	 * Sets the pages.
	 *
	 * @param pages the pages
	 */
	public void setPages(Map<String, Page> pages) {
		this.pages = pages;
	}
	
	
}
