package typeset.io.exceptions;

/**
 * The Class InvalidPathException.
 */
public class InvalidPathException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6137326468521556012L;

	/**
	 * Instantiates a new invalid path exception.
	 */
	public InvalidPathException() {
		super();
	}

	/**
	 * Instantiates a new invalid path exception.
	 *
	 * @param s the s
	 */
	public InvalidPathException(String s) {
		super(s);
	}
}
