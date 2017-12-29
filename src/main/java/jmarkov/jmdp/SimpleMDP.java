package jmarkov.jmdp;

import jmarkov.basic.*;
import jmarkov.basic.exceptions.SolverException;
import jmarkov.jmdp.solvers.ProbabilitySolver;

import java.io.Reader;
import java.io.StringReader;
import java.util.*;

public class SimpleMDP extends DTMDP<IntegerState, CharAction> {

    private static final int LOW_COST = 0;
	private static final int HIGH_COST = 5;
	public static final String STATE_DELIMITER = ",";
    public static final String SPACE_SEPARATOR = " ";
    public static final String INITIAL_STATE = "i";
    public static final String UNCERTAIN_STATE = "u";

    private Map<String, Integer> stateMap = new HashMap<>();
    private ActionProbPair[][] mdp = null;
    private List<Integer> uncertainStates = new ArrayList<>();

    private static final String input =
            " S0 i, S1, S2, S3 u, S4, S5,\n" +
            "S0 a S1 0.1\n" +
            "S0 a S5 0.9\n" +
            "S0 b S2 1.0\n" +
            "S1 a S3 1.0\n" +
            "S1 b S4 1.0\n" +
            "S5 b S3 1.0\n" +
            "S2 w S2 1.0\n" +
            "S4 w S4 1.0\n" +
            "S3 w S3 1.0";

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
                        else if(state[1].equals(UNCERTAIN_STATE))
                            uncertainStates.add(stateId);
                    }
                    stateId++;
                }
                stateScanner.close();
            }
            else {
                if(line++ == 2) {
                    mdp = new ActionProbPair[stateMap.size()][stateMap.size()];
                    for(int i =0; i<mdp.length; i++)
                        for(int j =0; j<mdp.length; j++)
                            mdp[i][j] = null;
                }
                Scanner transitionScanner = new Scanner(lineScanner.nextLine());
                while(transitionScanner.hasNext()) {
                    int i = stateMap.get(transitionScanner.next());
                    char a = transitionScanner.next().charAt(0);
                    int j = stateMap.get(transitionScanner.next());
                    double p = Double.parseDouble(transitionScanner.next());
                    mdp[i][j] = new ActionProbPair(a, p);
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

    @Override
    public double immediateCost(IntegerState s, CharAction a) {
        for(int j: uncertainStates)
            if(mdp[s.getId()][j] != null && mdp[s.getId()][j].action == a.actionLabel() && s.getId() != j)
                return LOW_COST;
        return HIGH_COST;
    }

    @Override
    public double prob(IntegerState i, IntegerState j, CharAction a) {
        if(mdp[i.getId()][j.getId()] != null)
            return mdp[i.getId()][j.getId()].probability;
        return 0;
    }

    @Override
    public States<IntegerState> reachable(IntegerState s, CharAction a) {
        StatesSet<IntegerState> states = new StatesSet<>();
        for(int j=0; j<mdp.length; j++)
            if(mdp[s.getId()][j] != null && mdp[s.getId()][j].action == a.actionLabel())
                states.add(new IntegerState(j));
        return states;
    }

    @Override
    public Actions<CharAction> feasibleActions(IntegerState s) {
        ActionsSet<CharAction> actions = new ActionsSet<>();
        for(int j=0; j<mdp.length; j++)
            if(mdp[s.getId()][j] != null)
                actions.add(new CharAction(mdp[s.getId()][j].action));
        return actions;
    }

    public boolean isAbsorbing(IntegerState s) {
        Actions<CharAction> actions = feasibleActions(s);
        if(actions.size() > 1)
            return false;
        States<IntegerState> reachableStates = reachable(s, actions.iterator().next());
        if(reachableStates.size() == 1 && reachableStates.iterator().next().equals(s))
            return true;

        return false;
    }

    public static void main(String[] args) {
        SimpleMDP problem = new SimpleMDP(new StringReader(input));
        problem.printSolution();
        DecisionRule<IntegerState, CharAction> decisionRule;
        try {
            decisionRule = problem.getOptimalPolicy().getDecisionRule();

            ProbabilitySolver<IntegerState, CharAction> solver = new ProbabilitySolver<>(problem, decisionRule);
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
    char action;
    double probability;

    public ActionProbPair(char a, double p) {
        action = a;
        probability = p;
    }
}