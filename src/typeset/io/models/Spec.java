package typeset.io.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Spec {
	private State given;
	private String wait;
	private Action when ;
	private State then;

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

	public Action getWhen() {
		return when;
	}

	public void setWhen(Action when) {
		this.when = when;
	}
	
	@Override
	public String toString() {
		String spec = "";
		spec += "\n\tGiven "+given+"\n";
		spec += "\tWhen "+when +"\n";
		spec += "\tWait "+wait+"\n";
		spec += "\tThen "+then+"\n";
		return spec;
	}
}
