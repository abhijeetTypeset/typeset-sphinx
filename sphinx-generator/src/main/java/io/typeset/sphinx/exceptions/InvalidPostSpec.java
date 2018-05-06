package io.typeset.sphinx.exceptions;

public class InvalidPostSpec extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8955351335600878402L;

	public InvalidPostSpec() {
		super();
	}

	public InvalidPostSpec(String s) {
		super("COMPILE ERROR: E107 ;" + s + "\nConsult https://github.com/TypesetIO/sphinx/wiki/Error-Codes for more details");
	}
}
