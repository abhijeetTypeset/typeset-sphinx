package io.typeset.sphinx.model.assertions;

import java.util.ArrayList;
import java.util.List;


/**
 * The Class ExplicitAssertion.
 */
public class ExplicitAssertion {
	
	/** The clauses. */
	private List<Clause> clauses;

	/**
	 * Instantiates a new explicit assertion.
	 */
	public ExplicitAssertion() {
		clauses = new ArrayList<>();
	}

	/**
	 * Addclauses.
	 *
	 * @param clause the clause
	 */
	public void addclauses(Clause clause) {
		clauses.add(clause);
	}

	/**
	 * Gets the clauses.
	 *
	 * @return the clauses
	 */
	public List<Clause> getclauses() {
		return clauses;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String ea = "";
		int size = clauses.size();
		int count = 0;
		for (Clause clause : clauses) {
			ea += clause;
			count++;
			if (count < size) {
				ea += " AND ";
			}
		}
		ea = "( " + ea + " )";
		return ea;
	}

}
