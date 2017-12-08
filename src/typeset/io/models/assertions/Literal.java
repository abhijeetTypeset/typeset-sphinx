package typeset.io.models.assertions;

public class Literal {

	private String symbol;
	private String action;
	private boolean isNegation;

	public Literal(String symbol, String action, boolean isNegation) {
		super();
		this.symbol = symbol;
		this.action = action;
		this.isNegation = isNegation;
	}

	public String getSymbol() {
		return symbol;
	}

	public String getAction() {
		return action;
	}

	public boolean isNegation() {
		return isNegation;
	}

	@Override
	public String toString() {

		String lit = action + "(" + symbol + ")";
		if (isNegation) {
			lit = "~" + lit;
		}
		return lit;
	}

}
