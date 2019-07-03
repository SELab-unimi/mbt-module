package it.unimi.di.se.decision;

import java.util.ArrayList;
import java.util.List;

import jmarkov.jmdp.StringAction;
import jmarkov.jmdp.IntegerState;
import jmarkov.jmdp.SimpleMDP;

public class DistanceDecisionMaker extends DecisionMaker {

	public DistanceDecisionMaker(SimpleMDP mdp) {
		super(mdp);
	}

	@Override
	public StringAction getAction(int stateIndex) {
		// stochastic choice depending on the hpd region size
		List<StringAction> actions = mixedPolicy.get(new IntegerState(stateIndex));
		
		List<ActionWeight> weightedActions = new ArrayList<>();
		double distanceSum = 0.0d;
		for(StringAction a: actions) {
			double distance = hpdDistance.get(new IntegerState(getPolicyObjective(stateIndex, a)));
			distanceSum += distance;
			weightedActions.add(new ActionWeight(a, distance));
		}
		if(weightedActions.size() == 1)
			return weightedActions.get(0).action;
		double rand = Math.random() * distanceSum;
		for(ActionWeight aw: weightedActions) {
			rand -= aw.weight;
			if(rand <= 0.0d)
				return aw.action;
		}
		return null;
	}

	@Override
	public void updateCount(int stateIndex) {
		// nothing to count here
	}

	@Override
	public void updateDistance(int stateIndex, double distance) {
		hpdDistance.put(new IntegerState(stateIndex), distance);
	}

}
