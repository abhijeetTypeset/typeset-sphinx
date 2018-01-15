package io.typeset.model;
import java.util.List;

/**
 * YML model for Widget.
 */
public class Widget extends Control{

	/** The controls. */
	private List<String> controls;

	/**
	 * Gets the controls.
	 *
	 * @return the controls
	 */
	public List<String> getControls() {
		return controls;
	}

	/**
	 * Sets the controls.
	 *
	 * @param controls the new controls
	 */
	public void setControls(List<String> controls) {
		this.controls = controls;
	}

}
