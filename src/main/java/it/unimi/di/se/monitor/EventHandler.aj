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
import java.util.Map;
import java.util.HashMap;

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
	static final String MODEL_PATH = "src/main/resources/safehome_u50.mdp";
	static private final String JMDP_MODEL_PATH = "src/main/resources/safehome_u50.jmdp";
	static final String RECTANGLES_PATH = "src/main/resources/rectangles";

	static final int SAMPLE_SIZE = 200;
	static final Monitor.Termination TERMINATION_CONDITION = Monitor.Termination.LIMIT;
	static final double COVERAGE = 0.0;
	static final double LIMIT = 2000;
	public static final double DIST_WEIGHT = 0.8;
	public static final double PROF_WEIGHT = 0.2;
	static final String PROFILE_NAME = "prof1";

	private Monitor monitor = null;
	private SimpleMDP mdp = null;

	@Pointcut("execution(public static void main(..))")
	void mainMethod() {}

	@Before(value="mainMethod()")
	public void initMonitor() {
		log.debug("MDP Policy computation...");
		try {
			mdp = new SimpleMDP(new BufferedReader(new FileReader(JMDP_MODEL_PATH)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		log.debug("Monitor initialization...");
		monitor = new Monitor(mdp, Policy.DISTANCE);
		monitor.launch();
	}

	@After(value="mainMethod()")
	public void shutdownMonitor(){
		log.debug("Shutting down Monitor...");
		monitor.addEvent(Event.stopEvent());
	}

	private String getActionFromPolicy() {
		monitor.addEvent(Event.readStateEvent());
		String stateName = CheckPoint.getInstance().join(Thread.currentThread());

		StringAction action = monitor.getDecisionMaker().getAction(Integer.parseInt(stateName.substring(1)));
		log.debug("Selected action = " + action.actionLabel());
		return String.valueOf(action.actionLabel());
	}

	@Before(value="execution(public void it.unimi.di.se.sut.MDPExecutor.resetExecution())")
	public void resetExecutionResetEvent() {
		log.debug("Reset initial state...");
		monitor.addEvent(Event.resetEvent());
	}


	private static final Map<String, String> EDGE_MAP = new HashMap<>();
	private static void edgeMapInit0() {
		EDGE_MAP.put("S3S3iS5", "a4");
		EDGE_MAP.put("S3S3iS6", "a5");
		EDGE_MAP.put("S3S3iS7", "a6");
	}
	private static void edgeMapInit1() {
		EDGE_MAP.put("S4S4cS8", "a9");
		EDGE_MAP.put("S4S4cS9", "a10");
	}
	private static void edgeMapInit2() {
		EDGE_MAP.put("S5S5wS4", "a8");
	}
	private static void edgeMapInit3() {
		EDGE_MAP.put("S6S6wS3", "a7");
	}
	private static void edgeMapInit4() {
		EDGE_MAP.put("S7S7wS7", "a18");
	}
	private static void edgeMapInit5() {
		EDGE_MAP.put("S8S8wS3", "a11");
	}
	private static void edgeMapInit6() {
		EDGE_MAP.put("S9S9xS10", "a12");
		EDGE_MAP.put("S9S9xS11", "a13");
	}
	private static void edgeMapInit7() {
		EDGE_MAP.put("S11S11wS9", "a14");
		EDGE_MAP.put("S11S11wS8", "a15");
	}
	private static void edgeMapInit8() {
		EDGE_MAP.put("S10S10wS12", "a16");
		EDGE_MAP.put("S10S10wS7", "a17");
	}
	private static void edgeMapInit9() {
		EDGE_MAP.put("S12S12wS12", "a19");
	}
	private static void edgeMapInit10() {
		EDGE_MAP.put("S0S0wS1", "a0");
	}
	private static void edgeMapInit11() {
		EDGE_MAP.put("S1S1wS2", "a1");
	}
	private static void edgeMapInit12() {
		EDGE_MAP.put("S2S2iS3", "a2");
		EDGE_MAP.put("S2S2sS4", "a3");
	}

	static {
		edgeMapInit0();
		edgeMapInit1();
		edgeMapInit2();
		edgeMapInit3();
		edgeMapInit4();
		edgeMapInit5();
		edgeMapInit6();
		edgeMapInit7();
		edgeMapInit8();
		edgeMapInit9();
		edgeMapInit10();
		edgeMapInit11();
		edgeMapInit12();
	}

	@AfterReturning(value="execution(public jmarkov.jmdp.IntegerState it.unimi.di.se.sut.MDPExecutor.doAction(jmarkov.jmdp.IntegerState, String)) && args(state, action)", returning="result")
	public void doActionAfterAdvice(jmarkov.jmdp.IntegerState state, String action, jmarkov.jmdp.IntegerState result) {

		long timeStamp = System.currentTimeMillis();
		monitor.addEvent(Event.readStateEvent());
		String currentMonitorState = CheckPoint.getInstance().join(Thread.currentThread());
		log.debug("Transition : " + currentMonitorState + "-->" + result.label());

		String eventLabel = EDGE_MAP.get(currentMonitorState + state.label() + action + result.label());
		if (eventLabel != null)
			monitor.addEvent(new Event(eventLabel, timeStamp));
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
