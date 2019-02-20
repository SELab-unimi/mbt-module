package mbt.decision;

import jmarkov.jmdp.SimpleMDP;

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
}
