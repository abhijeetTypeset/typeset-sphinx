package io.typeset.sphinx.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InvalidStackStateException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4525904354202404193L;
	private static final Logger logger = LogManager.getLogger("InvalidStackStateException");

	/**
	 * 
	 */


	/**
	 * Instantiates a new invalid path exception.
	 */
	public InvalidStackStateException() {
		super();
	}

	/**
	 * Instantiates a new invalid path exception.
	 *
	 * @param s the s
	 */
	public InvalidStackStateException(String s) {
		super("COMPILE ERROR: E108 ;" + s + "\nConsult https://github.com/TypesetIO/sphinx/wiki/Error-Codes for more details");
	}
}
