package mbt.decision;

import java.util.List;
import java.util.Random;

import jmarkov.jmdp.CharAction;
import jmarkov.jmdp.IntegerState;
import jmarkov.jmdp.SimpleMDP;
import mbt.monitor.StateAction;

public class FlatDecisionMaker extends DecisionMaker {

	public FlatDecisionMaker(SimpleMDP mdp) {
		super(mdp);	
	}

	@Override
	public CharAction getAction(int stateIndex) {
		List<CharAction> acts = mixedPolicy.get(new IntegerState(stateIndex));
		int rand = new Random().nextInt(acts.size());
		CharAction a = acts.get(rand);
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
