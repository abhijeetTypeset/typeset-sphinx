package typeset.io.model.assertions;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class Clause.
 */
public class Clause {
	
	/** The literals. */
	private List<Literal> literals;
	
	/**
	 * Instantiates a new clause.
	 */
	public Clause() {
		literals = new ArrayList<>();
	}
	
	/**
	 * Adds the literals.
	 *
	 * @param literal the literal
	 */
	public void addLiterals(Literal literal) {
		literals.add(literal);
	}

	/**
	 * Gets the literals.
	 *
	 * @return the literals
	 */
	public List<Literal> getLiterals() {
		return literals;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String cls = "";
		int size = literals.size();
		int count = 0;
		for (Literal literal : literals) {
			cls += literal;
			count++;
			if (count < size) {
				cls += " OR ";
			}
		}
		cls = "( " + cls + " )";
		return cls;
	}

}
