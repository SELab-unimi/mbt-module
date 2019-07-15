package it.unimi.di.se.decision;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import it.unimi.di.se.monitor.StateAction;
import jmarkov.jmdp.StringAction;
import jmarkov.basic.DecisionRule;
import jmarkov.jmdp.IntegerState;
import jmarkov.jmdp.SimpleMDP;

public class GlobalHistUncertaintyDecisionMaker extends DecisionMaker {

	public GlobalHistUncertaintyDecisionMaker(SimpleMDP mdp) {
		super(mdp);
	}

	public GlobalHistUncertaintyDecisionMaker(SimpleMDP mdp, Map<Integer, DecisionRule<IntegerState, StringAction>> decisionRules) {
		super(mdp, decisionRules);
	}

	@Override
	public StringAction getAction(int stateIndex) {
		// stochastic choice depending on the history (uncertain state hits)
		List<StringAction> actions = mixedPolicy.get(new IntegerState(stateIndex));
		return weightedRandomChoice(stateIndex, actions);
	}

	@Override
	public void updateCount(int stateIndex) {
		if(decisionRules.get(stateIndex) != null) {
			for(Entry<IntegerState, StringAction> e: decisionRules.get(stateIndex)) {
				StateAction k = new StateAction(e.getKey().getId(), e.getValue());
				count.put(k, count.get(k) + 1);
			}
		}
	}

	@Override
	public void updateDistance(int stateIndex, double distance) {
		// nothing to update here
	}

}
