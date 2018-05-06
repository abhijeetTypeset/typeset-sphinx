package io.typeset.sphinx.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Class InconsistentGraphException.
 */
public class InconsistentGraphException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4488498094809092833L;
	private static final Logger logger = LogManager.getLogger("InconsistentGraphException");

	/**
	 * Instantiates a new inconsistent graph exception.
	 */
	public InconsistentGraphException() {
		super();
	}

	/**
	 * Instantiates a new inconsistent graph exception.
	 *
	 * @param s the s
	 */
	public InconsistentGraphException(String s) {
		super("COMPILE ERROR: E110; " + s + "\nConsult https://github.com/TypesetIO/sphinx/wiki/Error-Codes for more details");
	}
}
