package jmarkov.jmdp;

import jmarkov.basic.DecisionRule;
import jmarkov.basic.exceptions.SolverException;
import jmarkov.jmdp.solvers.ProbabilitySolver;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.*;

public class SimpleMDPTest {

    private SimpleMDP mdp = null;
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

    @Before
    public void setUp() throws Exception {
        mdp = new SimpleMDP(new StringReader(input));
    }

    @Test
    public void valueIterationPolicy() throws Exception {
        mdp.printSolution();
        DecisionRule<IntegerState, CharAction> decisionRule;
        try {
            decisionRule = mdp.getOptimalPolicy().getDecisionRule();
            ProbabilitySolver<IntegerState, CharAction> solver = new ProbabilitySolver<>(mdp, decisionRule);
            solver.solve();
        } catch (SolverException e) {
            e.printStackTrace();
        }
        assertNotNull(mdp);
    }

    @Test
    public void valueIterationGaussSeidel() throws Exception {
        mdp.printSolution();
        DecisionRule<IntegerState, CharAction> decisionRule;
        try {
            decisionRule = mdp.getOptimalPolicy().getDecisionRule();
            ProbabilitySolver<IntegerState, CharAction> solver = new ProbabilitySolver<>(mdp, decisionRule);
            solver.setGaussSeidel(true);
            solver.solve();
        } catch (SolverException e) {
            e.printStackTrace();
        }
        assertNotNull(mdp);
    }
}