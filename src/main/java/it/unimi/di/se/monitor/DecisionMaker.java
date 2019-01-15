package it.unimi.di.se.monitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jmarkov.basic.Actions;
import jmarkov.basic.DecisionRule;
import jmarkov.basic.exceptions.SolverException;
import jmarkov.jmdp.CharAction;
import jmarkov.jmdp.IntegerState;
import jmarkov.jmdp.SimpleMDP;
import jmarkov.jmdp.solvers.ProbabilitySolver;

public class DecisionMaker {
	
	enum Policy {
		UNCERTAINTY,
		RANDOM,
		HISTORY
	}
	
	private SimpleMDP mdp = null;
	private Policy policy = null;
	private DecisionRule<IntegerState, CharAction> decisionRule = null;
	private Map<StateAction, Integer> count = null;
	
	public DecisionMaker(SimpleMDP mdp, Policy policy) {
		this.mdp = mdp;
		this.policy = policy;
		if(policy == Policy.UNCERTAINTY) {
			// TODO mixed strategy --> for each uncertain state compute best policy and then combine
			for(Integer s: this.mdp.getUncertainStates()) {
				this.mdp.setHighReward(s);
				break;
			}				
			this.mdp.printSolution();
			try {
				decisionRule = this.mdp.getOptimalPolicy().getDecisionRule();
			} catch (SolverException e) {
				e.printStackTrace();
			}
			ProbabilitySolver<IntegerState, CharAction> solver = new ProbabilitySolver<>(this.mdp, decisionRule);
			solver.solve();
		}
		if(policy == Policy.HISTORY) {
			count = new HashMap<>();
			for(IntegerState s: this.mdp.getAllStates())
				for(CharAction a: this.mdp.feasibleActions(s))
					count.put(new StateAction(s.getId(), a), 1);
		}
	}

	public CharAction getAction(int stateIndex) {
		if(policy == Policy.UNCERTAINTY) {
			return decisionRule.getAction(new IntegerState(stateIndex));
		}
		if(policy == Policy.RANDOM) {
			Actions<CharAction> actions = mdp.feasibleActions(new IntegerState(stateIndex));
			int randomIndex = (int)(Math.random() * actions.size());
			int i=0;
			for(CharAction a: actions)
				if(i++==randomIndex)
					return a;
		}
		if(policy == Policy.HISTORY) {
			IntegerState currentState = new IntegerState(stateIndex);
			Actions<CharAction> actions = mdp.feasibleActions(currentState);
			List<ActionWeight> weightedActions = new ArrayList<>();
			int countSum = 0;
			for(CharAction a: actions) {
				int c = count.get(new StateAction(stateIndex, a));
				countSum += c;
				weightedActions.add(new ActionWeight(a, c));
			}
			if(weightedActions.size() == 1) {
				CharAction a = weightedActions.get(0).action;
				StateAction k = new StateAction(stateIndex, a);
				count.put(k, count.get(k) + 1);
				return a;
			}
			double frequencySum = 0.0;
			for(ActionWeight aw: weightedActions) {
				aw.weight = 1 - (aw.weight/countSum);
				frequencySum += aw.weight;
			}
			Collections.sort(weightedActions, new ActionWeightComparator());
			double cumulativeProb = 0.0;
			double rand = Math.random();
			for(ActionWeight aw: weightedActions) {
				cumulativeProb += (aw.weight/frequencySum);
				if(rand <= cumulativeProb) {
					StateAction k = new StateAction(stateIndex, aw.action);
					count.put(k, count.get(k) + 1);
					return aw.action;
				}
			}
		}
		return null;
	}
	
	class ActionWeight {
		CharAction action = null;
		double weight = 0;
		
		ActionWeight(CharAction action, double weight) {
			this.action = action;
			this.weight = weight;
		}
	}
	
	class ActionWeightComparator implements Comparator<ActionWeight> {
		@Override
		public int compare(ActionWeight s, ActionWeight t) {
			if(s.weight > t.weight)
				return 1;
			else if(s.weight < t.weight)
				return -1;
			return 0;
		}
	}
}
