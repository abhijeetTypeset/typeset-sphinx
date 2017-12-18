package typeset.io.model;
import java.util.List;

/**
 * YML model for Page.
 */
public class Page extends Screen{
	
	/** The screens. */
	private List<String> screens;
	
	/** The url. */
	private String url;
	
	/** The root. */
	private Boolean root;
	
	/**
	 * Gets the screens.
	 *
	 * @return the screens
	 */
	public List<String> getScreens() {
		return screens;
	}
	
	/**
	 * Sets the screens.
	 *
	 * @param screens the new screens
	 */
	public void setScreens(List<String> screens) {
		this.screens = screens;
	}
	
	/* (non-Javadoc)
	 * @see typeset.io.model.Node#getUrl()
	 */
	public String getUrl() {
		return url;
	}
	
	/* (non-Javadoc)
	 * @see typeset.io.model.Node#setUrl(java.lang.String)
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * Gets the root.
	 *
	 * @return the root
	 */
	public Boolean getRoot() {
		return root;
	}
	
	/**
	 * Sets the root.
	 *
	 * @param root the new root
	 */
	public void setRoot(Boolean root) {
		this.root = root;
	}

}
