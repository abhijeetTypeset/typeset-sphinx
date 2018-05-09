package io.typeset.sphinx.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Class InvalidKeyException.
 */
public class InvalidKeyException extends RuntimeException{
	private static final Logger logger = LogManager.getLogger("InvalidKeyException");

 	/**
	 * 
	 */
	private static final long serialVersionUID = 5973566275620670034L;

	/**
 	 * Instantiates a new invalid model exception.
 	 */
 	public InvalidKeyException() {
         super();
     }
     
     /**
      * Instantiates a new invalid model exception.
      *
      * @param s the s
      */
     public InvalidKeyException(String s) {
 		super("COMPILE ERROR: E111; " + s + "\nConsult https://github.com/TypesetIO/sphinx/wiki/Error-Codes for more details");
     }
}
