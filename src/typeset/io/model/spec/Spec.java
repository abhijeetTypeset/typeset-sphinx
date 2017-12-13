package typeset.io.model.spec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Spec {
	private State given;
	private String wait;
	private Map<String, Action> when ;
	private State then;
	private String name;
	
	public State getGiven() {
		return given;
	}

	public void setGiven(State given) {
		this.given = given;
	}

	public String getWait() {
		return wait;
	}

	public void setWait(String wait) {
		this.wait = wait;
	}

	public State getThen() {
		return then;
	}

	public void setThen(State then) {
		this.then = then;
	}

	
	@Override
	public String toString() {
		String spec = "";
		spec += "\nName : " + name + "\n";
		spec += "\n\tGiven "+given+"\n";
		spec += "\tWhen "+when +"\n";
		spec += "\tWait "+wait+"\n";
		spec += "\tThen "+then+"\n";
		return spec;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Action> getWhen() {
		return when;
	}

	public void setWhen(Map<String, Action> when) {
		this.when = when;
	}
}