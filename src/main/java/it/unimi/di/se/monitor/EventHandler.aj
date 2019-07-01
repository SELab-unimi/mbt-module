package it.unimi.di.se.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unimi.di.se.monitor.Monitor.CheckPoint;
import jmarkov.jmdp.CharAction;
import jmarkov.jmdp.SimpleMDP;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

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
    static final String MODEL_PATH = "src/main/resources/webapp.mdp";
    static private final String JMDP_MODEL_PATH = "src/main/resources/webapp.jmdp";
    
    static final int SAMPLE_SIZE = 500;
    static final Monitor.Termination TERMINATION_CONDITION = Monitor.Termination.CONVERGENCE;
    static final double COVERAGE = 1.0;
    
     public static final Map<Character, Map.Entry<String, String[]>> actionMap = new HashMap<>();
    static {
    		actionMap.put('a', new AbstractMap.SimpleEntry<>("CLICK", new String[]{"open-login","5","s-user"}));
    		actionMap.put('b', new AbstractMap.SimpleEntry<>("TEXT", new String[]{"s-user","rosario"}));
    		actionMap.put('c', new AbstractMap.SimpleEntry<>("TEXT", new String[]{"s-password","ssss"}));
    		actionMap.put('d', new AbstractMap.SimpleEntry<>("SUBMIT", new String[]{"s-password","5","Rosario"}));
    		actionMap.put('e', new AbstractMap.SimpleEntry<>("TEXT", new String[]{"search","baby"}));
    		actionMap.put('f', new AbstractMap.SimpleEntry<>("CLICK", new String[]{"search-button","5","prd_1"}));
    		actionMap.put('g', new AbstractMap.SimpleEntry<>("CLICK", new String[]{"addBasket","5","cart-dropdown"}));
    		actionMap.put('h', new AbstractMap.SimpleEntry<>("CLICK", new String[]{"cart-dropdown","2","do-checkout"}));
    		actionMap.put('i', new AbstractMap.SimpleEntry<>("CLICK", new String[]{"do-checkout","5","checkout-payment-order"}));
    		actionMap.put('j', new AbstractMap.SimpleEntry<>("CLICK", new String[]{"checkout-payment-order","5","checkout-review"}));
    		actionMap.put('k', new AbstractMap.SimpleEntry<>("CLICK", new String[]{"checkout-review","5","place-order"}));
    		actionMap.put('l', new AbstractMap.SimpleEntry<>("CLICK", new String[]{"place-order","5","thanks-msg"}));
    		actionMap.put('w', new AbstractMap.SimpleEntry<>("NONE", new String[]{}));
    	}
    
    private Monitor monitor = null;
    private SimpleMDP mdp = null;
    private DecisionMaker decisionMaker= null;
    
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
   		decisionMaker = new DecisionMaker(mdp, DecisionMaker.Policy.UNCERTAINTY);
       	log.info("Monitor initialization...");
       	monitor = Monitor.getInstance();
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
		
		CharAction action = decisionMaker.getAction(Integer.parseInt(stateName.substring(1)));
		log.info("Selected action = " + action.actionLabel());	
		return String.valueOf(action.actionLabel());
	}
	
	@Before(value="execution(public void it.unimi.di.se.simulator.MBTDriver.resetDriver())")
	public void resetDriverResetEvent() {
		log.info("Reset initial state...");
		monitor.addEvent(Event.resetEvent());
	}
	
	
	@AfterReturning(value="execution(public it.unimi.di.se.simulator.WebAppAction it.unimi.di.se.simulator.MBTDriver.doAction(jmarkov.jmdp.IntegerState, char)) && args(state, action)", returning="result")
	public void doActionAfterAdvice(jmarkov.jmdp.IntegerState state, char action, it.unimi.di.se.simulator.WebAppAction result) {
		
		long timeStamp = System.currentTimeMillis();
		monitor.addEvent(Event.readStateEvent());
		String currentMonitorState = CheckPoint.getInstance().join(Thread.currentThread());
		
		
		if(currentMonitorState.equals("S3") && state.label().equals("S3") && action=='c' && result.success())
			monitor.addEvent(new Event("a2", timeStamp));
		else if(currentMonitorState.equals("S4") && state.label().equals("S4") && action=='d' && result.success())
			monitor.addEvent(new Event("a3", timeStamp));
		else if(currentMonitorState.equals("S4") && state.label().equals("S4") && action=='d' && !result.success())
			monitor.addEvent(new Event("a4", timeStamp));
		else if(currentMonitorState.equals("S5") && state.label().equals("S5") && action=='e' && result.success())
			monitor.addEvent(new Event("a6", timeStamp));
		else if(currentMonitorState.equals("S6") && state.label().equals("S6") && action=='f' && result.success())
			monitor.addEvent(new Event("a7", timeStamp));
		else if(currentMonitorState.equals("S6") && state.label().equals("S6") && action=='f' && !result.success())
			monitor.addEvent(new Event("a8", timeStamp));
		else if(currentMonitorState.equals("S7") && state.label().equals("S7") && action=='g' && result.success())
			monitor.addEvent(new Event("a9", timeStamp));
		else if(currentMonitorState.equals("S8") && state.label().equals("S8") && action=='h' && result.success())
			monitor.addEvent(new Event("a10", timeStamp));
		else if(currentMonitorState.equals("S9") && state.label().equals("S9") && action=='i' && result.success())
			monitor.addEvent(new Event("a11", timeStamp));
		else if(currentMonitorState.equals("S11") && state.label().equals("S11") && action=='k' && result.success())
			monitor.addEvent(new Event("a13", timeStamp));
		else if(currentMonitorState.equals("S10") && state.label().equals("S10") && action=='j' && result.success())
			monitor.addEvent(new Event("a12", timeStamp));
		else if(currentMonitorState.equals("S13") && state.label().equals("S13") && action=='w' && result==null)
			monitor.addEvent(new Event("a16", timeStamp));
		else if(currentMonitorState.equals("S12") && state.label().equals("S12") && action=='l' && result.success())
			monitor.addEvent(new Event("a14", timeStamp));
		else if(currentMonitorState.equals("S12") && state.label().equals("S12") && action=='l' && !result.success())
			monitor.addEvent(new Event("a15", timeStamp));
		else if(currentMonitorState.equals("S0") && state.label().equals("S0") && action=='a' && result.success())
			monitor.addEvent(new Event("a0", timeStamp));
		else if(currentMonitorState.equals("S1") && state.label().equals("S1") && action=='b' && result.success())
			monitor.addEvent(new Event("a1", timeStamp));
		else if(currentMonitorState.equals("S2") && state.label().equals("S2") && action=='w' && result == null)
			monitor.addEvent(new Event("a5", timeStamp));
		else
			log.error("*** PRE-/POST- CONDITION VIOLATION ***");
		
		monitor.addEvent(Event.readStateEvent());
		CheckPoint.getInstance().join(Thread.currentThread());
	}
	
	@Around(value="execution(private char it.unimi.di.se.simulator.MBTDriver.waitForAction(jmarkov.basic.Actions<jmarkov.jmdp.CharAction>, java.io.InputStream)) && args(actionList, input)")
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
