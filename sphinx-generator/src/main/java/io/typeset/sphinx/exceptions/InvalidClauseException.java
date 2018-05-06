package io.typeset.sphinx.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Class InvalidClauseException.
 */
public class InvalidClauseException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5459005943556781007L;
	private static final Logger logger = LogManager.getLogger("InvalidClauseException");

	/**
	 * Instantiates a new invalid clause exception.
	 */
	public InvalidClauseException() {
		super();
	}

	/**
	 * Instantiates a new invalid clause exception.
	 *
	 * @param s the s
	 */
	public InvalidClauseException(String s) {
		super("COMPILE ERROR: E102; " + s + "\nConsult https://github.com/TypesetIO/sphinx/wiki/Error-Codes for more details");
	}

}
