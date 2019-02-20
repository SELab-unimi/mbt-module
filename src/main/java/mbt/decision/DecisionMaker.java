package mbt.decision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jmarkov.basic.DecisionRule;
import jmarkov.basic.exceptions.SolverException;
import jmarkov.jmdp.CharAction;
import jmarkov.jmdp.IntegerState;
import jmarkov.jmdp.SimpleMDP;
import jmarkov.jmdp.solvers.ProbabilitySolver;
import mbt.monitor.StateAction;

public abstract class DecisionMaker {
	
	protected SimpleMDP mdp = null;
	protected Map<StateAction, Integer> count = null;
	protected Map<IntegerState, ArrayList<CharAction>> mixedPolicy = new HashMap<>();
	protected Map<Integer, DecisionRule<IntegerState, CharAction>> decisionRules = new HashMap<>();
	protected Map<IntegerState, Double> hpdDistance = new HashMap<>();
	protected Map<IntegerState, Double> operationalProfile = new HashMap<>();
	
	public DecisionMaker(SimpleMDP mdp) {
		this.mdp = mdp;
		// mixed policy --> for each uncertain state compute best policy and then combine
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
		count = new HashMap<>();
		for(IntegerState s: this.mdp.getAllStates())
			for(CharAction a: this.mdp.feasibleActions(s))
				count.put(new StateAction(s.getId(), a), 1);
	}
	
	public void setOperationalProfile(final int stateIndex, final double profile) {
		operationalProfile.put(new IntegerState(stateIndex), profile);
	}
	
	protected CharAction weightedRandomChoice(final int stateIndex, Iterable<CharAction> actions) {
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
	
	protected int getPolicyObjective(final int stateIndex, final CharAction action) {
		for(Integer objective: decisionRules.keySet()) {
			for(Entry<IntegerState, CharAction> e: decisionRules.get(objective)) {
				if(e.getKey().getId() == stateIndex && e.getValue().equals(action))
					return objective;
			}
		}
		return -1;
	}
	
	public abstract CharAction getAction(final int stateIndex);
	public abstract void updateCount(final int stateIndex);
	public abstract void updateDistance(final int stateIndex, final double distance);
	
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
