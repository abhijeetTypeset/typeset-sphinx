package typeset.io.exceptions;

/**
 * The Class InvalidLiteralException.
 */
public class InvalidLiteralException extends RuntimeException {
	 
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
         super(s);
     }
}
