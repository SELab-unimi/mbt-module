package it.unimi.di.se.decision;

import it.unimi.di.se.monitor.EventHandler;
import jmarkov.jmdp.CharAction;
import jmarkov.jmdp.SimpleMDP;

public class CombinedDecisionMaker extends DecisionMaker {
	
	private DecisionMaker distanceDecisionMaker = null;
	private DecisionMaker operationalDecisionMaker = null;

	public CombinedDecisionMaker(SimpleMDP mdp) {
		super(mdp);
		distanceDecisionMaker = new DistanceDecisionMaker(mdp);
		operationalDecisionMaker = new OperationalProfileDecisionMaker(mdp);
	}

	@Override
	public CharAction getAction(int stateIndex) {
		ActionWeight[] actions = new ActionWeight[2];
		actions[0] = new ActionWeight(distanceDecisionMaker.getAction(stateIndex), EventHandler.DIST_WEIGHT);
		actions[1] = new ActionWeight(operationalDecisionMaker.getAction(stateIndex), EventHandler.PROF_WEIGHT);
		double weightSum = EventHandler.DIST_WEIGHT + EventHandler.PROF_WEIGHT;
		double rand = Math.random() * weightSum;
		for(int i=0; i<actions.length; i++) {
			rand -= actions[i].weight;
			if(rand <= 0.0d)
				return actions[i].action;
		}
		return null;
	}
	
	@Override
	public void setOperationalProfile(final int stateIndex, final double profile) {
		operationalDecisionMaker.setOperationalProfile(stateIndex, profile);
	}

	@Override
	public void updateDistance(int stateIndex, double distance) {
		distanceDecisionMaker.updateDistance(stateIndex, distance);
	}
	
	@Override
	public void updateCount(int stateIndex) {
		// nothing to count here
	}

}
