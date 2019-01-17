package it.unimi.di.se.monitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import jmarkov.basic.Actions;
import jmarkov.basic.DecisionRule;
import jmarkov.basic.exceptions.SolverException;
import jmarkov.jmdp.CharAction;
import jmarkov.jmdp.IntegerState;
import jmarkov.jmdp.SimpleMDP;
import jmarkov.jmdp.solvers.ProbabilitySolver;

public class DecisionMaker {
	
	enum Policy {
		UNCERTAINTY_FLAT,
		UNCERTAINTY_HISTORY,
		UNCERTAINTY_LOCAL_HISTORY,
		RANDOM,
		HISTORY
	}
	
	private SimpleMDP mdp = null;
	private Policy policy = null;
	private Map<StateAction, Integer> count = null;
	private Map<IntegerState, ArrayList<CharAction>> mixedPolicy = new HashMap<>();	
	private Map<Integer, DecisionRule<IntegerState, CharAction>> decisionRules = new HashMap<>();
	
	public DecisionMaker(SimpleMDP mdp, Policy policy) {
		this.mdp = mdp;
		this.policy = policy;
		if(policy == Policy.UNCERTAINTY_FLAT || policy == Policy.UNCERTAINTY_HISTORY || policy == Policy.UNCERTAINTY_LOCAL_HISTORY) {
			// mixed strategy --> for each uncertain state compute best policy and then combine
			for(Integer s: this.mdp.getUncertainStates()) {
				this.mdp.clearRewards();
				this.mdp.setHighReward(s);		
				this.mdp.resetSolver();
				this.mdp.printSolution();
				DecisionRule<IntegerState, CharAction> decisionRule = null;
				try {
					decisionRule = this.mdp.getOptimalPolicy().getDecisionRule();
				} catch (SolverException e) {
					e.printStackTrace();
				}
				ProbabilitySolver<IntegerState, CharAction> solver = new ProbabilitySolver<>(this.mdp, decisionRule);
				solver.solve();
				decisionRules.put(s, decisionRule);
				for(Entry<IntegerState, CharAction> e: decisionRule) {
					ArrayList<CharAction> actions = mixedPolicy.get(e.getKey());
					if(actions != null) {
						if(!actions.contains(e.getValue()))
							actions.add(e.getValue());
					}
					else {
						actions = new ArrayList<CharAction>();
						actions.add(e.getValue());
						mixedPolicy.put(e.getKey(), actions);
					}					
				}
			}				
			
		}
		count = new HashMap<>();
		for(IntegerState s: this.mdp.getAllStates())
			for(CharAction a: this.mdp.feasibleActions(s))
				count.put(new StateAction(s.getId(), a), 1);
	}

	public CharAction getAction(int stateIndex) {
		if(policy == Policy.UNCERTAINTY_FLAT) {
			// uniform probability based choice
			List<CharAction> acts = mixedPolicy.get(new IntegerState(stateIndex));
			int rand = new Random().nextInt(acts.size());
//			if(stateIndex == 0)
//				rand = 1 - rand;
			CharAction a = acts.get(rand);
			StateAction k = new StateAction(stateIndex, a);
			count.put(k, count.get(k) + 1);
			return a;
		}
		if(policy == Policy.UNCERTAINTY_HISTORY) {
			// stochastic choice depending on the history (uncertain state hits)
			List<CharAction> actions = mixedPolicy.get(new IntegerState(stateIndex));
			return weightedRandomChoice(stateIndex, actions);
		}
		if(policy == Policy.UNCERTAINTY_LOCAL_HISTORY) {
			// stochastic choice depending on the history (local choices)
			List<CharAction> actions = mixedPolicy.get(new IntegerState(stateIndex));
			CharAction a = weightedRandomChoice(stateIndex, actions);
			StateAction k = new StateAction(stateIndex, a);
			count.put(k, count.get(k) + 1);
			return a;
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
			Actions<CharAction> actions = mdp.feasibleActions(new IntegerState(stateIndex));
			CharAction a = weightedRandomChoice(stateIndex, actions);
			StateAction k = new StateAction(stateIndex, a);
			count.put(k, count.get(k) + 1);
			return a;
		}
		return null;
	}
	
	private CharAction weightedRandomChoice(final int stateIndex, Iterable<CharAction> actions) {
		List<ActionWeight> weightedActions = new ArrayList<>();
		int countSum = 0;
		for(CharAction a: actions) {
			int c = count.get(new StateAction(stateIndex, a));
			countSum += c;
			weightedActions.add(new ActionWeight(a, c));
		}
		if(weightedActions.size() == 1)
			return weightedActions.get(0).action;
		for(ActionWeight aw: weightedActions)
			aw.weight = 1 - (aw.weight/countSum);
		Collections.sort(weightedActions, new ActionWeightComparator());
		double cumulativeProb = 0.0;
		double rand = Math.random();
		for(ActionWeight aw: weightedActions) {
			cumulativeProb += aw.weight;
			if(rand <= cumulativeProb)
				return aw.action;
		}
		return null;
	}
	
	public void updateCount(final int stateIndex) {
		if(policy == Policy.UNCERTAINTY_HISTORY && decisionRules.get(stateIndex) != null) {
			for(Entry<IntegerState, CharAction> e: decisionRules.get(stateIndex)) {
				StateAction k = new StateAction(e.getKey().getId(), e.getValue());
				count.put(k, count.get(k) + 1);
			}
		}
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
