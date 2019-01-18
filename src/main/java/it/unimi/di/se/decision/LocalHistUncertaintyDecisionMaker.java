package it.unimi.di.se.decision;

import java.util.List;

import it.unimi.di.se.monitor.StateAction;
import jmarkov.jmdp.CharAction;
import jmarkov.jmdp.IntegerState;
import jmarkov.jmdp.SimpleMDP;

public class LocalHistUncertaintyDecisionMaker extends DecisionMaker {

	public LocalHistUncertaintyDecisionMaker(SimpleMDP mdp) {
		super(mdp);
	}

	@Override
	public CharAction getAction(int stateIndex) {
		// stochastic choice depending on the history (local choices)
		List<CharAction> actions = mixedPolicy.get(new IntegerState(stateIndex));
		CharAction a = weightedRandomChoice(stateIndex, actions);
		StateAction k = new StateAction(stateIndex, a);
		count.put(k, count.get(k) + 1);
		return a;
	}

	@Override
	public void updateCount(int stateIndex) {
		// nothing to do here
	}

}
