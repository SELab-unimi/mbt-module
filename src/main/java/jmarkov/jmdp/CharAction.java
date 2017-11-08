package jmarkov.jmdp;

import jmarkov.basic.Action;

public class CharAction extends Action {

    private Character action = null;

    public CharAction(char action) {
        this.action = action;
    }

    @Override
    public String label() {
        return "action := " + action;
    }

    public char actionLabel() {
        return action;
    }

    @Override
    public int compareTo(Action a) {
        if (a instanceof CharAction)
            return action.compareTo(((CharAction) a).action);
        else
            throw new IllegalArgumentException("Comparing with different type of Action.");
    }
}