package it.unimi.di.se.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;


@Aspect
public class EventHandler {
    
    private Monitor monitor = null;
    private static final Logger log = LoggerFactory.getLogger(EventHandler.class.getName());
    static final String MODEL_PATH = "src/main/resources/simple-example.mdp";
    
    @Pointcut("execution(public static void main(..))")
    void mainMethod() {}
    
    @Before(value="mainMethod()")
    public void initMonitor(){
    		//log.info("Monitor initialization...");
    		monitor = new Monitor();
    		monitor.launch();
	}
        
    @After(value="mainMethod()")
    public void shutdownMonitor(){
    		//log.info("Shutting down Monitor...");
    		monitor.addEvent(Event.StopEvent());
	}
	
	@AfterReturning(value="execution(private void it.unimi.di.se.simulator.MDPSimulator.doTransition(..)) && args(state)")
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
}
