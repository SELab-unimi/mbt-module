package it.unimi.di.se.decision;

import java.util.List;
import java.util.Map;

import it.unimi.di.se.monitor.StateAction;
import jmarkov.jmdp.StringAction;
import jmarkov.basic.DecisionRule;
import jmarkov.jmdp.IntegerState;
import jmarkov.jmdp.SimpleMDP;

public class LocalHistUncertaintyDecisionMaker extends DecisionMaker {

	public LocalHistUncertaintyDecisionMaker(SimpleMDP mdp) {
		super(mdp);
	}

	public LocalHistUncertaintyDecisionMaker(SimpleMDP mdp, Map<Integer, DecisionRule<IntegerState, StringAction>> decisionRules) {
		super(mdp, decisionRules);
	}

	@Override
	public StringAction getAction(int stateIndex) {
		// stochastic choice depending on the history (local choices)
		List<StringAction> actions = mixedPolicy.get(new IntegerState(stateIndex));
		StringAction a = weightedRandomChoice(stateIndex, actions);
		StateAction k = new StateAction(stateIndex, a);
		count.put(k, count.get(k) + 1);
		return a;
	}

	@Override
	public void updateCount(int stateIndex) {
		// nothing to do here
	}

	@Override
	public void updateDistance(int stateIndex, double distance) {
		// nothing to update here
	}

}
