package it.unimi.di.se.decision;

import jmarkov.basic.Actions;
import jmarkov.jmdp.StringAction;
import jmarkov.jmdp.IntegerState;
import jmarkov.jmdp.SimpleMDP;

public class RandomDecisionMaker extends DecisionMaker {

	public RandomDecisionMaker(SimpleMDP mdp) {
		super(mdp);
	}

	@Override
	public StringAction getAction(int stateIndex) {
		Actions<StringAction> actions = mdp.feasibleActions(new IntegerState(stateIndex));
		int randomIndex = (int)(Math.random() * actions.size());
		int i=0;
		for(StringAction a: actions)
			if(i++==randomIndex)
				return a;
		return null;
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
