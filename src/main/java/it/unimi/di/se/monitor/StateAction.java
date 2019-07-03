package it.unimi.di.se.monitor;

import java.util.Objects;
import jmarkov.jmdp.StringAction;

public class StateAction {
	Integer state = 0;
	StringAction action = null;
	
	public StateAction(int state, StringAction action) {
		this.state = state;
		this.action = action;
	}
	
	public boolean equals(Object o) {
		StateAction target = (StateAction)o;
		return this.state == target.state && this.action.equals(target.action);
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
