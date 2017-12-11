package it.unimi.di.se.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unimi.di.se.monitor.Monitor.CheckPoint;
import jmarkov.basic.DecisionRule;
import jmarkov.basic.exceptions.SolverException;
import jmarkov.jmdp.CharAction;
import jmarkov.jmdp.IntegerState;
import jmarkov.jmdp.SimpleMDP;
import jmarkov.jmdp.solvers.ProbabilitySolver;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;


@Aspect
public class EventHandler {
    
    private static final Logger log = LoggerFactory.getLogger(EventHandler.class.getName());
    static final String MODEL_PATH = "src/main/resources/simple-example.mdp";
    static private final String JMDP_MODEL_PATH = "src/main/resources/simple-example.jmdp";
    
    private Monitor monitor = null;
    private SimpleMDP mdp = null;
    private DecisionRule<IntegerState, CharAction> decisionRule = null;
    
    @Pointcut("execution(public static void main(..))")
    void mainMethod() {}
    
    @Before(value="mainMethod()")
    public void initMonitor() {
    		log.info("MDP Policy computation...");
		try {
			mdp = new SimpleMDP(new BufferedReader(new FileReader(JMDP_MODEL_PATH)));
			mdp.printSolution();
			decisionRule = mdp.getOptimalPolicy().getDecisionRule();
			ProbabilitySolver<IntegerState, CharAction> solver = new ProbabilitySolver<>(mdp, decisionRule);
			solver.solve();
		} catch (FileNotFoundException|SolverException e) {
			e.printStackTrace();
		}
    		log.info("Monitor initialization...");
    		monitor = new Monitor();
    		monitor.launch();
	}
        
    @After(value="mainMethod()")
    public void shutdownMonitor(){
    		log.info("Shutting down Monitor...");
    		monitor.addEvent(Event.stopEvent());
	}
	
	private String getActionFromPolicy() {			
		monitor.addEvent(Event.readStateEvent());
		String stateName = CheckPoint.getInstance().join(Thread.currentThread());
		
		CharAction action = decisionRule.getAction(new IntegerState(Integer.parseInt(stateName.substring(1))));		
		log.info("Selected action = " + action.actionLabel());	
		return String.valueOf(action.actionLabel());
	}
	
	@Before(value="execution(public void it.unimi.di.se.simulator.MDPSimulator.resetSimulation())")
	public void resetSimulationResetEvent() {
		log.info("Reset initial state...");
		monitor.addEvent(Event.resetEvent());
	}
	
	
	@Before(value="execution(private void it.unimi.di.se.simulator.MDPSimulator.doTransition(jmarkov.jmdp.IntegerState)) && args(state)")
	public void doTransitionBeforeAdvice(jmarkov.jmdp.IntegerState state) {
				
		boolean condition = true;
		if(monitor.currentState.getName().equals("S1")) {
			if(state.label().equals("S3"))
				condition &= state != null;
		}
		if(!condition)
			log.error("*** PRECONDITION VIOLATION ***");
	}
	
	@AfterReturning(value="execution(private void it.unimi.di.se.simulator.MDPSimulator.doTransition(jmarkov.jmdp.IntegerState)) && args(state)")
	public void doTransitionAfterAdvice(jmarkov.jmdp.IntegerState state) {
		
		if(monitor.currentState.getName().equals("S3") && state.label().equals("S3"))
			monitor.addEvent(new Event("a8", System.currentTimeMillis()));
		else if(monitor.currentState.getName().equals("S4") && state.label().equals("S4"))
			monitor.addEvent(new Event("a7", System.currentTimeMillis()));
		else if(monitor.currentState.getName().equals("S5") && state.label().equals("S3"))
			monitor.addEvent(new Event("a5", System.currentTimeMillis()));
		else if(monitor.currentState.getName().equals("S0") && state.label().equals("S1"))
			monitor.addEvent(new Event("a0", System.currentTimeMillis()));
		else if(monitor.currentState.getName().equals("S0") && state.label().equals("S5"))
			monitor.addEvent(new Event("a1", System.currentTimeMillis()));
		else if(monitor.currentState.getName().equals("S0") && state.label().equals("S2"))
			monitor.addEvent(new Event("a2", System.currentTimeMillis()));
		else if(monitor.currentState.getName().equals("S1") && state.label().equals("S3"))
			monitor.addEvent(new Event("a3", System.currentTimeMillis()));
		else if(monitor.currentState.getName().equals("S1") && state.label().equals("S4"))
			monitor.addEvent(new Event("a4", System.currentTimeMillis()));
		else if(monitor.currentState.getName().equals("S2") && state.label().equals("S2"))
			monitor.addEvent(new Event("a6", System.currentTimeMillis()));
	}
	
	@Around(value="execution(private char it.unimi.di.se.simulator.MDPDriver.waitForAction(jmarkov.basic.Actions<jmarkov.jmdp.CharAction>, java.io.InputStream)) && args(actionList, input)")
	public Object waitForActionControl(ProceedingJoinPoint thisJoinPoint, jmarkov.basic.Actions<jmarkov.jmdp.CharAction> actionList, java.io.InputStream input) throws Throwable {
		Object[] args = thisJoinPoint.getArgs();
		for(int i=0; i<args.length; i++)
			if(args[i] instanceof java.io.InputStream) {
				args[i] = new ByteArrayInputStream(getActionFromPolicy().getBytes());
				break;
			}
		
		return thisJoinPoint.proceed(args);
	}
}
