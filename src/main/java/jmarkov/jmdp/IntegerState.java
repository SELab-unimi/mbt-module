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
	public boolean equals(Object o) {
		if (!(o instanceof IntegerState))
            return false;
		IntegerState s1 = (IntegerState) o;
		return getId() == s1.getId();
	}
	
	@Override
	public int hashCode() {
		return new Integer(prop[0]).hashCode();
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