package typeset.io.model.assertions;

import java.util.ArrayList;
import java.util.List;

public class ExplicitAssertion {
	private List<Clause> clauses;

	public ExplicitAssertion() {
		clauses = new ArrayList<>();
	}

	public void addclauses(Clause clause) {
		clauses.add(clause);
	}

	public List<Clause> getclauses() {
		return clauses;
	}

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
