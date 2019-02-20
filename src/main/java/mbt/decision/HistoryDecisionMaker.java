package mbt.decision;

import jmarkov.basic.Actions;
import jmarkov.jmdp.CharAction;
import jmarkov.jmdp.IntegerState;
import jmarkov.jmdp.SimpleMDP;
import mbt.monitor.StateAction;

public class HistoryDecisionMaker extends DecisionMaker {

	public HistoryDecisionMaker(SimpleMDP mdp) {
		super(mdp);
	}

	@Override
	public CharAction getAction(int stateIndex) {
		Actions<CharAction> actions = mdp.feasibleActions(new IntegerState(stateIndex));
		CharAction a = weightedRandomChoice(stateIndex, actions);
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
