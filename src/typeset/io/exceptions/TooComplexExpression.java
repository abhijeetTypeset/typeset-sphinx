package typeset.io.exceptions;

/**
 * The Class TooComplexExpression.
 */
public class TooComplexExpression extends RuntimeException {
	
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
