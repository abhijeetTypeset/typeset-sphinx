package typeset.io.exceptions;

public class TooComplexExpression extends RuntimeException {
	public TooComplexExpression() {
		super();
	}

	public TooComplexExpression(String s) {
		super(s);
	}
}
