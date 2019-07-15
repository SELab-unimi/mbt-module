package it.unimi.di.se.decision;

import java.util.Map;

import jmarkov.basic.DecisionRule;
import jmarkov.jmdp.IntegerState;
import jmarkov.jmdp.SimpleMDP;
import jmarkov.jmdp.StringAction;

public class DecisionMakerFactory {
	
	public DecisionMaker createPolicy(SimpleMDP mdp, Policy policy) {
		if(policy == Policy.UNCERTAINTY_FLAT)
			return new FlatDecisionMaker(mdp);
		else if(policy == Policy.UNCERTAINTY_LOCAL_HISTORY)
			return new LocalHistUncertaintyDecisionMaker(mdp);
		else if(policy == Policy.UNCERTAINTY_HISTORY)
			return new GlobalHistUncertaintyDecisionMaker(mdp);
		else if(policy == Policy.DISTANCE)
			return new DistanceDecisionMaker(mdp);
		else if(policy == Policy.PROFILE)
			return new OperationalProfileDecisionMaker(mdp);
		else if(policy == Policy.COMBINED)
			return new CombinedDecisionMaker(mdp);
		else if(policy == Policy.RANDOM)
			return new RandomDecisionMaker(mdp);
		else if(policy == Policy.HISTORY)
			return new HistoryDecisionMaker(mdp);
		return null;
	}

	public DecisionMaker createPolicy(SimpleMDP mdp, Policy policy, Map<Integer, DecisionRule<IntegerState, StringAction>> decisionRules) {
		if(policy == Policy.UNCERTAINTY_FLAT)
			return new FlatDecisionMaker(mdp, decisionRules);
		else if(policy == Policy.UNCERTAINTY_LOCAL_HISTORY)
			return new LocalHistUncertaintyDecisionMaker(mdp, decisionRules);
		else if(policy == Policy.UNCERTAINTY_HISTORY)
			return new GlobalHistUncertaintyDecisionMaker(mdp, decisionRules);
		else if(policy == Policy.DISTANCE)
			return new DistanceDecisionMaker(mdp, decisionRules);
		else if(policy == Policy.PROFILE)
			return new OperationalProfileDecisionMaker(mdp, decisionRules);
		else if(policy == Policy.COMBINED)
			return new CombinedDecisionMaker(mdp, decisionRules);
		else if(policy == Policy.RANDOM)
			return new RandomDecisionMaker(mdp, decisionRules);
		else if(policy == Policy.HISTORY)
			return new HistoryDecisionMaker(mdp, decisionRules);
		return null;
	}
}
