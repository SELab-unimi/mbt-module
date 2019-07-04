package jmarkov.jmdp;

import jmarkov.basic.*;
import jmarkov.basic.exceptions.SolverException;
import jmarkov.jmdp.solvers.ProbabilitySolver;

import java.io.Reader;
import java.io.StringReader;
import java.util.*;

public class SimpleMDP extends DTMDP<IntegerState, StringAction> {

	private static final int LOW_COST = 0;
	private static final int HIGH_COST = 5;
	public static final String STATE_DELIMITER = ",";
	public static final String SPACE_SEPARATOR = " ";
	public static final String INITIAL_STATE = "i";
	public static final String UNCERTAIN_STATE = "u";

	private Map<String, Integer> stateMap = new HashMap<>();
	private List<List<Map<String, Double>>> mdp = null;
	private Map<Integer, String> uncertainRegions = new HashMap<>();
	private Map<Integer, String> rewardMap = new HashMap<>();

	private static final String inputExample =
			" S0 i, S1, S2, S3 u, S4, S5,\n" +
					"S0 a S1 0.1\n" +
					"S0 a S5 0.9\n" +
					"S0 b S2 1.0\n" +
					"S1 a S3 1.0\n" +
					"S1 b S4 1.0\n" +
					"S5 b S3 1.0\n" +
					"S2 w S2 1.0\n" +
					"S4 w S4 1.0\n" +
					"S3 w S3 1.0 u";

	public SimpleMDP(Reader in) {
		super();
		States<IntegerState> initial = null;
		Scanner lineScanner = new Scanner(in);
		int line = 0;
		while(lineScanner.hasNextLine()) {
			if(line++ == 0) {
				Scanner stateScanner = new Scanner(lineScanner.nextLine());
				stateScanner.useDelimiter(STATE_DELIMITER);
				int stateId = 0;
				while(stateScanner.hasNext()) {
					String stateDescription = stateScanner.next();
					String[] state = stateDescription.trim().split(SPACE_SEPARATOR);
					stateMap.put(state[0], stateId);
					if(state.length > 1) {
						if(state[1].equals(INITIAL_STATE))
							initial = new StatesSet<>(new IntegerState(stateId));
//						else if(state[1].equals(UNCERTAIN_STATE))
//							uncertainStates.add(stateId);
					}
					stateId++;
				}
				stateScanner.close();
			}
			else {
				if(line++ == 2) {
					//mdp = (HashMap<String, Double>[][]) new HashMap[stateMap.size()][stateMap.size()];
					mdp = new ArrayList<>(stateMap.size());
					for(int i = 0; i < stateMap.size(); i++) {
						List<Map<String, Double>> row = new ArrayList<>(stateMap.size());
						mdp.add(i, row);
						for(int j = 0; j < stateMap.size(); j++)
							row.add(j, new HashMap<>());
					}
				}
				Scanner transitionScanner = new Scanner(lineScanner.nextLine());
				while(transitionScanner.hasNext()) {
					int i = stateMap.get(transitionScanner.next());
					String a = transitionScanner.next();
					int j = stateMap.get(transitionScanner.next());
					double p = Double.parseDouble(transitionScanner.next());
					mdp.get(i).get(j).put(a, p);
					if (transitionScanner.hasNext() && transitionScanner.next().equals("u")) {
						uncertainRegions.put(i, a);
					}
				}
				transitionScanner.close();
			}
		}
		super.initial = initial;
		lineScanner.close();
	}

	public IntegerState getInitialState() {
		return initial.iterator().next();
	}

	public Map<Integer, String> getUncertainRegions() {
		return uncertainRegions;
	}
	
	public void setHighReward(Integer s, String a) {
		rewardMap.put(s, a);
	}
	
	public void clearRewards() {
		rewardMap.clear();
	}

	@Override
	public double immediateCost(IntegerState s, StringAction a) {
		if (rewardMap.containsKey(s.getId()) && rewardMap.get(s.getId()).equals(a.actionLabel()))
			return LOW_COST;
		return HIGH_COST;
	}

	@Override
	public double prob(IntegerState i, IntegerState j, StringAction a) {
		return mdp.get(i.getId()).get(j.getId()).getOrDefault(a.actionLabel(), 0.0);
	}

	@Override
	public States<IntegerState> reachable(IntegerState s, StringAction a) {
		StatesSet<IntegerState> states = new StatesSet<>();
		for(int j=0; j<mdp.size(); j++)
			if(mdp.get(s.getId()).get(j).getOrDefault(a.actionLabel(), 0.0) > 0.0)
				states.add(new IntegerState(j));
		return states;
	}

	@Override
	public Actions<StringAction> feasibleActions(IntegerState s) {
		ActionsSet<StringAction> actions = new ActionsSet<>();
		List<Map<String, Double>> row = mdp.get(s.getId());
		for (int j = 0; j < stateMap.size(); j++) {
			for (String a: row.get(j).keySet()) {
				actions.add(new StringAction(a));
			}
		}
		return actions;
	}

	public boolean isAbsorbing(IntegerState s) {
		Actions<StringAction> actions = feasibleActions(s);
		if(actions.size() > 1)
			return false;
		States<IntegerState> reachableStates = reachable(s, actions.iterator().next());
		if(reachableStates.size() == 1 && reachableStates.iterator().next().equals(s))
			return true;

		return false;
	}

	public static void main(String[] args) {
		SimpleMDP problem = new SimpleMDP(new StringReader(inputExample));
		problem.printSolution();
		DecisionRule<IntegerState, StringAction> decisionRule;
		try {
			decisionRule = problem.getOptimalPolicy().getDecisionRule();

			ProbabilitySolver<IntegerState, StringAction> solver = new ProbabilitySolver<>(problem, decisionRule);
			solver.solve();
			//solver.setGaussSeidel(true);
			//System.out.println("Gauss");
			//solver.solve();
		} catch (SolverException e) {
			e.printStackTrace();
		}
	}
}

class ActionProbPair {
	String action;
	double probability;

	public ActionProbPair(String a, double p) {
		action = a;
		probability = p;
	}
}