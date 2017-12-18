package typeset.io.model;
import java.util.List;

/**
 * YML model for Screen.
 */
public class Screen extends App{
	
	/** The apps. */
	private List<String> apps;

	/**
	 * Gets the apps.
	 *
	 * @return the apps
	 */
	public List<String> getApps() {
		return apps;
	}

	/**
	 * Sets the apps.
	 *
	 * @param apps the new apps
	 */
	public void setApps(List<String> apps) {
		this.apps = apps;
	}

}
