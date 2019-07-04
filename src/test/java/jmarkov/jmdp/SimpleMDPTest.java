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
    
    private static final String RANDOM_GEN_INPUT = 
    		" S0 i, S1, S2, S3 u, S4,\n" + 
    		"S0 a0 S0 0.16420394010878137\n" + 
    		"S0 a0 S3 0.4200725240038906\n" + 
    		"S0 a0 S4 0.41572353588732797\n" + 
    		"S1 a0 S0 0.2879999607271682\n" + 
    		"S1 a0 S1 0.05399003409861871\n" + 
    		"S1 a0 S2 0.27684490995629923\n" + 
    		"S1 a0 S3 0.18702121583644643\n" + 
    		"S1 a0 S4 0.1941438793814674\n" + 
    		"S2 a0 S4 1.0\n" + 
    		"S3 a0 S1 0.0482297675026248\n" + 
    		"S3 a0 S2 0.2894723900841264\n" + 
    		"S3 a0 S3 0.34755659663154187\n" + 
    		"S3 a0 S4 0.31474124578170687\n" + 
    		"S4 a0 S1 0.2462040088556015\n" + 
    		"S4 a0 S2 0.6574959868101935\n" + 
    		"S4 a0 S3 0.09630000433420496\n" + 
    		"S0 a1 S0 0.3056780328059419\n" + 
    		"S0 a1 S1 0.27374199271724114\n" + 
    		"S0 a1 S2 0.4205799744768169\n" + 
    		"S1 a1 S2 0.41031827525271947\n" + 
    		"S1 a1 S3 0.5896817247472805\n" + 
    		"S2 a1 S0 0.6201202185465162\n" + 
    		"S2 a1 S1 0.05668576051559651\n" + 
    		"S2 a1 S3 0.3175741007495001\n" + 
    		"S2 a1 S4 0.00561992018838728\n" + 
    		"S3 a1 S1 0.3443158159881901\n" + 
    		"S3 a1 S2 0.15328997468028357\n" + 
    		"S3 a1 S3 0.268745425460864\n" + 
    		"S3 a1 S4 0.23364878387066235\n" + 
    		"S4 a1 S1 1.0\n";
    
    @Before
    public void setUp() throws Exception {
        mdp = new SimpleMDP(new StringReader(input));
    }

    @Test
    public void valueIterationPolicy() throws Exception {
        mdp.printSolution();
        DecisionRule<IntegerState, StringAction> decisionRule;
        try {
            decisionRule = mdp.getOptimalPolicy().getDecisionRule();
            ProbabilitySolver<IntegerState, StringAction> solver = new ProbabilitySolver<>(mdp, decisionRule);
            solver.solve();
        } catch (SolverException e) {
            e.printStackTrace();
        }
        assertNotNull(mdp);
    }

    @Test
    public void valueIterationGaussSeidel() throws Exception {
        mdp.printSolution();
        DecisionRule<IntegerState, StringAction> decisionRule;
        try {
            decisionRule = mdp.getOptimalPolicy().getDecisionRule();
            ProbabilitySolver<IntegerState, StringAction> solver = new ProbabilitySolver<>(mdp, decisionRule);
            solver.setGaussSeidel(true);
            solver.solve();
        } catch (SolverException e) {
            e.printStackTrace();
        }
        assertNotNull(mdp);
    }
    
    @Test
    public void valueIterationPolicyRandGenInput() throws Exception {
    		mdp = new SimpleMDP(new StringReader(RANDOM_GEN_INPUT));
        mdp.printSolution();
        DecisionRule<IntegerState, StringAction> decisionRule;
        try {
            decisionRule = mdp.getOptimalPolicy().getDecisionRule();
            ProbabilitySolver<IntegerState, StringAction> solver = new ProbabilitySolver<>(mdp, decisionRule);
            solver.solve();
        } catch (SolverException e) {
            e.printStackTrace();
        }
        assertNotNull(mdp);
    }
}