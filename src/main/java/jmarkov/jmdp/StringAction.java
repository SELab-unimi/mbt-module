package jmarkov.jmdp;

import jmarkov.basic.Action;

public class StringAction extends Action {

	private String action = null;

    public StringAction(String action) {
        this.action = action;
    }

    @Override
    public String label() {
        return "action := " + action;
    }

    public String actionLabel() {
        return action;
    }

    @Override
    public int compareTo(Action a) {
        if (a instanceof StringAction)
            return action.compareTo(((StringAction) a).action);
        else
            throw new IllegalArgumentException("Comparing with different type of Action.");
    }

}
