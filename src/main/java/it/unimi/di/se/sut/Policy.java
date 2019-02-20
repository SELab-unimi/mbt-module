package it.unimi.di.se.sut;

import jmarkov.jmdp.CharAction;
import jmarkov.jmdp.IntegerState;
import jmarkov.jmdp.solvers.ProbabilitySolver;

import java.util.HashMap;
import java.util.Map;

public class Policy {

    private Map<IntegerState, CharAction> actionMap = new HashMap<>();

    public Policy(ProbabilitySolver<IntegerState, CharAction> solver) {
        solver.solve();
        // TODO extract mapping
    }
}
