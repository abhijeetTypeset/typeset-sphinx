package io.typeset.sphinx.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Class InvalidLiteralException.
 */
public class InvalidLiteralException extends RuntimeException {
	private static final Logger logger = LogManager.getLogger("InvalidLiteralException");

 	/**
	 * 
	 */
	private static final long serialVersionUID = -426007972474179132L;

	/**
 	 * Instantiates a new invalid literal exception.
 	 */
 	public InvalidLiteralException() {
         super();
     }
     
     /**
      * Instantiates a new invalid literal exception.
      *
      * @param s the s
      */
     public InvalidLiteralException(String s) {
 		super("COMPILE ERROR: E103; " + s + "\nConsult https://github.com/TypesetIO/sphinx/wiki/Error-Codes for more details");
     }
}
