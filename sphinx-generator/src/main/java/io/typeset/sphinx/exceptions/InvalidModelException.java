package io.typeset.sphinx.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Class InvalidModelException.
 */
public class InvalidModelException extends RuntimeException{
	private static final Logger logger = LogManager.getLogger("InvalidModelException");

 	/**
	 * 
	 */
	private static final long serialVersionUID = 5973566275620670034L;

	/**
 	 * Instantiates a new invalid model exception.
 	 */
 	public InvalidModelException() {
         super();
     }
     
     /**
      * Instantiates a new invalid model exception.
      *
      * @param s the s
      */
     public InvalidModelException(String s) {
 		super("COMPILE ERROR: E104; " + s + "\nConsult https://github.com/TypesetIO/sphinx/wiki/Error-Codes for more details");
     }
}
