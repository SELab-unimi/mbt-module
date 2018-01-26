package it.unimi.di.se.monitor;

import java.util.Arrays;

import it.unimi.di.se.mdp.mdpDsl.Action;

public class Dirichlet {
	
	private static final double DELTA = 0.001;
	
	private Action action;
	// hyper parameters
	private Double[] alpha;
	private double sum = 0;
	private Double[] mean;
	private boolean convergence = false;
	
	public Dirichlet(int length, Action action){
		this.action = action;
		alpha = new Double[length];
		for(int i=0; i<length; i++)
			alpha[i] = 0.0;
		for(int i=0; i<length; i++)
			mean[i] = -1.0;
	}
	
	public String action() {
		return action.getName();
	}
	
	public void set(int i, double hyperParameter){
		alpha[i] = hyperParameter;
	}
	
	public boolean update(int i){
		alpha[i]++;
		Double[] updatedMean = mean();
		for(int k=0; k<mean.length; k++)
			if(Math.abs(updatedMean[k] - mean[k]) > DELTA)
				return false;
		convergence = true;
		return true;
	}
	
	public boolean convergence() {
		return convergence;
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
	
	public String printHpdiRCommand(double credMass) {
		StringBuilder rCommand = new StringBuilder("hdi(rdirichlet(100000, ");
		rCommand.append(printParams().replace("[", "c(").replace("]", ")"));
		rCommand.append("), credMass=").append(credMass).append(")");
		return rCommand.toString();
	}
	
	public String printParams() {
		return prettyPrintParams(alpha);
	}
	
	public String printMean() {
		return prettyPrintParams(mean());
	}
	
	public String printMode() {
		return prettyPrintParams(mode());
	}
	
	private String prettyPrintParams(Double[] params) {
		StringBuilder builder = new StringBuilder("[");
		for(Double a: params)
			if(a > 0) 
				builder.append(a).append(", ");
		builder.replace(builder.length()-2, builder.length(), "]");
		return builder.toString();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("[ ");
		for(Double a: alpha)
			builder.append(a).append(", ");
		return builder.append(" ]").toString();
	}

}
