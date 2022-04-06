package it.unimi.di.se.monitor;

import it.unimi.di.se.mdp.mdpDsl.State;

import java.util.HashMap;
import java.util.Map;

public class HyperRectangle {

    private Map<State, Bound[]> stateVarMap = new HashMap<>();

    public void add(State s, Bound[] bounds) {
        stateVarMap.put(s, bounds);
    }

    public boolean contains(State s, double[][] region) {
        Bound[] bounds = stateVarMap.get(s);
        for (int i=0; i<bounds.length; i++) {
            if (!bounds[i].contains(new Bound(region[i][0], region[i][1])))
                return false;
        }
        return true;
    }

    public boolean disjoint(State s, double[][] region) {
        Bound[] bounds = stateVarMap.get(s);
        for (int i=0; i<bounds.length; i++) {
            if (bounds[i].disjoint(new Bound(region[i][0], region[i][1])))
                return true;
        }
        return false;
    }
}
