package it.unimi.di.se.monitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Injector;

import net.openhft.affinity.AffinityLock;
import net.openhft.affinity.AffinityStrategies;
import it.unimi.di.se.mdp.MdpDslStandaloneSetup;
import it.unimi.di.se.mdp.mdpDsl.Arc;
import it.unimi.di.se.mdp.mdpDsl.ConcentrationParam;
import it.unimi.di.se.mdp.mdpDsl.DirichletPrior;
import it.unimi.di.se.mdp.mdpDsl.MDPModel;
import it.unimi.di.se.mdp.mdpDsl.Map;
import it.unimi.di.se.mdp.mdpDsl.State;


public class Monitor {
	
	private static final Logger log = LoggerFactory.getLogger(Monitor.class.getName());
	
	private MDPModel model = null;
	State currentState = null;
	private long currentTime;
	private LinkedBlockingQueue<Event> queue = null;
	
	private HashMap<State, ArrayList<Arc>> outgoingArcs = new HashMap<State, ArrayList<Arc>>();
	private HashMap<Arc, Map> arcsMapping = new HashMap<Arc, Map>();
	
	// Bayesian analysis fields
	private HashMap<State, Dirichlet> dirichlet = new HashMap<State, Dirichlet>();
	private HashMap<State, Integer> stateIndex = new HashMap<State, Integer>();
	
	public Monitor(){
		Injector injector = new MdpDslStandaloneSetup().createInjectorAndDoEMFRegistration();
		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		URI uri = URI.createURI("dummy:/model.mdp");
		Resource resource = resourceSet.createResource(uri);
		
		InputStream in = null;
		try {
			in = new FileInputStream(new File(EventHandler.MODEL_PATH));
			resource.load(in, resourceSet.getLoadOptions());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// init MDP model
		model = (MDPModel) resource.getContents().get(0);
		retrieveOutgoingArcs();
		retrieveMapping();
		queue = new LinkedBlockingQueue<Event>();
		
		int i = 0;
		for(State s: model.getStates())
			stateIndex.put(s, i++);
		
		// init Baesyan analysis
		for(State s: model.getStates()){
			if(s.getPrior() != null && s.getPrior().size() > 0){
				for(DirichletPrior prior: s.getPrior()) {
					Dirichlet d = new Dirichlet(model.getStates().size(), prior.getAct());
					for(ConcentrationParam c: prior.getConcentration())
						d.set(stateIndex.get(c.getDst()), Double.parseDouble(c.getAlpha()));
					dirichlet.put(s, d);
				}
			}
		}
	}
	
	private Arc retrieveArc(State source, State target){
		for(Arc a: outgoingArcs.get(source))
			if(a.getDst().equals(target))
				return a;
		return null;
	}
	
	private void retrieveOutgoingArcs(){
		for(Arc a: model.getArcs())
			addArc(a.getSrc(), a);
	}
	
	private void retrieveMapping(){
		for(Map m: model.getMapping())
			arcsMapping.put(m.getArc(), m);
	}
	
	private void addArc(State s, Arc a){
		if(outgoingArcs.containsKey(s))
			outgoingArcs.get(s).add(a);
		else {
			ArrayList<Arc> list = new ArrayList<Arc>();
			list.add(a);
			outgoingArcs.put(s, list);
		}
	}
	
	public void setInitialState(){
		for(State s: model.getStates())
			if(s.isInitial()){
				currentState = s;
				break;
			}
		currentTime = System.currentTimeMillis();
	}
	
	public void launch(){
		final AffinityLock al = AffinityLock.acquireLock();
		try {
			log.info("[AFFINITY] SUT locked: CPU " + al.cpuId());
	        
		    Thread t = new Thread(new Runnable() {
		        @Override
		        public void run() {
		        	try (AffinityLock al2 = al.acquireLock(AffinityStrategies.DIFFERENT_SOCKET, AffinityStrategies.DIFFERENT_CORE, AffinityStrategies.ANY)) {
		        		log.info("[AFFINITY] Monitor locked: CPU " + al2.cpuId());
		        		Monitor.this.startMonitor();		        
		            }
		        }
		    });
		    t.start();
		} finally {
			al.release();
		}
	}
	
	private void startMonitor() {
		setInitialState();
		log.info("MONITOR STARTED...");
		while (true) {
			try {
				Event event = queue.take();
				if (event.isStop()) {
					log.info("MONITOR STOPPED...");
					break;
				} else if (event.isReset()) {
					setInitialState();
				} else if (!checkEvent(event)) {
					try {
						throw new Exception("Invalid event: " + event);
					} catch (Exception e) {
						e.printStackTrace();
						log.info("Current state: ");
						log.info(currentState.getName());

						log.info("**** TEST FAILED ****");
						log.info("TRACE:\n");
						System.exit(1);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean checkEvent(Event event) {
		long time = event.getTime() - currentTime;
		//log.info("[Monitor] checking event: " + event.getName() + ", time: " + time);
		for(Arc a: outgoingArcs.get(currentState))
			if(a.getName().equals(event.getName())){
				
				// Bayesian analysis
				if(dirichlet.containsKey(currentState))
					dirichlet.get(currentState).update(stateIndex.get(a.getDst()));				
				
				// update state
				currentState = a.getDst();
				currentTime = event.getTime();
				return true;
			}
		return false;
	}
    
	public void addEvent(Event event){
		try {
			queue.put(event);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
