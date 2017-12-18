package typeset.io.exceptions;

/**
 * The Class InconsistentGraphException.
 */
public class InconsistentGraphException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4488498094809092833L;

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
		super(s);
	}
}
