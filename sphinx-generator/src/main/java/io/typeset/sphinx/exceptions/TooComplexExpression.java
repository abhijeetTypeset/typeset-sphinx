package io.typeset.sphinx.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Class TooComplexExpression.
 */
public class TooComplexExpression extends RuntimeException {
	private static final Logger logger = LogManager.getLogger("TooComplexExpression");

	/**
	 * 
	 */
	private static final long serialVersionUID = 4273591674489895258L;

	/**
	 * Instantiates a new too complex expression.
	 */
	public TooComplexExpression() {
		super();
	}

	/**
	 * Instantiates a new too complex expression.
	 *
	 * @param s the s
	 */
	public TooComplexExpression(String s) {
		super("COMPILE ERROR: E109 ;" + s + "\nConsult https://github.com/TypesetIO/sphinx/wiki/Error-Codes for more details");
	}
}
