package it.unimi.di.se.monitor;

import it.unimi.di.se.mdp.mdpDsl.Action;

public class Dirichlet {
	
	private Action action;
	// hyper parameters
	private Double[] alpha;
	
	public Dirichlet(int length, Action action){
		this.action = action;
		alpha = new Double[length];
		for(int i=0; i<length; i++)
			alpha[i] = 0.0;
	}
	
	public void set(int i, double hyperParameter){
		alpha[i] = hyperParameter;
	}
	
	public void update(int i){
		alpha[i]++;
	}
}
