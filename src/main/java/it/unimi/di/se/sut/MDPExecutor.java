package it.unimi.di.se.sut;

import jmarkov.basic.Actions;
import jmarkov.basic.States;
import jmarkov.jmdp.StringAction;
import jmarkov.jmdp.IntegerState;
import jmarkov.jmdp.SimpleMDP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.util.*;

public class MDPExecutor {

    private static Logger logger = LoggerFactory.getLogger(MDPExecutor.class);

    private SimpleMDP mdp = null;
    private IntegerState currentState = null;
    private Comparator<IntegerState> stateComparator = new IntegerStateComparator();

    public MDPExecutor(Reader input) {
        mdp = new SimpleMDP(input);
        resetExecution();
    }

    public IntegerState getCurrentState() {
        return currentState;
    }
    
    public void setCurrentState(IntegerState state) {
    		currentState = state;
    }

    public List<IntegerState> reachableStates(String action) {
        return orderedReachableStates(mdp.reachable(currentState, new StringAction(action)));
    }

    public IntegerState doAction(IntegerState sourceState, String action) {
        StringAction a = new StringAction(action);
        List<IntegerState> orderedStates = orderedReachableStates(mdp.reachable(currentState, a));
        double uRand = new Random().nextDouble();
        logger.trace("U[0,1] = " + uRand);
        double cumulativeProb = 0;
        IntegerState lastState = null;
        for(IntegerState c: orderedStates) {
            cumulativeProb += mdp.prob(currentState, c, a);
            logger.trace("state = " + c.label() + ", cumulative prob = " + cumulativeProb);
            if(uRand <= cumulativeProb && lastState == null)
            		lastState = c;
        }
        return lastState;
    }

    private List<IntegerState> orderedReachableStates(States<IntegerState> reachableSet) {
        Iterator<IntegerState> iterator = reachableSet.iterator();
        List<IntegerState> orderedStates = new ArrayList<>();
        while (iterator.hasNext())
            orderedStates.add(iterator.next());
        Collections.sort(orderedStates, stateComparator);
        return orderedStates;
    }

    public boolean isCurrentStateAbsorbing() {
        return mdp.isAbsorbing(currentState);
    }

    public Actions<StringAction> getFeasibleActions() {
        return mdp.feasibleActions(currentState);
    }

    public void resetExecution() {
        currentState = mdp.getInitialState();
    }
}
