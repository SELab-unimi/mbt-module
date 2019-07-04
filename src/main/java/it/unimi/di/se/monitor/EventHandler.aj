package it.unimi.di.se.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unimi.di.se.decision.DecisionMakerFactory;
import it.unimi.di.se.decision.Policy;
import it.unimi.di.se.monitor.Monitor.CheckPoint;
import jmarkov.jmdp.StringAction;
import jmarkov.jmdp.SimpleMDP;

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
    static final String MODEL_PATH = "src/main/resources/random1.mdp";
    static private final String JMDP_MODEL_PATH = "src/main/resources/random1.jmdp";
    
    static final int SAMPLE_SIZE = 1000;
    static final Monitor.Termination TERMINATION_CONDITION = Monitor.Termination.LIMIT;
    static final double COVERAGE = 0.0;
    static final double LIMIT = 2000;
    public static final double DIST_WEIGHT = 0.0;
    public static final double PROF_WEIGHT = 0.0;
    static final String PROFILE_NAME = null;
    
    private Monitor monitor = null;
    private SimpleMDP mdp = null;
    
    @Pointcut("execution(public static void main(..))")
    void mainMethod() {}
    
    @Before(value="mainMethod()")
    public void initMonitor() {
    		log.info("MDP Policy computation...");
   		try {
   			mdp = new SimpleMDP(new BufferedReader(new FileReader(JMDP_MODEL_PATH)));
   		} catch (FileNotFoundException e) {
   			e.printStackTrace();
   		}
       	log.info("Monitor initialization...");
       	monitor = new Monitor(new DecisionMakerFactory().createPolicy(mdp, Policy.DISTANCE));
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
		
		StringAction action = monitor.getDecisionMaker().getAction(Integer.parseInt(stateName.substring(1)));
		log.info("Selected action = " + action.actionLabel());	
		return String.valueOf(action.actionLabel());
	}
	
	@Before(value="execution(public void it.unimi.di.se.sut.MDPExecutor.resetExecution())")
	public void resetExecutionResetEvent() {
		log.info("Reset initial state...");
		monitor.addEvent(Event.resetEvent());
	}
	
	
	@AfterReturning(value="execution(public jmarkov.jmdp.IntegerState it.unimi.di.se.sut.MDPExecutor.doAction(jmarkov.jmdp.IntegerState, String)) && args(state, action)", returning="result")
	public void doActionAfterAdvice(jmarkov.jmdp.IntegerState state, String action, jmarkov.jmdp.IntegerState result) {
		
		long timeStamp = System.currentTimeMillis();
		monitor.addEvent(Event.readStateEvent());
		String currentMonitorState = CheckPoint.getInstance().join(Thread.currentThread());
		log.info("Transition : " + currentMonitorState + "-->" + result.label());
		
		
		if(currentMonitorState.equals("S3") && state.label().equals("S3") && action.equals("a0") && result.label().equals("S1"))
			monitor.addEvent(new Event("e18", timeStamp));
		else if(currentMonitorState.equals("S3") && state.label().equals("S3") && action.equals("a0") && result.label().equals("S2"))
			monitor.addEvent(new Event("e19", timeStamp));
		else if(currentMonitorState.equals("S3") && state.label().equals("S3") && action.equals("a0") && result.label().equals("S4"))
			monitor.addEvent(new Event("e20", timeStamp));
		else if(currentMonitorState.equals("S3") && state.label().equals("S3") && action.equals("a0") && result.label().equals("S5"))
			monitor.addEvent(new Event("e21", timeStamp));
		else if(currentMonitorState.equals("S3") && state.label().equals("S3") && action.equals("a0") && result.label().equals("S6"))
			monitor.addEvent(new Event("e22", timeStamp));
		else if(currentMonitorState.equals("S3") && state.label().equals("S3") && action.equals("a0") && result.label().equals("S8"))
			monitor.addEvent(new Event("e23", timeStamp));
		else if(currentMonitorState.equals("S3") && state.label().equals("S3") && action.equals("a0") && result.label().equals("S9"))
			monitor.addEvent(new Event("e24", timeStamp));
		else if(currentMonitorState.equals("S3") && state.label().equals("S3") && action.equals("a1") && result.label().equals("S8"))
			monitor.addEvent(new Event("e91", timeStamp));
		else if(currentMonitorState.equals("S4") && state.label().equals("S4") && action.equals("a0") && result.label().equals("S0"))
			monitor.addEvent(new Event("e25", timeStamp));
		else if(currentMonitorState.equals("S4") && state.label().equals("S4") && action.equals("a0") && result.label().equals("S1"))
			monitor.addEvent(new Event("e26", timeStamp));
		else if(currentMonitorState.equals("S4") && state.label().equals("S4") && action.equals("a0") && result.label().equals("S2"))
			monitor.addEvent(new Event("e27", timeStamp));
		else if(currentMonitorState.equals("S4") && state.label().equals("S4") && action.equals("a0") && result.label().equals("S6"))
			monitor.addEvent(new Event("e28", timeStamp));
		else if(currentMonitorState.equals("S4") && state.label().equals("S4") && action.equals("a0") && result.label().equals("S7"))
			monitor.addEvent(new Event("e29", timeStamp));
		else if(currentMonitorState.equals("S4") && state.label().equals("S4") && action.equals("a0") && result.label().equals("S9"))
			monitor.addEvent(new Event("e30", timeStamp));
		else if(currentMonitorState.equals("S4") && state.label().equals("S4") && action.equals("a1") && result.label().equals("S5"))
			monitor.addEvent(new Event("e92", timeStamp));
		else if(currentMonitorState.equals("S4") && state.label().equals("S4") && action.equals("a1") && result.label().equals("S9"))
			monitor.addEvent(new Event("e93", timeStamp));
		else if(currentMonitorState.equals("S5") && state.label().equals("S5") && action.equals("a0") && result.label().equals("S0"))
			monitor.addEvent(new Event("e31", timeStamp));
		else if(currentMonitorState.equals("S5") && state.label().equals("S5") && action.equals("a0") && result.label().equals("S1"))
			monitor.addEvent(new Event("e32", timeStamp));
		else if(currentMonitorState.equals("S5") && state.label().equals("S5") && action.equals("a0") && result.label().equals("S3"))
			monitor.addEvent(new Event("e33", timeStamp));
		else if(currentMonitorState.equals("S5") && state.label().equals("S5") && action.equals("a0") && result.label().equals("S5"))
			monitor.addEvent(new Event("e34", timeStamp));
		else if(currentMonitorState.equals("S5") && state.label().equals("S5") && action.equals("a0") && result.label().equals("S6"))
			monitor.addEvent(new Event("e35", timeStamp));
		else if(currentMonitorState.equals("S5") && state.label().equals("S5") && action.equals("a0") && result.label().equals("S7"))
			monitor.addEvent(new Event("e36", timeStamp));
		else if(currentMonitorState.equals("S5") && state.label().equals("S5") && action.equals("a0") && result.label().equals("S8"))
			monitor.addEvent(new Event("e37", timeStamp));
		else if(currentMonitorState.equals("S5") && state.label().equals("S5") && action.equals("a0") && result.label().equals("S9"))
			monitor.addEvent(new Event("e38", timeStamp));
		else if(currentMonitorState.equals("S5") && state.label().equals("S5") && action.equals("a1") && result.label().equals("S0"))
			monitor.addEvent(new Event("e94", timeStamp));
		else if(currentMonitorState.equals("S5") && state.label().equals("S5") && action.equals("a1") && result.label().equals("S1"))
			monitor.addEvent(new Event("e95", timeStamp));
		else if(currentMonitorState.equals("S5") && state.label().equals("S5") && action.equals("a1") && result.label().equals("S3"))
			monitor.addEvent(new Event("e96", timeStamp));
		else if(currentMonitorState.equals("S5") && state.label().equals("S5") && action.equals("a1") && result.label().equals("S4"))
			monitor.addEvent(new Event("e97", timeStamp));
		else if(currentMonitorState.equals("S5") && state.label().equals("S5") && action.equals("a1") && result.label().equals("S5"))
			monitor.addEvent(new Event("e98", timeStamp));
		else if(currentMonitorState.equals("S5") && state.label().equals("S5") && action.equals("a1") && result.label().equals("S6"))
			monitor.addEvent(new Event("e99", timeStamp));
		else if(currentMonitorState.equals("S5") && state.label().equals("S5") && action.equals("a1") && result.label().equals("S8"))
			monitor.addEvent(new Event("e100", timeStamp));
		else if(currentMonitorState.equals("S5") && state.label().equals("S5") && action.equals("a1") && result.label().equals("S9"))
			monitor.addEvent(new Event("e101", timeStamp));
		else if(currentMonitorState.equals("S6") && state.label().equals("S6") && action.equals("a0") && result.label().equals("S0"))
			monitor.addEvent(new Event("e39", timeStamp));
		else if(currentMonitorState.equals("S6") && state.label().equals("S6") && action.equals("a0") && result.label().equals("S3"))
			monitor.addEvent(new Event("e40", timeStamp));
		else if(currentMonitorState.equals("S6") && state.label().equals("S6") && action.equals("a0") && result.label().equals("S4"))
			monitor.addEvent(new Event("e41", timeStamp));
		else if(currentMonitorState.equals("S6") && state.label().equals("S6") && action.equals("a0") && result.label().equals("S7"))
			monitor.addEvent(new Event("e42", timeStamp));
		else if(currentMonitorState.equals("S6") && state.label().equals("S6") && action.equals("a0") && result.label().equals("S8"))
			monitor.addEvent(new Event("e43", timeStamp));
		else if(currentMonitorState.equals("S6") && state.label().equals("S6") && action.equals("a0") && result.label().equals("S9"))
			monitor.addEvent(new Event("e44", timeStamp));
		else if(currentMonitorState.equals("S6") && state.label().equals("S6") && action.equals("a1") && result.label().equals("S6"))
			monitor.addEvent(new Event("e102", timeStamp));
		else if(currentMonitorState.equals("S6") && state.label().equals("S6") && action.equals("a1") && result.label().equals("S9"))
			monitor.addEvent(new Event("e103", timeStamp));
		else if(currentMonitorState.equals("S7") && state.label().equals("S7") && action.equals("a0") && result.label().equals("S0"))
			monitor.addEvent(new Event("e45", timeStamp));
		else if(currentMonitorState.equals("S7") && state.label().equals("S7") && action.equals("a0") && result.label().equals("S3"))
			monitor.addEvent(new Event("e46", timeStamp));
		else if(currentMonitorState.equals("S7") && state.label().equals("S7") && action.equals("a0") && result.label().equals("S4"))
			monitor.addEvent(new Event("e47", timeStamp));
		else if(currentMonitorState.equals("S7") && state.label().equals("S7") && action.equals("a0") && result.label().equals("S5"))
			monitor.addEvent(new Event("e48", timeStamp));
		else if(currentMonitorState.equals("S7") && state.label().equals("S7") && action.equals("a0") && result.label().equals("S6"))
			monitor.addEvent(new Event("e49", timeStamp));
		else if(currentMonitorState.equals("S7") && state.label().equals("S7") && action.equals("a0") && result.label().equals("S7"))
			monitor.addEvent(new Event("e50", timeStamp));
		else if(currentMonitorState.equals("S7") && state.label().equals("S7") && action.equals("a0") && result.label().equals("S8"))
			monitor.addEvent(new Event("e51", timeStamp));
		else if(currentMonitorState.equals("S7") && state.label().equals("S7") && action.equals("a1") && result.label().equals("S0"))
			monitor.addEvent(new Event("e104", timeStamp));
		else if(currentMonitorState.equals("S7") && state.label().equals("S7") && action.equals("a1") && result.label().equals("S1"))
			monitor.addEvent(new Event("e105", timeStamp));
		else if(currentMonitorState.equals("S7") && state.label().equals("S7") && action.equals("a1") && result.label().equals("S2"))
			monitor.addEvent(new Event("e106", timeStamp));
		else if(currentMonitorState.equals("S7") && state.label().equals("S7") && action.equals("a1") && result.label().equals("S5"))
			monitor.addEvent(new Event("e107", timeStamp));
		else if(currentMonitorState.equals("S7") && state.label().equals("S7") && action.equals("a1") && result.label().equals("S6"))
			monitor.addEvent(new Event("e108", timeStamp));
		else if(currentMonitorState.equals("S8") && state.label().equals("S8") && action.equals("a0") && result.label().equals("S2"))
			monitor.addEvent(new Event("e52", timeStamp));
		else if(currentMonitorState.equals("S8") && state.label().equals("S8") && action.equals("a0") && result.label().equals("S5"))
			monitor.addEvent(new Event("e53", timeStamp));
		else if(currentMonitorState.equals("S8") && state.label().equals("S8") && action.equals("a0") && result.label().equals("S8"))
			monitor.addEvent(new Event("e54", timeStamp));
		else if(currentMonitorState.equals("S8") && state.label().equals("S8") && action.equals("a1") && result.label().equals("S1"))
			monitor.addEvent(new Event("e109", timeStamp));
		else if(currentMonitorState.equals("S8") && state.label().equals("S8") && action.equals("a1") && result.label().equals("S2"))
			monitor.addEvent(new Event("e110", timeStamp));
		else if(currentMonitorState.equals("S8") && state.label().equals("S8") && action.equals("a1") && result.label().equals("S3"))
			monitor.addEvent(new Event("e111", timeStamp));
		else if(currentMonitorState.equals("S8") && state.label().equals("S8") && action.equals("a1") && result.label().equals("S5"))
			monitor.addEvent(new Event("e112", timeStamp));
		else if(currentMonitorState.equals("S8") && state.label().equals("S8") && action.equals("a1") && result.label().equals("S6"))
			monitor.addEvent(new Event("e113", timeStamp));
		else if(currentMonitorState.equals("S8") && state.label().equals("S8") && action.equals("a1") && result.label().equals("S7"))
			monitor.addEvent(new Event("e114", timeStamp));
		else if(currentMonitorState.equals("S8") && state.label().equals("S8") && action.equals("a1") && result.label().equals("S8"))
			monitor.addEvent(new Event("e115", timeStamp));
		else if(currentMonitorState.equals("S8") && state.label().equals("S8") && action.equals("a1") && result.label().equals("S9"))
			monitor.addEvent(new Event("e116", timeStamp));
		else if(currentMonitorState.equals("S9") && state.label().equals("S9") && action.equals("a0") && result.label().equals("S0"))
			monitor.addEvent(new Event("e55", timeStamp));
		else if(currentMonitorState.equals("S9") && state.label().equals("S9") && action.equals("a0") && result.label().equals("S1"))
			monitor.addEvent(new Event("e56", timeStamp));
		else if(currentMonitorState.equals("S9") && state.label().equals("S9") && action.equals("a0") && result.label().equals("S2"))
			monitor.addEvent(new Event("e57", timeStamp));
		else if(currentMonitorState.equals("S9") && state.label().equals("S9") && action.equals("a0") && result.label().equals("S3"))
			monitor.addEvent(new Event("e58", timeStamp));
		else if(currentMonitorState.equals("S9") && state.label().equals("S9") && action.equals("a0") && result.label().equals("S4"))
			monitor.addEvent(new Event("e59", timeStamp));
		else if(currentMonitorState.equals("S9") && state.label().equals("S9") && action.equals("a0") && result.label().equals("S5"))
			monitor.addEvent(new Event("e60", timeStamp));
		else if(currentMonitorState.equals("S9") && state.label().equals("S9") && action.equals("a0") && result.label().equals("S6"))
			monitor.addEvent(new Event("e61", timeStamp));
		else if(currentMonitorState.equals("S9") && state.label().equals("S9") && action.equals("a0") && result.label().equals("S7"))
			monitor.addEvent(new Event("e62", timeStamp));
		else if(currentMonitorState.equals("S9") && state.label().equals("S9") && action.equals("a0") && result.label().equals("S8"))
			monitor.addEvent(new Event("e63", timeStamp));
		else if(currentMonitorState.equals("S9") && state.label().equals("S9") && action.equals("a0") && result.label().equals("S9"))
			monitor.addEvent(new Event("e64", timeStamp));
		else if(currentMonitorState.equals("S9") && state.label().equals("S9") && action.equals("a1") && result.label().equals("S1"))
			monitor.addEvent(new Event("e117", timeStamp));
		else if(currentMonitorState.equals("S9") && state.label().equals("S9") && action.equals("a1") && result.label().equals("S2"))
			monitor.addEvent(new Event("e118", timeStamp));
		else if(currentMonitorState.equals("S9") && state.label().equals("S9") && action.equals("a1") && result.label().equals("S3"))
			monitor.addEvent(new Event("e119", timeStamp));
		else if(currentMonitorState.equals("S9") && state.label().equals("S9") && action.equals("a1") && result.label().equals("S4"))
			monitor.addEvent(new Event("e120", timeStamp));
		else if(currentMonitorState.equals("S9") && state.label().equals("S9") && action.equals("a1") && result.label().equals("S5"))
			monitor.addEvent(new Event("e121", timeStamp));
		else if(currentMonitorState.equals("S9") && state.label().equals("S9") && action.equals("a1") && result.label().equals("S6"))
			monitor.addEvent(new Event("e122", timeStamp));
		else if(currentMonitorState.equals("S9") && state.label().equals("S9") && action.equals("a1") && result.label().equals("S8"))
			monitor.addEvent(new Event("e123", timeStamp));
		else if(currentMonitorState.equals("S9") && state.label().equals("S9") && action.equals("a1") && result.label().equals("S9"))
			monitor.addEvent(new Event("e124", timeStamp));
		else if(currentMonitorState.equals("S0") && state.label().equals("S0") && action.equals("a0") && result.label().equals("S0"))
			monitor.addEvent(new Event("e0", timeStamp));
		else if(currentMonitorState.equals("S0") && state.label().equals("S0") && action.equals("a0") && result.label().equals("S3"))
			monitor.addEvent(new Event("e1", timeStamp));
		else if(currentMonitorState.equals("S0") && state.label().equals("S0") && action.equals("a0") && result.label().equals("S4"))
			monitor.addEvent(new Event("e2", timeStamp));
		else if(currentMonitorState.equals("S0") && state.label().equals("S0") && action.equals("a1") && result.label().equals("S0"))
			monitor.addEvent(new Event("e65", timeStamp));
		else if(currentMonitorState.equals("S0") && state.label().equals("S0") && action.equals("a1") && result.label().equals("S3"))
			monitor.addEvent(new Event("e66", timeStamp));
		else if(currentMonitorState.equals("S0") && state.label().equals("S0") && action.equals("a1") && result.label().equals("S4"))
			monitor.addEvent(new Event("e67", timeStamp));
		else if(currentMonitorState.equals("S0") && state.label().equals("S0") && action.equals("a1") && result.label().equals("S6"))
			monitor.addEvent(new Event("e68", timeStamp));
		else if(currentMonitorState.equals("S0") && state.label().equals("S0") && action.equals("a1") && result.label().equals("S7"))
			monitor.addEvent(new Event("e69", timeStamp));
		else if(currentMonitorState.equals("S0") && state.label().equals("S0") && action.equals("a1") && result.label().equals("S8"))
			monitor.addEvent(new Event("e70", timeStamp));
		else if(currentMonitorState.equals("S1") && state.label().equals("S1") && action.equals("a0") && result.label().equals("S0"))
			monitor.addEvent(new Event("e3", timeStamp));
		else if(currentMonitorState.equals("S1") && state.label().equals("S1") && action.equals("a0") && result.label().equals("S1"))
			monitor.addEvent(new Event("e4", timeStamp));
		else if(currentMonitorState.equals("S1") && state.label().equals("S1") && action.equals("a0") && result.label().equals("S2"))
			monitor.addEvent(new Event("e5", timeStamp));
		else if(currentMonitorState.equals("S1") && state.label().equals("S1") && action.equals("a0") && result.label().equals("S3"))
			monitor.addEvent(new Event("e6", timeStamp));
		else if(currentMonitorState.equals("S1") && state.label().equals("S1") && action.equals("a0") && result.label().equals("S4"))
			monitor.addEvent(new Event("e7", timeStamp));
		else if(currentMonitorState.equals("S1") && state.label().equals("S1") && action.equals("a0") && result.label().equals("S5"))
			monitor.addEvent(new Event("e8", timeStamp));
		else if(currentMonitorState.equals("S1") && state.label().equals("S1") && action.equals("a0") && result.label().equals("S6"))
			monitor.addEvent(new Event("e9", timeStamp));
		else if(currentMonitorState.equals("S1") && state.label().equals("S1") && action.equals("a0") && result.label().equals("S7"))
			monitor.addEvent(new Event("e10", timeStamp));
		else if(currentMonitorState.equals("S1") && state.label().equals("S1") && action.equals("a0") && result.label().equals("S8"))
			monitor.addEvent(new Event("e11", timeStamp));
		else if(currentMonitorState.equals("S1") && state.label().equals("S1") && action.equals("a0") && result.label().equals("S9"))
			monitor.addEvent(new Event("e12", timeStamp));
		else if(currentMonitorState.equals("S1") && state.label().equals("S1") && action.equals("a1") && result.label().equals("S0"))
			monitor.addEvent(new Event("e71", timeStamp));
		else if(currentMonitorState.equals("S1") && state.label().equals("S1") && action.equals("a1") && result.label().equals("S1"))
			monitor.addEvent(new Event("e72", timeStamp));
		else if(currentMonitorState.equals("S1") && state.label().equals("S1") && action.equals("a1") && result.label().equals("S2"))
			monitor.addEvent(new Event("e73", timeStamp));
		else if(currentMonitorState.equals("S1") && state.label().equals("S1") && action.equals("a1") && result.label().equals("S3"))
			monitor.addEvent(new Event("e74", timeStamp));
		else if(currentMonitorState.equals("S1") && state.label().equals("S1") && action.equals("a1") && result.label().equals("S4"))
			monitor.addEvent(new Event("e75", timeStamp));
		else if(currentMonitorState.equals("S1") && state.label().equals("S1") && action.equals("a1") && result.label().equals("S5"))
			monitor.addEvent(new Event("e76", timeStamp));
		else if(currentMonitorState.equals("S1") && state.label().equals("S1") && action.equals("a1") && result.label().equals("S6"))
			monitor.addEvent(new Event("e77", timeStamp));
		else if(currentMonitorState.equals("S1") && state.label().equals("S1") && action.equals("a1") && result.label().equals("S7"))
			monitor.addEvent(new Event("e78", timeStamp));
		else if(currentMonitorState.equals("S1") && state.label().equals("S1") && action.equals("a1") && result.label().equals("S8"))
			monitor.addEvent(new Event("e79", timeStamp));
		else if(currentMonitorState.equals("S1") && state.label().equals("S1") && action.equals("a1") && result.label().equals("S9"))
			monitor.addEvent(new Event("e80", timeStamp));
		else if(currentMonitorState.equals("S2") && state.label().equals("S2") && action.equals("a0") && result.label().equals("S2"))
			monitor.addEvent(new Event("e13", timeStamp));
		else if(currentMonitorState.equals("S2") && state.label().equals("S2") && action.equals("a0") && result.label().equals("S3"))
			monitor.addEvent(new Event("e14", timeStamp));
		else if(currentMonitorState.equals("S2") && state.label().equals("S2") && action.equals("a0") && result.label().equals("S6"))
			monitor.addEvent(new Event("e15", timeStamp));
		else if(currentMonitorState.equals("S2") && state.label().equals("S2") && action.equals("a0") && result.label().equals("S7"))
			monitor.addEvent(new Event("e16", timeStamp));
		else if(currentMonitorState.equals("S2") && state.label().equals("S2") && action.equals("a0") && result.label().equals("S9"))
			monitor.addEvent(new Event("e17", timeStamp));
		else if(currentMonitorState.equals("S2") && state.label().equals("S2") && action.equals("a1") && result.label().equals("S0"))
			monitor.addEvent(new Event("e81", timeStamp));
		else if(currentMonitorState.equals("S2") && state.label().equals("S2") && action.equals("a1") && result.label().equals("S1"))
			monitor.addEvent(new Event("e82", timeStamp));
		else if(currentMonitorState.equals("S2") && state.label().equals("S2") && action.equals("a1") && result.label().equals("S2"))
			monitor.addEvent(new Event("e83", timeStamp));
		else if(currentMonitorState.equals("S2") && state.label().equals("S2") && action.equals("a1") && result.label().equals("S3"))
			monitor.addEvent(new Event("e84", timeStamp));
		else if(currentMonitorState.equals("S2") && state.label().equals("S2") && action.equals("a1") && result.label().equals("S4"))
			monitor.addEvent(new Event("e85", timeStamp));
		else if(currentMonitorState.equals("S2") && state.label().equals("S2") && action.equals("a1") && result.label().equals("S5"))
			monitor.addEvent(new Event("e86", timeStamp));
		else if(currentMonitorState.equals("S2") && state.label().equals("S2") && action.equals("a1") && result.label().equals("S6"))
			monitor.addEvent(new Event("e87", timeStamp));
		else if(currentMonitorState.equals("S2") && state.label().equals("S2") && action.equals("a1") && result.label().equals("S7"))
			monitor.addEvent(new Event("e88", timeStamp));
		else if(currentMonitorState.equals("S2") && state.label().equals("S2") && action.equals("a1") && result.label().equals("S8"))
			monitor.addEvent(new Event("e89", timeStamp));
		else if(currentMonitorState.equals("S2") && state.label().equals("S2") && action.equals("a1") && result.label().equals("S9"))
			monitor.addEvent(new Event("e90", timeStamp));
		else
			log.error("*** PRE-/POST- CONDITION VIOLATION ***");
	}
	
	@Around(value="execution(private String it.unimi.di.se.sut.Driver.waitForAction(jmarkov.basic.Actions<jmarkov.jmdp.StringAction>, java.io.InputStream)) && args(actionList, input)")
	public Object waitForActionControl(ProceedingJoinPoint thisJoinPoint, jmarkov.basic.Actions<jmarkov.jmdp.StringAction> actionList, java.io.InputStream input) throws Throwable {
		Object[] args = thisJoinPoint.getArgs();
		for(int i=0; i<args.length; i++)
			if(args[i] instanceof java.io.InputStream) {
				args[i] = new ByteArrayInputStream(getActionFromPolicy().getBytes());
				break;
			}
		
		return thisJoinPoint.proceed(args);
	}
}
