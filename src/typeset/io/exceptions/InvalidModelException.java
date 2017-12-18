package typeset.io.exceptions;

/**
 * The Class InvalidModelException.
 */
public class InvalidModelException extends RuntimeException{
	 
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
         super(s);
     }
}
