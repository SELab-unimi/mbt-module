package it.unimi.di.se.monitor;

import java.util.logging.Logger;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;


@Aspect
public class EventHandler {
    
    private Monitor monitor = null;
    private static final Logger log = Logger.getLogger(EventHandler.class.getName());
    static final String MODEL_PATH = "src/main/resources/buffer.ctmc";
    
    @Pointcut("execution(public static void main(..))")
    void mainMethod() {}
    
    @Before(value="mainMethod()")
    public void initMonitor(){
    	log.info("Monitor initialization...");
    	monitor = new Monitor();
    	monitor.launch();
	}
        
    @After(value="mainMethod()")
    public void shutdownMonitor(){
    	log.info("Shutting down Monitor...");
    	monitor.addEvent(Event.StopEvent());
	}
	
	@AfterReturning(value="execution(private Integer it.unimi.di.sut.prodcons.Consumer.consume(..))", returning="result")
	public void consumeAfterAdvice(Integer result) {
		if(monitor.currentState.getName().equals("S3") || monitor.currentState.getName().equals("S2") || monitor.currentState.getName().equals("S1"))
			monitor.addEvent(new Event("private Integer it.unimi.di.sut.prodcons.Consumer.consume(..)", System.currentTimeMillis()));
		
		boolean condition = true;
		if(monitor.currentState.getName().equals("S3"))
			condition &= result > 0;
		if(!condition)
			log.severe("*** PRECONDITION VIOLATION ***");
	}
	
	@Before(value="execution(private void it.unimi.di.sut.prodcons.Producer.produce(int)) && args(value)")
	public void produceBeforeAdvice(int value) {
		
		boolean condition = true;
		if(monitor.currentState.getName().equals("S0"))
			condition &= value > 0;
		if(!condition)
			log.severe("*** POSTCONDITION VIOLATION ***");
	}
	
	@AfterReturning(value="execution(private void it.unimi.di.sut.prodcons.Producer.produce(..))")
	public void produceAfterAdvice() {
		if(monitor.currentState.getName().equals("S0") || monitor.currentState.getName().equals("S1") || monitor.currentState.getName().equals("S2"))
			monitor.addEvent(new Event("private void it.unimi.di.sut.prodcons.Producer.produce(..)", System.currentTimeMillis()));
	}
}
