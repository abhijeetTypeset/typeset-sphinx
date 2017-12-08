package typeset.io.models.assertions;

import java.util.ArrayList;
import java.util.List;

public class Clause {
	
	private List<Literal> literals;
	
	public Clause() {
		literals = new ArrayList<>();
	}
	
	public void addLiterals(Literal literal) {
		literals.add(literal);
	}

	public List<Literal> getLiterals() {
		return literals;
	}
	
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
