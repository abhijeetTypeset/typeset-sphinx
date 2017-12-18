package typeset.io.model;
import java.util.List;


/**
 * YML model for App.
 */
public class App extends Widget{

	/** The widgets. */
	private List<String> widgets;

	/**
	 * Gets the widgets.
	 *
	 * @return the widgets
	 */
	public List<String> getWidgets() {
		return widgets;
	}
	
	/**
	 * Sets the widgets.
	 *
	 * @param widgets the new widgets
	 */
	public void setWidgets(List<String> widgets) {
		this.widgets = widgets;
	}


}
