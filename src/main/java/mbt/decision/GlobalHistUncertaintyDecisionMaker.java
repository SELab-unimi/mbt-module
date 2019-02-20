package mbt.decision;

import java.util.List;
import java.util.Map.Entry;

import jmarkov.jmdp.CharAction;
import jmarkov.jmdp.IntegerState;
import jmarkov.jmdp.SimpleMDP;
import mbt.monitor.StateAction;

public class GlobalHistUncertaintyDecisionMaker extends DecisionMaker {

	public GlobalHistUncertaintyDecisionMaker(SimpleMDP mdp) {
		super(mdp);
	}

	@Override
	public CharAction getAction(int stateIndex) {
		// stochastic choice depending on the history (uncertain state hits)
		List<CharAction> actions = mixedPolicy.get(new IntegerState(stateIndex));
		return weightedRandomChoice(stateIndex, actions);
	}

	@Override
	public void updateCount(int stateIndex) {
		if(decisionRules.get(stateIndex) != null) {
			for(Entry<IntegerState, CharAction> e: decisionRules.get(stateIndex)) {
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
