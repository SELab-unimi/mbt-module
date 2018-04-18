package it.unimi.di.se.monitor;

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
	private DecisionRule<IntegerState, CharAction> decisionRule;
	
	public DecisionMaker(SimpleMDP mdp, Policy policy) {
		this.mdp = mdp;
		this.policy = policy;
		if(policy == Policy.UNCERTAINTY) {
			this.mdp.printSolution();
			try {
				decisionRule = this.mdp.getOptimalPolicy().getDecisionRule();
			} catch (SolverException e) {
				e.printStackTrace();
			}
			ProbabilitySolver<IntegerState, CharAction> solver = new ProbabilitySolver<>(this.mdp, decisionRule);
			solver.solve();
		}
	}

	public CharAction getAction(int stateIndex) {
		if(policy == Policy.UNCERTAINTY) {
			return decisionRule.getAction(new IntegerState(stateIndex));
		}
		return null;
	}
}
