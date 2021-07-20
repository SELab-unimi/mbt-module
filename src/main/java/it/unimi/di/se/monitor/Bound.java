package it.unimi.di.se.monitor;

public class Bound {

    private double lbound = 0.0d;
    private double ubound = 0.0d;

    public Bound(Double lbound, Double ubound) {
        this.lbound = lbound;
        this.ubound = ubound;
    }

    public boolean contains(Bound target) {
        return this.lbound <= target.lbound && this.ubound >= target.ubound;
    }

    public boolean disjoint(Bound target) {
        return this.ubound < target.lbound || this.lbound > target.ubound;
    }
}
