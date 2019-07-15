package it.unimi.di.se.decision;

import java.util.Map;

import it.unimi.di.se.monitor.StateAction;
import jmarkov.basic.Actions;
import jmarkov.basic.DecisionRule;
import jmarkov.jmdp.StringAction;
import jmarkov.jmdp.IntegerState;
import jmarkov.jmdp.SimpleMDP;

public class HistoryDecisionMaker extends DecisionMaker {

	public HistoryDecisionMaker(SimpleMDP mdp) {
		super(mdp);
	}

	public HistoryDecisionMaker(SimpleMDP mdp, Map<Integer, DecisionRule<IntegerState, StringAction>> decisionRules) {
		super(mdp, decisionRules);
	}

	@Override
	public StringAction getAction(int stateIndex) {
		Actions<StringAction> actions = mdp.feasibleActions(new IntegerState(stateIndex));
		StringAction a = weightedRandomChoice(stateIndex, actions);
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
