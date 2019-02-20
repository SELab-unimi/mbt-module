package mbt.monitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jsat.linear.DenseVector;

import it.unimi.di.se.mdp.mdpDsl.Action;

public class Dirichlet {
	
	private static final double K = 2;
	
	private static final Logger log = LoggerFactory.getLogger(Dirichlet.class.getName());
	
	private Action action;
	// hyper parameters
	private Double[] alpha;
	private Double[] sample;
	private double prevPdf = 0;
	private int sampleSize = 0;
	private int count = 0;
	private double distance = 0.0;
	
	public Dirichlet(int length, Action action){
		this.action = action;
		alpha = new Double[length];
		for(int i=0; i<length; i++)
			alpha[i] = 0.0;
		sample = new Double[length];
		for(int i=0; i<length; i++)
			sample[i] = 0.0;
	}
	
	public String action() {
		return action.getName();
	}
	
	public void set(int i, double hyperParameter){
		alpha[i] = hyperParameter;
	}
	
	public int getSampleSize() {
		return sampleSize;
	}
	
	public int getCount() {
		return count;
	}
	
	public void resetCount() {
		count = 0;
	}
	
	/**
	 * Return Pr(D|M), i.e., the density function of the inferred model on the observed data
	 * @return
	 */
	public double pdf() {
		double[] unboxedSample = unbox(sample);
		double[] unboxedAlpha = unbox(alpha);
		double result = 0.0;
		try {
			result = new jsat.distributions.multivariate.Dirichlet(new DenseVector(unboxedAlpha))
					.pdf(new DenseVector(mean(unboxedSample)));
		}
		catch(ArithmeticException e) {
			result = 0.0;
		}
		return result;
	}
	
	private double[] unbox(Double[] args) {
		List<Double> support = new ArrayList<Double>();
		for(Double d: args)
			if(d > 0)
				support.add(d);
		return Stream.of(support.toArray(new Double[0])).mapToDouble(Double::doubleValue).toArray();
	}
	
	public void update(int i){
		count++;
		sampleSize++;
		alpha[i]++;
		sample[i]++;
	}
	
	public String report() {
		double currentPdf = pdf();
		double prevVal = prevPdf;
		return "[Dirichlet] tests = " + sampleSize +
				", Bayes Factor = " + (currentPdf/prevVal) + 
				", E[x_i] = " + printMean() +
				", x_i = " + printMode() +
				", HPD region = " + Arrays.deepToString(hpdRegion(0.95)) +
				", region size = " + distance;
	}
	
	public boolean convergence() {
		double currentPdf = pdf();
		double prevVal = prevPdf;
		prevPdf = currentPdf;
		log.warn("[Dirichlet] tests = " + sampleSize +
				", Bayes Factor = " + (currentPdf/prevVal) + 
				", E[x_i] = " + printMean() +
				", x_i = " + printMode() +
				", HPD region = " + Arrays.deepToString(hpdRegion(0.95)));
		if(prevVal > 0 && currentPdf > 0)
			return (currentPdf/prevVal) < K;
		return false;
	}
	
	public Double[] mode() {
		Double[] result = new Double[alpha.length];
		for(int i=0; i<alpha.length; i++) {
			if(alpha[i] > 1)
				result[i] = (alpha[i] - 1) / (sum() - length());
			else
				result[i] = 0.0;
		}
		return result;
	}
	
	private int length() {
		int i=0;
		for(double a: alpha)
			if(a>0)
				i++;
		return i;
	}
	
	public double[] mean(double[] in) {
		double[] result = new double[in.length];
		for(int i=0; i<in.length; i++)
			result[i] = in[i] / sum(in);
		return result;
	}
	
	public Double[] mean() {
		Double[] result = new Double[alpha.length];
		for(int i=0; i<alpha.length; i++)
			result[i] = alpha[i] / sum();
		return result;
	}
	
	private double sum(double[] in) {
		double sum = 0;
		for(int i=0; i<in.length; i++)
			sum += in[i];
		return sum;
	}

	private double sum() {
		double sum = 0;
		for(int i=0; i<alpha.length; i++)
			sum += alpha[i];
		return sum;
	}
	
	public Double[] params() {
		return alpha;
	}
	
	public double[][] hpdRegion(double credMass) {
		String rCommand = new StringBuilder("hdi(rdirichlet(100000, ")
				.append(printParams().replace("[", "c(").replace("]", ")"))
				.append("), credMass=").append(credMass)
				.append(")").toString();
		double[][] region = transpose(REngineAccessPoint.getEngine().eval(rCommand).asDoubleMatrix());
		updateDistance(region);
		return region;
	}
	
	private void updateDistance(double[][] region) {
		double result = 0.0;
		for (int i = 0; i < region.length; i++) {
			double tmpDist = Math.abs(region[i][1] - region[i][0]);
			if(tmpDist > result)
				result = tmpDist;
		}
		distance = result;
	}
	
	public double getDistance() {
		if(distance == 0.0)
			hpdRegion(0.95);
		return distance;		
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
	
	private static double[][] transpose(double[][] m){
        double[][] t = new double[m[0].length][m.length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                t[j][i] = m[i][j];
        return t;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("[ ");
		for(Double a: alpha)
			builder.append(a).append(", ");
		return builder.append(" ]").toString();
	}

}
