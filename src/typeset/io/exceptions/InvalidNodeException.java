package typeset.io.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Class InvalidNodeException.
 */
public class InvalidNodeException extends RuntimeException {
	private static final Logger logger = LogManager.getLogger("InvalidNodeException");

	/**
	 * 
	 */
	private static final long serialVersionUID = -79551173494742209L;

	/**
	 * Instantiates a new invalid node exception.
	 */
	public InvalidNodeException() {
		super();
	}

	/**
	 * Instantiates a new invalid node exception.
	 *
	 * @param s the s
	 */
	public InvalidNodeException(String s) {
		super(s);
	}
}
