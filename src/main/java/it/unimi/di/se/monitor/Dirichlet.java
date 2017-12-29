package it.unimi.di.se.monitor;

import java.util.Arrays;

import it.unimi.di.se.mdp.mdpDsl.Action;

public class Dirichlet {
	
	private Action action;
	// hyper parameters
	private Double[] alpha;
	private double sum = 0;
	
	public Dirichlet(int length, Action action){
		this.action = action;
		alpha = new Double[length];
		for(int i=0; i<length; i++)
			alpha[i] = 0.0;
	}
	
	public String action() {
		return action.getName();
	}
	
	public void set(int i, double hyperParameter){
		alpha[i] = hyperParameter;
	}
	
	public void update(int i){
		alpha[i]++;
	}
	
	public Double[] mode() {
		Double[] result = new Double[alpha.length];
		for(int i=0; i<alpha.length; i++) {
			if(alpha[i] > 1)
				result[i] = (alpha[i] - 1) / (sum() - alpha.length);
			else
				result[i] = 0.0;
		}
		return result;
	}
	
	public Double[] mean() {
		Double[] result = new Double[alpha.length];
		for(int i=0; i<alpha.length; i++)
			result[i] = alpha[i] / sum();
		return result;
	}

	private double sum() {
		if(sum != 0)
			return sum;
		for(int i=0; i<alpha.length; i++)
			sum += alpha[i];
		return sum;
	}
	
	public Double[] params() {
		return alpha;
	}
	
	public String hpdiRCommand(double credMass) {
		StringBuilder rCommand = new StringBuilder("hdi(rdirichlet(100000, ");
		rCommand.append(Arrays.toString(alpha).replace("[", "c(").replace("]", ")"));
		rCommand.append(" ), credMass=").append(credMass).append(")");
		return rCommand.toString();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("[ ");
		for(Double a: alpha)
			builder.append(a).append(", ");
		return builder.append(" ]").toString();
	}
	
	
}
