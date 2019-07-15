package it.unimi.di.se.decision;

import java.util.List;
import java.util.Map;
import java.util.Random;

import it.unimi.di.se.monitor.StateAction;
import jmarkov.jmdp.StringAction;
import jmarkov.basic.DecisionRule;
import jmarkov.jmdp.IntegerState;
import jmarkov.jmdp.SimpleMDP;

public class FlatDecisionMaker extends DecisionMaker {

	public FlatDecisionMaker(SimpleMDP mdp) {
		super(mdp);	
	}

	public FlatDecisionMaker(SimpleMDP mdp, Map<Integer, DecisionRule<IntegerState, StringAction>> decisionRules) {
		super(mdp, decisionRules);
	}

	@Override
	public StringAction getAction(int stateIndex) {
		List<StringAction> acts = mixedPolicy.get(new IntegerState(stateIndex));
		int rand = new Random().nextInt(acts.size());
		StringAction a = acts.get(rand);
		StateAction k = new StateAction(stateIndex, a);
		count.put(k, count.get(k) + 1);
		return a;
	}

	@Override
	public void updateCount(int stateIndex) {
		// nothing to count here	
	}

	@Override
	public void updateDistance(int stateIndex, double distance) {
		// nothing to update here		
	}
	
}
