package it.unimi.di.se.sut;

import jmarkov.jmdp.IntegerState;

import java.util.Comparator;

public class IntegerStateComparator implements Comparator<IntegerState> {
    @Override
    public int compare(IntegerState state1, IntegerState state2) {
        return state1.label().compareTo(state2.label());
    }
}
