package it.unimi.di.se.decision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import it.unimi.di.se.monitor.StateAction;
import jmarkov.basic.DecisionRule;
import jmarkov.basic.exceptions.SolverException;
import jmarkov.jmdp.StringAction;
import jmarkov.jmdp.IntegerState;
import jmarkov.jmdp.SimpleMDP;
import jmarkov.jmdp.solvers.ProbabilitySolver;

public abstract class DecisionMaker {
	
	protected SimpleMDP mdp = null;
	protected Map<StateAction, Integer> count = null;
	protected Map<IntegerState, ArrayList<StringAction>> mixedPolicy = new HashMap<>();
	protected Map<Integer, DecisionRule<IntegerState, StringAction>> decisionRules = new HashMap<>();
	protected Map<IntegerState, Double> hpdDistance = new HashMap<>();
	protected Map<IntegerState, Double> operationalProfile = new HashMap<>();
	
	public DecisionMaker(SimpleMDP mdp) {
		this(mdp, null);
	}
	
	public DecisionMaker(SimpleMDP mdp, Map<Integer, DecisionRule<IntegerState, StringAction>> decisions) {
		this.mdp = mdp;
		if (decisions != null) {
			// user provided policy
			decisionRules = decisions;
			for (Integer s: decisionRules.keySet()) {
				for(Entry<IntegerState, StringAction> e: decisionRules.get(s)) {
					ArrayList<StringAction> actions = mixedPolicy.get(e.getKey());
					if(actions != null) {
						if(!actions.contains(e.getValue()))
							actions.add(e.getValue());
					}
					else {
						actions = new ArrayList<StringAction>();
						actions.add(e.getValue());
						mixedPolicy.put(e.getKey(), actions);
					}					
				}
			}
		}
		else {
			// compute mixed policy --> for each uncertain state compute best policy and then combine
			Map<Integer, String> uncertainRegions = this.mdp.getUncertainRegions();
			for(Integer s: uncertainRegions.keySet()) {
				this.mdp.clearRewards();
				this.mdp.setHighReward(s, uncertainRegions.get(s));
				this.mdp.resetSolver();
				this.mdp.printSolution();
				DecisionRule<IntegerState, StringAction> decisionRule = null;
				try {
					decisionRule = this.mdp.getOptimalPolicy().getDecisionRule();
				} catch (SolverException e) {
					e.printStackTrace();
				}
				ProbabilitySolver<IntegerState, StringAction> solver = new ProbabilitySolver<>(this.mdp, decisionRule);
				solver.solve();
				decisionRules.put(s, decisionRule);
				for(Entry<IntegerState, StringAction> e: decisionRule) {
					ArrayList<StringAction> actions = mixedPolicy.get(e.getKey());
					if(actions != null) {
						if(!actions.contains(e.getValue()))
							actions.add(e.getValue());
					}
					else {
						actions = new ArrayList<StringAction>();
						actions.add(e.getValue());
						mixedPolicy.put(e.getKey(), actions);
					}					
				}
			}	
		}		
		count = new HashMap<>();
		for(IntegerState s: this.mdp.getAllStates())
			for(StringAction a: this.mdp.feasibleActions(s))
				count.put(new StateAction(s.getId(), a), 1);
	}
	
	public void setOperationalProfile(final int stateIndex, final double profile) {
		operationalProfile.put(new IntegerState(stateIndex), profile);
	}
	
	protected StringAction weightedRandomChoice(final int stateIndex, Iterable<StringAction> actions) {
		List<ActionWeight> weightedActions = new ArrayList<>();
		int countSum = 0;
		for(StringAction a: actions) {
			int c = count.get(new StateAction(stateIndex, a));
			countSum += c;
			weightedActions.add(new ActionWeight(a, c));
		}
		if(weightedActions.size() == 1)
			return weightedActions.get(0).action;
		double totalWeight = 0.0d;
		for(ActionWeight aw: weightedActions) {
			aw.weight = countSum / aw.weight;
			totalWeight += aw.weight;
		}
		double rand = Math.random() * totalWeight;
		for(ActionWeight aw: weightedActions) {
			rand -= aw.weight;
			if(rand <= 0.0d)
				return aw.action;
		}
		return null;
	}
	
	protected int getPolicyObjective(final int stateIndex, final StringAction action) {
		for(Integer objective: decisionRules.keySet()) {
			for(Entry<IntegerState, StringAction> e: decisionRules.get(objective)) {
				if(e.getKey().getId() == stateIndex && e.getValue().equals(action))
					return objective;
			}
		}
		return -1;
	}
	
	public abstract StringAction getAction(final int stateIndex);
	public abstract void updateCount(final int stateIndex);
	public abstract void updateDistance(final int stateIndex, final double distance);
	
	class ActionWeight {
		StringAction action = null;
		double weight = 0;
		
		ActionWeight(StringAction action, double weight) {
			this.action = action;
			this.weight = weight;
		}
	}
}
