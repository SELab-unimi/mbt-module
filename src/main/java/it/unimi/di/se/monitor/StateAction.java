package it.unimi.di.se.monitor;

import java.util.Objects;
import jmarkov.jmdp.CharAction;

public class StateAction {
	Integer state = 0;
	CharAction action = null;
	
	StateAction(int state, CharAction action) {
		this.state = state;
		this.action = action;
	}
	
	public boolean equals(Object o) {
		StateAction target = (StateAction)o;
		return this.state == target.state && this.action.actionLabel() == target.action.actionLabel();
	}
	
	@Override
    public int hashCode() {
        return Objects.hash(state, action.actionLabel());
    }
	
	@Override
	public String toString() {
		return "(S" + state + ", " + action.actionLabel() + ")";
	}
}
