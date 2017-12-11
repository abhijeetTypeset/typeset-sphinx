package typeset.io.model;
import java.util.List;

public class Page extends Screen{
	
	private List<String> screens;
	private String url;
	private Boolean root;
	
	public List<String> getScreens() {
		return screens;
	}
	public void setScreens(List<String> screens) {
		this.screens = screens;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Boolean getRoot() {
		return root;
	}
	public void setRoot(Boolean root) {
		this.root = root;
	}

}
