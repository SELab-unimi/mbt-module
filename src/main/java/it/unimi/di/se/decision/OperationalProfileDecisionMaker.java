package it.unimi.di.se.decision;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jmarkov.jmdp.StringAction;
import jmarkov.basic.DecisionRule;
import jmarkov.jmdp.IntegerState;
import jmarkov.jmdp.SimpleMDP;

public class OperationalProfileDecisionMaker extends DecisionMaker {

	public OperationalProfileDecisionMaker(SimpleMDP mdp) {
		super(mdp);
	}

	public OperationalProfileDecisionMaker(SimpleMDP mdp, Map<Integer, DecisionRule<IntegerState, StringAction>> decisionRules) {
		super(mdp, decisionRules);
	}

	@Override
	public StringAction getAction(int stateIndex) {
		List<StringAction> actions = mixedPolicy.get(new IntegerState(stateIndex));

		List<ActionWeight> weightedActions = new ArrayList<>();
		double distanceSum = 0.0d;
		for(StringAction a: actions) {
			double distance = operationalProfile.get(new IntegerState(getPolicyObjective(stateIndex, a)));
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
		// TODO Auto-generated method stub

	}

	@Override
	public void updateDistance(int stateIndex, double distance) {
		// TODO Auto-generated method stub

	}

}
