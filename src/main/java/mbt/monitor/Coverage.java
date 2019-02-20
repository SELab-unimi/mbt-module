package mbt.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unimi.di.se.mdp.mdpDsl.Arc;
import it.unimi.di.se.mdp.mdpDsl.MDPModel;
import it.unimi.di.se.mdp.mdpDsl.State;
import jmarkov.jmdp.CharAction;

public class Coverage {
	
	private Map<StateAction, Integer> execution = new HashMap<>();

	public Coverage(MDPModel model) {
		int i=0;
		for(State s: model.getStates()) {
			if(!isFinal(s, model)) {
				for(Arc a: model.getArcs())
					if(a.getSrc().equals(s))
						execution.put(new StateAction(i, new CharAction(a.getAct().getName().charAt(0))), 0);
			}
			i++;
		}
	}
	
	private boolean isFinal(State s, MDPModel model) {
		List<Arc> outgoingArcs = new ArrayList<>();
		for(Arc a: model.getArcs())
			if(a.getSrc().equals(s))
				outgoingArcs.add(a);
		if(outgoingArcs.size() == 1 && outgoingArcs.get(0).getDst().equals(s))
			return true;
		return false;
	}
	
	public void addExecution(int state, CharAction action) {
		StateAction key = new StateAction(state, action);
		Integer i = execution.get(key);
		if(i != null)
			execution.put(key, i+1);
	}
	
	public int getExecutedPairs() {
		int executedPairs = 0;
		for(StateAction k: execution.keySet())
			if(execution.get(k) > 0)
				executedPairs++;
		return executedPairs;
	}
	
	public double getCoverage() {
		return (double)getExecutedPairs()/(double)execution.size();
	}
	
	public String info() {
		String info = "";
		for(StateAction k: execution.keySet())
			info += k.toString() + " = " + execution.get(k) + "; ";
		return info;
	}
	
	public String toString() {
		return "StateAction-pairs: " + execution.size() + ", Executed-pairs: " + getExecutedPairs() + ", Coverage: " + getCoverage();
	}

}
