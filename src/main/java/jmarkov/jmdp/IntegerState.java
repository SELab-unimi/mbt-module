package jmarkov.jmdp;

import jmarkov.basic.PropertiesState;


/**
 * Simple state represented by an natural number k >= 0
 */
public class IntegerState extends PropertiesState {

    public IntegerState(int k) {
        super(new int[] {k});
    }

    public int getId() {
        return prop[0];
    }

    @Override
    public String label() {
        return "S" + getId();
    }

    @Override
    public boolean isConsistent() {
        return true;
    }

}