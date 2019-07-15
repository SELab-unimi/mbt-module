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
    static final String MODEL_PATH = "src/main/resources/n_unbal-comb-80-20-1.mdp";
    static private final String JMDP_MODEL_PATH = "src/main/resources/n_unbal-comb-80-20-1.jmdp";
    
    static final int SAMPLE_SIZE = 1000;
    static final Monitor.Termination TERMINATION_CONDITION = Monitor.Termination.LIMIT;
    static final double COVERAGE = 0.0;
    static final double LIMIT = 5000;
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
       	monitor = new Monitor(mdp, Policy.COMBINED);
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
	    		EDGE_MAP.put("S3S3a0S1", "e15");
	    		EDGE_MAP.put("S3S3a0S5", "e16");
	    		EDGE_MAP.put("S3S3a0S7", "e17");
	    		EDGE_MAP.put("S3S3a0S8", "e18");
	    		EDGE_MAP.put("S3S3a1S1", "e65");
	    		EDGE_MAP.put("S3S3a1S4", "e66");
	    		EDGE_MAP.put("S3S3a1S5", "e67");
	    		EDGE_MAP.put("S3S3a1S7", "e68");
	    		EDGE_MAP.put("S3S3a1S8", "e69");
	    		EDGE_MAP.put("S3S3a2S0", "e122");
	    		EDGE_MAP.put("S3S3a2S1", "e123");
	    		EDGE_MAP.put("S3S3a2S3", "e124");
	    		EDGE_MAP.put("S3S3a2S6", "e125");
	    		EDGE_MAP.put("S3S3a2S7", "e126");
	    		EDGE_MAP.put("S3S3a2S9", "e127");
	    		EDGE_MAP.put("S3S3a3S8", "e175");
	    		EDGE_MAP.put("S3S3a4S1", "e221");
	    		EDGE_MAP.put("S3S3a4S7", "e222");
	    		EDGE_MAP.put("S3S3a4S8", "e223");
	    		EDGE_MAP.put("S3S3a5S0", "e266");
	    		EDGE_MAP.put("S3S3a5S2", "e267");
	    		EDGE_MAP.put("S3S3a5S3", "e268");
	    		EDGE_MAP.put("S3S3a5S4", "e269");
	    		EDGE_MAP.put("S3S3a5S5", "e270");
	    		EDGE_MAP.put("S3S3a5S7", "e271");
	    		EDGE_MAP.put("S3S3a5S8", "e272");
	    		EDGE_MAP.put("S3S3a5S9", "e273");
	    		EDGE_MAP.put("S3S3a6S0", "e308");
	    		EDGE_MAP.put("S3S3a7S0", "e372");
	    		EDGE_MAP.put("S3S3a7S1", "e373");
	    		EDGE_MAP.put("S3S3a7S3", "e374");
	    		EDGE_MAP.put("S3S3a7S4", "e375");
	    		EDGE_MAP.put("S3S3a7S5", "e376");
	    		EDGE_MAP.put("S3S3a7S6", "e377");
	    		EDGE_MAP.put("S3S3a7S7", "e378");
	    		EDGE_MAP.put("S3S3a7S8", "e379");
	    		EDGE_MAP.put("S3S3a7S9", "e380");
	    		EDGE_MAP.put("S3S3a8S0", "e431");
	    		EDGE_MAP.put("S3S3a8S2", "e432");
	    		EDGE_MAP.put("S3S3a8S3", "e433");
	    		EDGE_MAP.put("S3S3a8S4", "e434");
	    		EDGE_MAP.put("S3S3a8S5", "e435");
	    		EDGE_MAP.put("S3S3a8S6", "e436");
	    		EDGE_MAP.put("S3S3a8S7", "e437");
	    		EDGE_MAP.put("S3S3a8S8", "e438");
	    		EDGE_MAP.put("S3S3a8S9", "e439");
	    		EDGE_MAP.put("S3S3a9S0", "e485");
	    		EDGE_MAP.put("S3S3a9S6", "e486");
	    		EDGE_MAP.put("S3S3a9S7", "e487");
	    		EDGE_MAP.put("S3S3a9S9", "e488");
	    	}
	    	private static void edgeMapInit1() {
	    		EDGE_MAP.put("S4S4a0S2", "e19");
	    		EDGE_MAP.put("S4S4a0S5", "e20");
	    		EDGE_MAP.put("S4S4a1S3", "e70");
	    		EDGE_MAP.put("S4S4a1S4", "e71");
	    		EDGE_MAP.put("S4S4a1S6", "e72");
	    		EDGE_MAP.put("S4S4a1S7", "e73");
	    		EDGE_MAP.put("S4S4a1S8", "e74");
	    		EDGE_MAP.put("S4S4a2S3", "e128");
	    		EDGE_MAP.put("S4S4a2S7", "e129");
	    		EDGE_MAP.put("S4S4a2S8", "e130");
	    		EDGE_MAP.put("S4S4a3S1", "e176");
	    		EDGE_MAP.put("S4S4a3S5", "e177");
	    		EDGE_MAP.put("S4S4a4S9", "e224");
	    		EDGE_MAP.put("S4S4a5S0", "e274");
	    		EDGE_MAP.put("S4S4a5S1", "e275");
	    		EDGE_MAP.put("S4S4a5S7", "e276");
	    		EDGE_MAP.put("S4S4a5S8", "e277");
	    		EDGE_MAP.put("S4S4a5S9", "e278");
	    		EDGE_MAP.put("S4S4a6S0", "e309");
	    		EDGE_MAP.put("S4S4a6S1", "e310");
	    		EDGE_MAP.put("S4S4a6S2", "e311");
	    		EDGE_MAP.put("S4S4a6S3", "e312");
	    		EDGE_MAP.put("S4S4a6S6", "e313");
	    		EDGE_MAP.put("S4S4a6S7", "e314");
	    		EDGE_MAP.put("S4S4a6S8", "e315");
	    		EDGE_MAP.put("S4S4a7S0", "e381");
	    		EDGE_MAP.put("S4S4a7S1", "e382");
	    		EDGE_MAP.put("S4S4a7S2", "e383");
	    		EDGE_MAP.put("S4S4a7S3", "e384");
	    		EDGE_MAP.put("S4S4a7S4", "e385");
	    		EDGE_MAP.put("S4S4a7S5", "e386");
	    		EDGE_MAP.put("S4S4a7S6", "e387");
	    		EDGE_MAP.put("S4S4a7S8", "e388");
	    		EDGE_MAP.put("S4S4a7S9", "e389");
	    		EDGE_MAP.put("S4S4a8S0", "e440");
	    		EDGE_MAP.put("S4S4a8S1", "e441");
	    		EDGE_MAP.put("S4S4a8S2", "e442");
	    		EDGE_MAP.put("S4S4a8S4", "e443");
	    		EDGE_MAP.put("S4S4a8S7", "e444");
	    		EDGE_MAP.put("S4S4a9S0", "e489");
	    		EDGE_MAP.put("S4S4a9S1", "e490");
	    		EDGE_MAP.put("S4S4a9S2", "e491");
	    		EDGE_MAP.put("S4S4a9S4", "e492");
	    		EDGE_MAP.put("S4S4a9S5", "e493");
	    		EDGE_MAP.put("S4S4a9S7", "e494");
	    		EDGE_MAP.put("S4S4a9S8", "e495");
	    		EDGE_MAP.put("S4S4a9S9", "e496");
	    	}
	    	private static void edgeMapInit2() {
	    		EDGE_MAP.put("S5S5a0S1", "e21");
	    		EDGE_MAP.put("S5S5a0S2", "e22");
	    		EDGE_MAP.put("S5S5a0S4", "e23");
	    		EDGE_MAP.put("S5S5a0S6", "e24");
	    		EDGE_MAP.put("S5S5a0S7", "e25");
	    		EDGE_MAP.put("S5S5a0S8", "e26");
	    		EDGE_MAP.put("S5S5a1S0", "e75");
	    		EDGE_MAP.put("S5S5a1S2", "e76");
	    		EDGE_MAP.put("S5S5a1S3", "e77");
	    		EDGE_MAP.put("S5S5a1S5", "e78");
	    		EDGE_MAP.put("S5S5a1S6", "e79");
	    		EDGE_MAP.put("S5S5a1S9", "e80");
	    		EDGE_MAP.put("S5S5a2S0", "e131");
	    		EDGE_MAP.put("S5S5a2S1", "e132");
	    		EDGE_MAP.put("S5S5a2S2", "e133");
	    		EDGE_MAP.put("S5S5a2S3", "e134");
	    		EDGE_MAP.put("S5S5a2S4", "e135");
	    		EDGE_MAP.put("S5S5a2S5", "e136");
	    		EDGE_MAP.put("S5S5a2S6", "e137");
	    		EDGE_MAP.put("S5S5a2S8", "e138");
	    		EDGE_MAP.put("S5S5a2S9", "e139");
	    		EDGE_MAP.put("S5S5a3S0", "e178");
	    		EDGE_MAP.put("S5S5a3S1", "e179");
	    		EDGE_MAP.put("S5S5a3S3", "e180");
	    		EDGE_MAP.put("S5S5a3S4", "e181");
	    		EDGE_MAP.put("S5S5a3S6", "e182");
	    		EDGE_MAP.put("S5S5a3S7", "e183");
	    		EDGE_MAP.put("S5S5a3S8", "e184");
	    		EDGE_MAP.put("S5S5a3S9", "e185");
	    		EDGE_MAP.put("S5S5a4S6", "e225");
	    		EDGE_MAP.put("S5S5a5S8", "e279");
	    		EDGE_MAP.put("S5S5a6S0", "e316");
	    		EDGE_MAP.put("S5S5a6S5", "e317");
	    		EDGE_MAP.put("S5S5a6S6", "e318");
	    		EDGE_MAP.put("S5S5a7S0", "e390");
	    		EDGE_MAP.put("S5S5a7S2", "e391");
	    		EDGE_MAP.put("S5S5a7S4", "e392");
	    		EDGE_MAP.put("S5S5a7S7", "e393");
	    		EDGE_MAP.put("S5S5a7S8", "e394");
	    		EDGE_MAP.put("S5S5a7S9", "e395");
	    		EDGE_MAP.put("S5S5a8S5", "e445");
	    		EDGE_MAP.put("S5S5a8S8", "e446");
	    		EDGE_MAP.put("S5S5a9S1", "e497");
	    		EDGE_MAP.put("S5S5a9S2", "e498");
	    	}
	    	private static void edgeMapInit3() {
	    		EDGE_MAP.put("S6S6a0S0", "e27");
	    		EDGE_MAP.put("S6S6a0S1", "e28");
	    		EDGE_MAP.put("S6S6a0S2", "e29");
	    		EDGE_MAP.put("S6S6a0S3", "e30");
	    		EDGE_MAP.put("S6S6a0S4", "e31");
	    		EDGE_MAP.put("S6S6a0S5", "e32");
	    		EDGE_MAP.put("S6S6a0S6", "e33");
	    		EDGE_MAP.put("S6S6a0S7", "e34");
	    		EDGE_MAP.put("S6S6a0S8", "e35");
	    		EDGE_MAP.put("S6S6a0S9", "e36");
	    		EDGE_MAP.put("S6S6a1S1", "e81");
	    		EDGE_MAP.put("S6S6a1S2", "e82");
	    		EDGE_MAP.put("S6S6a1S3", "e83");
	    		EDGE_MAP.put("S6S6a1S5", "e84");
	    		EDGE_MAP.put("S6S6a1S6", "e85");
	    		EDGE_MAP.put("S6S6a1S7", "e86");
	    		EDGE_MAP.put("S6S6a1S9", "e87");
	    		EDGE_MAP.put("S6S6a2S0", "e140");
	    		EDGE_MAP.put("S6S6a2S2", "e141");
	    		EDGE_MAP.put("S6S6a2S3", "e142");
	    		EDGE_MAP.put("S6S6a2S6", "e143");
	    		EDGE_MAP.put("S6S6a2S8", "e144");
	    		EDGE_MAP.put("S6S6a3S2", "e186");
	    		EDGE_MAP.put("S6S6a3S6", "e187");
	    		EDGE_MAP.put("S6S6a3S9", "e188");
	    		EDGE_MAP.put("S6S6a4S2", "e226");
	    		EDGE_MAP.put("S6S6a4S3", "e227");
	    		EDGE_MAP.put("S6S6a4S4", "e228");
	    		EDGE_MAP.put("S6S6a4S9", "e229");
	    		EDGE_MAP.put("S6S6a5S4", "e280");
	    		EDGE_MAP.put("S6S6a5S5", "e281");
	    		EDGE_MAP.put("S6S6a5S9", "e282");
	    		EDGE_MAP.put("S6S6a6S0", "e319");
	    		EDGE_MAP.put("S6S6a6S1", "e320");
	    		EDGE_MAP.put("S6S6a6S2", "e321");
	    		EDGE_MAP.put("S6S6a7S4", "e396");
	    		EDGE_MAP.put("S6S6a7S7", "e397");
	    		EDGE_MAP.put("S6S6a8S0", "e447");
	    		EDGE_MAP.put("S6S6a8S3", "e448");
	    		EDGE_MAP.put("S6S6a8S5", "e449");
	    		EDGE_MAP.put("S6S6a8S7", "e450");
	    		EDGE_MAP.put("S6S6a8S9", "e451");
	    		EDGE_MAP.put("S6S6a9S0", "e499");
	    		EDGE_MAP.put("S6S6a9S6", "e500");
	    		EDGE_MAP.put("S6S6a9S7", "e501");
	    	}
	    	private static void edgeMapInit4() {
	    		EDGE_MAP.put("S7S7a0S0", "e37");
	    		EDGE_MAP.put("S7S7a0S3", "e38");
	    		EDGE_MAP.put("S7S7a0S4", "e39");
	    		EDGE_MAP.put("S7S7a0S6", "e40");
	    		EDGE_MAP.put("S7S7a1S0", "e88");
	    		EDGE_MAP.put("S7S7a1S2", "e89");
	    		EDGE_MAP.put("S7S7a1S3", "e90");
	    		EDGE_MAP.put("S7S7a1S4", "e91");
	    		EDGE_MAP.put("S7S7a1S6", "e92");
	    		EDGE_MAP.put("S7S7a1S7", "e93");
	    		EDGE_MAP.put("S7S7a1S8", "e94");
	    		EDGE_MAP.put("S7S7a1S9", "e95");
	    		EDGE_MAP.put("S7S7a2S1", "e145");
	    		EDGE_MAP.put("S7S7a2S2", "e146");
	    		EDGE_MAP.put("S7S7a2S4", "e147");
	    		EDGE_MAP.put("S7S7a2S5", "e148");
	    		EDGE_MAP.put("S7S7a2S6", "e149");
	    		EDGE_MAP.put("S7S7a2S8", "e150");
	    		EDGE_MAP.put("S7S7a3S3", "e189");
	    		EDGE_MAP.put("S7S7a4S0", "e230");
	    		EDGE_MAP.put("S7S7a4S8", "e231");
	    		EDGE_MAP.put("S7S7a5S0", "e283");
	    		EDGE_MAP.put("S7S7a5S1", "e284");
	    		EDGE_MAP.put("S7S7a5S4", "e285");
	    		EDGE_MAP.put("S7S7a5S5", "e286");
	    		EDGE_MAP.put("S7S7a5S6", "e287");
	    		EDGE_MAP.put("S7S7a5S7", "e288");
	    		EDGE_MAP.put("S7S7a5S8", "e289");
	    		EDGE_MAP.put("S7S7a6S0", "e322");
	    		EDGE_MAP.put("S7S7a6S1", "e323");
	    		EDGE_MAP.put("S7S7a6S2", "e324");
	    		EDGE_MAP.put("S7S7a6S3", "e325");
	    		EDGE_MAP.put("S7S7a6S4", "e326");
	    		EDGE_MAP.put("S7S7a6S5", "e327");
	    		EDGE_MAP.put("S7S7a6S6", "e328");
	    		EDGE_MAP.put("S7S7a6S7", "e329");
	    		EDGE_MAP.put("S7S7a6S8", "e330");
	    		EDGE_MAP.put("S7S7a6S9", "e331");
	    		EDGE_MAP.put("S7S7a7S0", "e398");
	    		EDGE_MAP.put("S7S7a7S1", "e399");
	    		EDGE_MAP.put("S7S7a7S3", "e400");
	    		EDGE_MAP.put("S7S7a7S5", "e401");
	    		EDGE_MAP.put("S7S7a7S6", "e402");
	    		EDGE_MAP.put("S7S7a7S7", "e403");
	    		EDGE_MAP.put("S7S7a7S8", "e404");
	    		EDGE_MAP.put("S7S7a7S9", "e405");
	    		EDGE_MAP.put("S7S7a8S0", "e452");
	    		EDGE_MAP.put("S7S7a8S1", "e453");
	    		EDGE_MAP.put("S7S7a8S2", "e454");
	    		EDGE_MAP.put("S7S7a8S3", "e455");
	    		EDGE_MAP.put("S7S7a8S4", "e456");
	    		EDGE_MAP.put("S7S7a8S5", "e457");
	    		EDGE_MAP.put("S7S7a8S6", "e458");
	    		EDGE_MAP.put("S7S7a8S8", "e459");
	    		EDGE_MAP.put("S7S7a8S9", "e460");
	    		EDGE_MAP.put("S7S7a9S0", "e502");
	    		EDGE_MAP.put("S7S7a9S1", "e503");
	    		EDGE_MAP.put("S7S7a9S2", "e504");
	    		EDGE_MAP.put("S7S7a9S3", "e505");
	    		EDGE_MAP.put("S7S7a9S4", "e506");
	    		EDGE_MAP.put("S7S7a9S5", "e507");
	    		EDGE_MAP.put("S7S7a9S6", "e508");
	    		EDGE_MAP.put("S7S7a9S7", "e509");
	    		EDGE_MAP.put("S7S7a9S8", "e510");
	    		EDGE_MAP.put("S7S7a9S9", "e511");
	    	}
	    	private static void edgeMapInit5() {
	    		EDGE_MAP.put("S8S8a0S1", "e41");
	    		EDGE_MAP.put("S8S8a0S2", "e42");
	    		EDGE_MAP.put("S8S8a0S4", "e43");
	    		EDGE_MAP.put("S8S8a0S6", "e44");
	    		EDGE_MAP.put("S8S8a0S7", "e45");
	    		EDGE_MAP.put("S8S8a0S8", "e46");
	    		EDGE_MAP.put("S8S8a1S1", "e96");
	    		EDGE_MAP.put("S8S8a1S4", "e97");
	    		EDGE_MAP.put("S8S8a2S0", "e151");
	    		EDGE_MAP.put("S8S8a2S2", "e152");
	    		EDGE_MAP.put("S8S8a2S7", "e153");
	    		EDGE_MAP.put("S8S8a2S9", "e154");
	    		EDGE_MAP.put("S8S8a3S0", "e190");
	    		EDGE_MAP.put("S8S8a3S1", "e191");
	    		EDGE_MAP.put("S8S8a3S2", "e192");
	    		EDGE_MAP.put("S8S8a3S3", "e193");
	    		EDGE_MAP.put("S8S8a3S4", "e194");
	    		EDGE_MAP.put("S8S8a3S5", "e195");
	    		EDGE_MAP.put("S8S8a3S7", "e196");
	    		EDGE_MAP.put("S8S8a3S8", "e197");
	    		EDGE_MAP.put("S8S8a4S1", "e232");
	    		EDGE_MAP.put("S8S8a4S3", "e233");
	    		EDGE_MAP.put("S8S8a4S4", "e234");
	    		EDGE_MAP.put("S8S8a4S5", "e235");
	    		EDGE_MAP.put("S8S8a4S6", "e236");
	    		EDGE_MAP.put("S8S8a4S7", "e237");
	    		EDGE_MAP.put("S8S8a4S8", "e238");
	    		EDGE_MAP.put("S8S8a4S9", "e239");
	    		EDGE_MAP.put("S8S8a5S1", "e290");
	    		EDGE_MAP.put("S8S8a5S7", "e291");
	    		EDGE_MAP.put("S8S8a6S2", "e332");
	    		EDGE_MAP.put("S8S8a6S7", "e333");
	    		EDGE_MAP.put("S8S8a6S8", "e334");
	    		EDGE_MAP.put("S8S8a6S9", "e335");
	    		EDGE_MAP.put("S8S8a7S0", "e406");
	    		EDGE_MAP.put("S8S8a7S1", "e407");
	    		EDGE_MAP.put("S8S8a7S2", "e408");
	    		EDGE_MAP.put("S8S8a7S3", "e409");
	    		EDGE_MAP.put("S8S8a7S4", "e410");
	    		EDGE_MAP.put("S8S8a7S5", "e411");
	    		EDGE_MAP.put("S8S8a7S6", "e412");
	    		EDGE_MAP.put("S8S8a7S7", "e413");
	    		EDGE_MAP.put("S8S8a7S8", "e414");
	    		EDGE_MAP.put("S8S8a7S9", "e415");
	    		EDGE_MAP.put("S8S8a8S0", "e461");
	    		EDGE_MAP.put("S8S8a8S1", "e462");
	    		EDGE_MAP.put("S8S8a8S2", "e463");
	    		EDGE_MAP.put("S8S8a8S3", "e464");
	    		EDGE_MAP.put("S8S8a8S4", "e465");
	    		EDGE_MAP.put("S8S8a8S5", "e466");
	    		EDGE_MAP.put("S8S8a8S7", "e467");
	    		EDGE_MAP.put("S8S8a8S8", "e468");
	    		EDGE_MAP.put("S8S8a8S9", "e469");
	    		EDGE_MAP.put("S8S8a9S0", "e512");
	    		EDGE_MAP.put("S8S8a9S3", "e513");
	    		EDGE_MAP.put("S8S8a9S4", "e514");
	    		EDGE_MAP.put("S8S8a9S6", "e515");
	    		EDGE_MAP.put("S8S8a9S7", "e516");
	    		EDGE_MAP.put("S8S8a9S8", "e517");
	    		EDGE_MAP.put("S8S8a9S9", "e518");
	    	}
	    	private static void edgeMapInit6() {
	    		EDGE_MAP.put("S9S9a0S0", "e47");
	    		EDGE_MAP.put("S9S9a0S1", "e48");
	    		EDGE_MAP.put("S9S9a0S2", "e49");
	    		EDGE_MAP.put("S9S9a0S3", "e50");
	    		EDGE_MAP.put("S9S9a0S4", "e51");
	    		EDGE_MAP.put("S9S9a0S5", "e52");
	    		EDGE_MAP.put("S9S9a0S6", "e53");
	    		EDGE_MAP.put("S9S9a0S7", "e54");
	    		EDGE_MAP.put("S9S9a0S8", "e55");
	    		EDGE_MAP.put("S9S9a0S9", "e56");
	    		EDGE_MAP.put("S9S9a1S0", "e98");
	    		EDGE_MAP.put("S9S9a1S1", "e99");
	    		EDGE_MAP.put("S9S9a1S2", "e100");
	    		EDGE_MAP.put("S9S9a1S4", "e101");
	    		EDGE_MAP.put("S9S9a1S5", "e102");
	    		EDGE_MAP.put("S9S9a1S6", "e103");
	    		EDGE_MAP.put("S9S9a1S7", "e104");
	    		EDGE_MAP.put("S9S9a2S3", "e155");
	    		EDGE_MAP.put("S9S9a3S1", "e198");
	    		EDGE_MAP.put("S9S9a3S3", "e199");
	    		EDGE_MAP.put("S9S9a3S4", "e200");
	    		EDGE_MAP.put("S9S9a3S5", "e201");
	    		EDGE_MAP.put("S9S9a3S7", "e202");
	    		EDGE_MAP.put("S9S9a3S8", "e203");
	    		EDGE_MAP.put("S9S9a4S0", "e240");
	    		EDGE_MAP.put("S9S9a4S2", "e241");
	    		EDGE_MAP.put("S9S9a4S4", "e242");
	    		EDGE_MAP.put("S9S9a4S5", "e243");
	    		EDGE_MAP.put("S9S9a4S6", "e244");
	    		EDGE_MAP.put("S9S9a5S9", "e292");
	    		EDGE_MAP.put("S9S9a6S0", "e336");
	    		EDGE_MAP.put("S9S9a6S1", "e337");
	    		EDGE_MAP.put("S9S9a6S3", "e338");
	    		EDGE_MAP.put("S9S9a6S4", "e339");
	    		EDGE_MAP.put("S9S9a6S5", "e340");
	    		EDGE_MAP.put("S9S9a6S6", "e341");
	    		EDGE_MAP.put("S9S9a6S7", "e342");
	    		EDGE_MAP.put("S9S9a6S9", "e343");
	    		EDGE_MAP.put("S9S9a7S2", "e416");
	    		EDGE_MAP.put("S9S9a8S0", "e470");
	    		EDGE_MAP.put("S9S9a8S6", "e471");
	    		EDGE_MAP.put("S9S9a8S8", "e472");
	    		EDGE_MAP.put("S9S9a8S9", "e473");
	    		EDGE_MAP.put("S9S9a9S0", "e519");
	    		EDGE_MAP.put("S9S9a9S1", "e520");
	    		EDGE_MAP.put("S9S9a9S2", "e521");
	    		EDGE_MAP.put("S9S9a9S5", "e522");
	    		EDGE_MAP.put("S9S9a9S6", "e523");
	    		EDGE_MAP.put("S9S9a9S7", "e524");
	    		EDGE_MAP.put("S9S9a9S9", "e525");
	    	}
	    	private static void edgeMapInit7() {
	    		EDGE_MAP.put("S0S0a0S1", "e0");
	    		EDGE_MAP.put("S0S0a0S2", "e1");
	    		EDGE_MAP.put("S0S0a0S4", "e2");
	    		EDGE_MAP.put("S0S0a0S5", "e3");
	    		EDGE_MAP.put("S0S0a1S2", "e57");
	    		EDGE_MAP.put("S0S0a1S6", "e58");
	    		EDGE_MAP.put("S0S0a2S0", "e105");
	    		EDGE_MAP.put("S0S0a2S3", "e106");
	    		EDGE_MAP.put("S0S0a2S6", "e107");
	    		EDGE_MAP.put("S0S0a2S8", "e108");
	    		EDGE_MAP.put("S0S0a3S0", "e156");
	    		EDGE_MAP.put("S0S0a3S1", "e157");
	    		EDGE_MAP.put("S0S0a3S2", "e158");
	    		EDGE_MAP.put("S0S0a3S3", "e159");
	    		EDGE_MAP.put("S0S0a3S4", "e160");
	    		EDGE_MAP.put("S0S0a3S5", "e161");
	    		EDGE_MAP.put("S0S0a3S6", "e162");
	    		EDGE_MAP.put("S0S0a3S7", "e163");
	    		EDGE_MAP.put("S0S0a3S8", "e164");
	    		EDGE_MAP.put("S0S0a3S9", "e165");
	    		EDGE_MAP.put("S0S0a4S2", "e204");
	    		EDGE_MAP.put("S0S0a4S3", "e205");
	    		EDGE_MAP.put("S0S0a4S7", "e206");
	    		EDGE_MAP.put("S0S0a4S8", "e207");
	    		EDGE_MAP.put("S0S0a4S9", "e208");
	    		EDGE_MAP.put("S0S0a5S1", "e245");
	    		EDGE_MAP.put("S0S0a5S2", "e246");
	    		EDGE_MAP.put("S0S0a5S4", "e247");
	    		EDGE_MAP.put("S0S0a5S5", "e248");
	    		EDGE_MAP.put("S0S0a5S7", "e249");
	    		EDGE_MAP.put("S0S0a6S9", "e293");
	    		EDGE_MAP.put("S0S0a7S0", "e344");
	    		EDGE_MAP.put("S0S0a7S1", "e345");
	    		EDGE_MAP.put("S0S0a7S2", "e346");
	    		EDGE_MAP.put("S0S0a7S3", "e347");
	    		EDGE_MAP.put("S0S0a7S4", "e348");
	    		EDGE_MAP.put("S0S0a7S5", "e349");
	    		EDGE_MAP.put("S0S0a7S6", "e350");
	    		EDGE_MAP.put("S0S0a7S7", "e351");
	    		EDGE_MAP.put("S0S0a7S8", "e352");
	    		EDGE_MAP.put("S0S0a7S9", "e353");
	    		EDGE_MAP.put("S0S0a8S0", "e417");
	    		EDGE_MAP.put("S0S0a8S1", "e418");
	    		EDGE_MAP.put("S0S0a8S2", "e419");
	    		EDGE_MAP.put("S0S0a8S3", "e420");
	    		EDGE_MAP.put("S0S0a8S4", "e421");
	    		EDGE_MAP.put("S0S0a8S6", "e422");
	    		EDGE_MAP.put("S0S0a8S7", "e423");
	    		EDGE_MAP.put("S0S0a8S8", "e424");
	    		EDGE_MAP.put("S0S0a9S1", "e474");
	    		EDGE_MAP.put("S0S0a9S9", "e475");
	    	}
	    	private static void edgeMapInit8() {
	    		EDGE_MAP.put("S1S1a0S7", "e4");
	    		EDGE_MAP.put("S1S1a1S0", "e59");
	    		EDGE_MAP.put("S1S1a1S1", "e60");
	    		EDGE_MAP.put("S1S1a1S3", "e61");
	    		EDGE_MAP.put("S1S1a2S1", "e109");
	    		EDGE_MAP.put("S1S1a2S2", "e110");
	    		EDGE_MAP.put("S1S1a2S3", "e111");
	    		EDGE_MAP.put("S1S1a2S4", "e112");
	    		EDGE_MAP.put("S1S1a2S6", "e113");
	    		EDGE_MAP.put("S1S1a2S7", "e114");
	    		EDGE_MAP.put("S1S1a2S9", "e115");
	    		EDGE_MAP.put("S1S1a3S3", "e166");
	    		EDGE_MAP.put("S1S1a4S0", "e209");
	    		EDGE_MAP.put("S1S1a4S3", "e210");
	    		EDGE_MAP.put("S1S1a5S0", "e250");
	    		EDGE_MAP.put("S1S1a5S2", "e251");
	    		EDGE_MAP.put("S1S1a5S4", "e252");
	    		EDGE_MAP.put("S1S1a5S6", "e253");
	    		EDGE_MAP.put("S1S1a5S8", "e254");
	    		EDGE_MAP.put("S1S1a5S9", "e255");
	    		EDGE_MAP.put("S1S1a6S0", "e294");
	    		EDGE_MAP.put("S1S1a6S1", "e295");
	    		EDGE_MAP.put("S1S1a6S2", "e296");
	    		EDGE_MAP.put("S1S1a6S4", "e297");
	    		EDGE_MAP.put("S1S1a6S5", "e298");
	    		EDGE_MAP.put("S1S1a6S6", "e299");
	    		EDGE_MAP.put("S1S1a6S7", "e300");
	    		EDGE_MAP.put("S1S1a6S9", "e301");
	    		EDGE_MAP.put("S1S1a7S0", "e354");
	    		EDGE_MAP.put("S1S1a7S1", "e355");
	    		EDGE_MAP.put("S1S1a7S3", "e356");
	    		EDGE_MAP.put("S1S1a7S4", "e357");
	    		EDGE_MAP.put("S1S1a7S5", "e358");
	    		EDGE_MAP.put("S1S1a7S6", "e359");
	    		EDGE_MAP.put("S1S1a7S7", "e360");
	    		EDGE_MAP.put("S1S1a7S8", "e361");
	    		EDGE_MAP.put("S1S1a7S9", "e362");
	    		EDGE_MAP.put("S1S1a8S0", "e425");
	    		EDGE_MAP.put("S1S1a8S2", "e426");
	    		EDGE_MAP.put("S1S1a8S3", "e427");
	    		EDGE_MAP.put("S1S1a8S5", "e428");
	    		EDGE_MAP.put("S1S1a8S8", "e429");
	    		EDGE_MAP.put("S1S1a9S0", "e476");
	    		EDGE_MAP.put("S1S1a9S2", "e477");
	    		EDGE_MAP.put("S1S1a9S3", "e478");
	    		EDGE_MAP.put("S1S1a9S5", "e479");
	    		EDGE_MAP.put("S1S1a9S8", "e480");
	    	}
	    	private static void edgeMapInit9() {
	    		EDGE_MAP.put("S2S2a0S0", "e5");
	    		EDGE_MAP.put("S2S2a0S1", "e6");
	    		EDGE_MAP.put("S2S2a0S2", "e7");
	    		EDGE_MAP.put("S2S2a0S3", "e8");
	    		EDGE_MAP.put("S2S2a0S4", "e9");
	    		EDGE_MAP.put("S2S2a0S5", "e10");
	    		EDGE_MAP.put("S2S2a0S6", "e11");
	    		EDGE_MAP.put("S2S2a0S7", "e12");
	    		EDGE_MAP.put("S2S2a0S8", "e13");
	    		EDGE_MAP.put("S2S2a0S9", "e14");
	    		EDGE_MAP.put("S2S2a1S4", "e62");
	    		EDGE_MAP.put("S2S2a1S5", "e63");
	    		EDGE_MAP.put("S2S2a1S9", "e64");
	    		EDGE_MAP.put("S2S2a2S0", "e116");
	    		EDGE_MAP.put("S2S2a2S3", "e117");
	    		EDGE_MAP.put("S2S2a2S4", "e118");
	    		EDGE_MAP.put("S2S2a2S5", "e119");
	    		EDGE_MAP.put("S2S2a2S7", "e120");
	    		EDGE_MAP.put("S2S2a2S8", "e121");
	    		EDGE_MAP.put("S2S2a3S1", "e167");
	    		EDGE_MAP.put("S2S2a3S2", "e168");
	    		EDGE_MAP.put("S2S2a3S3", "e169");
	    		EDGE_MAP.put("S2S2a3S4", "e170");
	    		EDGE_MAP.put("S2S2a3S6", "e171");
	    		EDGE_MAP.put("S2S2a3S7", "e172");
	    		EDGE_MAP.put("S2S2a3S8", "e173");
	    		EDGE_MAP.put("S2S2a3S9", "e174");
	    		EDGE_MAP.put("S2S2a4S0", "e211");
	    		EDGE_MAP.put("S2S2a4S1", "e212");
	    		EDGE_MAP.put("S2S2a4S2", "e213");
	    		EDGE_MAP.put("S2S2a4S3", "e214");
	    		EDGE_MAP.put("S2S2a4S4", "e215");
	    		EDGE_MAP.put("S2S2a4S5", "e216");
	    		EDGE_MAP.put("S2S2a4S6", "e217");
	    		EDGE_MAP.put("S2S2a4S7", "e218");
	    		EDGE_MAP.put("S2S2a4S8", "e219");
	    		EDGE_MAP.put("S2S2a4S9", "e220");
	    		EDGE_MAP.put("S2S2a5S0", "e256");
	    		EDGE_MAP.put("S2S2a5S1", "e257");
	    		EDGE_MAP.put("S2S2a5S2", "e258");
	    		EDGE_MAP.put("S2S2a5S3", "e259");
	    		EDGE_MAP.put("S2S2a5S4", "e260");
	    		EDGE_MAP.put("S2S2a5S5", "e261");
	    		EDGE_MAP.put("S2S2a5S6", "e262");
	    		EDGE_MAP.put("S2S2a5S7", "e263");
	    		EDGE_MAP.put("S2S2a5S8", "e264");
	    		EDGE_MAP.put("S2S2a5S9", "e265");
	    		EDGE_MAP.put("S2S2a6S2", "e302");
	    		EDGE_MAP.put("S2S2a6S3", "e303");
	    		EDGE_MAP.put("S2S2a6S5", "e304");
	    		EDGE_MAP.put("S2S2a6S6", "e305");
	    		EDGE_MAP.put("S2S2a6S7", "e306");
	    		EDGE_MAP.put("S2S2a6S8", "e307");
	    		EDGE_MAP.put("S2S2a7S0", "e363");
	    		EDGE_MAP.put("S2S2a7S1", "e364");
	    		EDGE_MAP.put("S2S2a7S2", "e365");
	    		EDGE_MAP.put("S2S2a7S3", "e366");
	    		EDGE_MAP.put("S2S2a7S5", "e367");
	    		EDGE_MAP.put("S2S2a7S6", "e368");
	    		EDGE_MAP.put("S2S2a7S7", "e369");
	    		EDGE_MAP.put("S2S2a7S8", "e370");
	    		EDGE_MAP.put("S2S2a7S9", "e371");
	    		EDGE_MAP.put("S2S2a8S2", "e430");
	    		EDGE_MAP.put("S2S2a9S2", "e481");
	    		EDGE_MAP.put("S2S2a9S4", "e482");
	    		EDGE_MAP.put("S2S2a9S8", "e483");
	    		EDGE_MAP.put("S2S2a9S9", "e484");
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
