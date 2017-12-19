package typeset.io.exceptions;

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
		super(s);
	}
}
