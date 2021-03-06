package it.unimi.di.se.monitor;

import java.io.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Injector;

import it.unimi.di.se.decision.DecisionMaker;
import it.unimi.di.se.decision.DecisionMakerFactory;
import it.unimi.di.se.decision.Policy;
import it.unimi.di.se.mdp.MdpDslStandaloneSetup;
import it.unimi.di.se.mdp.mdpDsl.Arc;
import it.unimi.di.se.mdp.mdpDsl.ConcentrationParam;
import it.unimi.di.se.mdp.mdpDsl.DirichletPrior;
import it.unimi.di.se.mdp.mdpDsl.MDPModel;
import it.unimi.di.se.mdp.mdpDsl.ObservableMap;
import it.unimi.di.se.mdp.mdpDsl.State;
import it.unimi.di.se.mdp.mdpDsl.Profile;
import it.unimi.di.se.mdp.mdpDsl.ProfileMap;
import jmarkov.basic.DecisionRule;
import jmarkov.jmdp.IntegerState;
import jmarkov.jmdp.SimpleMDP;
import jmarkov.jmdp.StringAction;


public class Monitor {

	enum Termination {
		COVERAGE,
		CONVERGENCE,
		BOUNDS,
		LIMIT
	}

	private static final Logger log = LoggerFactory.getLogger(Monitor.class.getName());

	private MDPModel model = null;
	State currentState = null;
	private long currentTime;
	private LinkedBlockingQueue<Event> queue = null;

	private HashMap<State, ArrayList<Arc>> outgoingArcs = new HashMap<>();
	private HashMap<Arc, ObservableMap> arcsMapping = new HashMap<>();

	// Bayesian analysis fields
	private HashMap<State, Dirichlet> prior = new HashMap<>();
	private HashMap<State, Dirichlet> posterior = new HashMap<>();
	private List<HyperRectangle> hyperRectangles = new ArrayList<>();
	private HashMap<State, Integer> stateIndex = new HashMap<>();

	private DecisionMaker decisionMaker = null;

	private Coverage coverageInfo = null;

	public Monitor(SimpleMDP mdp, Policy policy){
		Injector injector = new MdpDslStandaloneSetup().createInjectorAndDoEMFRegistration();
		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		URI uri = URI.createURI("dummy:/model.mdp");
		Resource resource = resourceSet.createResource(uri);

		InputStream in = null;
		try {
			in = new FileInputStream(new File(EventHandler.MODEL_PATH));
			resource.load(in, resourceSet.getLoadOptions());
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// init MDP model
		model = (MDPModel) resource.getContents().get(0);
		retrieveOutgoingArcs();
		retrieveMapping();
		queue = new LinkedBlockingQueue<Event>();

		// init decision maker
		if (model.getRules() == null ||  model.getRules().size() == 0) {
			decisionMaker = new DecisionMakerFactory().createPolicy(mdp, policy);
		}
		else {
			Map<Integer, DecisionRule<IntegerState, StringAction>> decisionRules = new HashMap<>();
			for (it.unimi.di.se.mdp.mdpDsl.DecisionRule r: model.getRules()) {
				Integer s = Integer.parseInt(r.getObjective().getName().substring(1));
				DecisionRule<IntegerState, StringAction> rule = new DecisionRule<>();
				for (it.unimi.di.se.mdp.mdpDsl.StateActionRule m: r.getRuleMap()) {
					rule.set(new IntegerState(Integer.parseInt(m.getState().getName().substring(1))), new StringAction(m.getAction().getName()));
				}
				decisionRules.put(s, rule);
			}
			decisionMaker = new DecisionMakerFactory().createPolicy(mdp, policy, decisionRules);
		}

		// coverage info
		coverageInfo = new Coverage(model);

		int i = 0;
		for(State s: model.getStates())
			stateIndex.put(s, i++);

		// profile info
		for(Profile p: model.getProfiles()) {
			if(p.getName().equals(EventHandler.PROFILE_NAME)) {
				for(ProfileMap map: p.getMap())
					decisionMaker.setOperationalProfile(stateIndex.get(map.getState()).intValue(), Double.parseDouble(map.getWeight()));
			}
		}

		// init Baesyan analysis
		for(State s: model.getStates()){
			if(s.getPrior() != null && s.getPrior().size() > 0){
				for(DirichletPrior srcModelPrior: s.getPrior()) {
					Dirichlet dirichlePrior = new Dirichlet(model.getStates().size(), srcModelPrior.getAct());
					Dirichlet dirichlePosterior = new Dirichlet(model.getStates().size(), srcModelPrior.getAct());
					for(ConcentrationParam c: srcModelPrior.getConcentration()) {
						dirichlePrior.set(stateIndex.get(c.getDst()), Double.parseDouble(c.getAlpha()));
						dirichlePosterior.set(stateIndex.get(c.getDst()), Double.parseDouble(c.getAlpha()));
					}
					prior.put(s, dirichlePrior);
					posterior.put(s, dirichlePosterior);
					decisionMaker.updateDistance(stateIndex.get(s), dirichlePosterior.getDistance());
				}
			}
		}

		// init hyper-rectangles
		if (EventHandler.TERMINATION_CONDITION == Termination.BOUNDS) {
			Scanner lines = null;
			try {
				lines = new Scanner(new FileReader(EventHandler.RECTANGLES_PATH));
				//lines = new Scanner(new FileReader("src/main/resources/rectangles"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			boolean firstLine = true;
			List<Map.Entry<State, Integer>> stateVarsMap = new ArrayList<>();
			while (lines.hasNextLine()) {
				// Assumption: first line contains a mapping between states and parametric #variables (e.g., "S3-2 S10-1")
				// Assumption: next lines contain the hyper-rectangles following the above mapping (e.g., "[(0.89375, 0.9), (0.01953125, 0.03125), (0.9125, 0.925)]")
				if (firstLine) {
					String[] stateVariables = lines.nextLine().split(" ");
					for (String pair: stateVariables) {
						String[] args = pair.trim().split("-");
						stateVarsMap.add(new AbstractMap.SimpleEntry<State, Integer>(stateByName(args[0]), Integer.parseInt(args[1])));
					}
					firstLine = false;
				}
				else {
					HyperRectangle rect = new HyperRectangle();
					String[] rawBounds = lines.nextLine().split("\\),");
					int k = 0;
					for (Map.Entry<State, Integer> entry: stateVarsMap) {
						Bound[] bounds = new Bound[entry.getValue()];
						for (int j = k; j< k + bounds.length; j++) {
							String[] rawBound = rawBounds[j].split(",");
							Double lbound = Double.parseDouble(cleanBound(rawBound[0]));
							Double ubound = Double.parseDouble(cleanBound(rawBound[1]));
							bounds[j-k] = new Bound(lbound, ubound);
						}
						rect.add(entry.getKey(), bounds);
						k += bounds.length;
					}
					hyperRectangles.add(rect);
				}
			}
			lines.close();
		}
	}

//	private Arc retrieveArc(State source, State target){
//		for(Arc a: outgoingArcs.get(source))
//			if(a.getDst().equals(target))
//				return a;
//		return null;
//	}

	private String cleanBound(String rawBound) {
		return rawBound.trim()
				.replace("[", "")
				.replace("(", "")
				.replace(")]", "");
	}

	private State stateByName(String name) {
		for (State s: model.getStates()) {
			if (s.getName().equals(name))
				return s;
		}
		return null;
	}

	private void retrieveOutgoingArcs(){
		for(Arc a: model.getArcs())
			addArc(a.getSrc(), a);
	}

	private void retrieveMapping(){
		for(ObservableMap m: model.getObservableActions())
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

	public DecisionMaker getDecisionMaker() {
		return decisionMaker;
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
		Thread t = new Thread(new Runnable() {
	        @Override
	        public void run() {
		        	Monitor.this.startMonitor();
	        }
	    });
	    t.start();
	}

	private void startMonitor() {
		setInitialState();
		log.debug("MONITOR STARTED...");
		while (true) {
			try {
				Event event = queue.take();
				if (event.isStop()) {
					log.debug("MONITOR STOPPED...");
					report();
					System.exit(0);
				} else if (event.isReset()) {
					setInitialState();
				} else if (event.isReadState()) {
					CheckPoint.getInstance().join(Thread.currentThread(), currentState.getName());
				} else if (!checkEvent(event)) {
					try {
						throw new Exception("Invalid event: " + event.getName());
					} catch (Exception e) {
						e.printStackTrace();
						log.debug("Current state: ");
						log.debug(currentState.getName());

						log.debug("**** TEST FAILED ****");
						log.debug("TRACE:\n");
						System.exit(1);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private int showInferenceInfo = 0;
	private int eventCount = 0;

	private boolean checkEvent(Event event) {
		//long time = event.getTime() - currentTime;
		//log.info("[Monitor] checking event: " + event.getName() + ", time: " + time);
		eventCount++;
		for(Arc a: outgoingArcs.get(currentState))
			if(a.getName().equals(event.getName())){

				// Bayesian analysis and termination if <currentState, action> region is uncertain
				if(posterior.containsKey(currentState) &&
						posterior.get(currentState).action().equals(a.getAct().getName())) {

					// update count of <currentState, action> region
					decisionMaker.updateCount(stateIndex.get(currentState));

					posterior.get(currentState).update(stateIndex.get(a.getDst()));
					boolean convergence = true;
					boolean testConvergence = true;

					if(showInferenceInfo++ > 10) {
						for(State s: posterior.keySet())
							log.info(s.getName() + " - " + posterior.get(s).report() + " events = " + eventCount);
						showInferenceInfo = 0;
					}

					for(State s: posterior.keySet()) {
						log.debug("[Monitor] count = " + posterior.get(s).getCount() + ", sample = " + posterior.get(s).getSampleSize());
						testConvergence &= posterior.get(s).getCount() > EventHandler.SAMPLE_SIZE;
					}
					if(testConvergence) {
						for(State s: posterior.keySet()) {
							log.debug("[Monitor] PDF = " + posterior.get(s).pdf());
							posterior.get(s).resetCount();
							convergence &= posterior.get(s).convergence();
						}
						if(EventHandler.TERMINATION_CONDITION == Termination.CONVERGENCE && convergence) {
							log.debug("[Monitor] convergence reached.");
							addEvent(Event.stopEvent());
						}
					}
				}

				// coverage info and termination
				coverageInfo.addExecution(stateIndex.get(currentState), new StringAction(a.getAct().getName()));
				int tests = 0;
				for(State s: posterior.keySet()) {
					tests += posterior.get(s).getSampleSize();
					decisionMaker.updateDistance(stateIndex.get(s), posterior.get(s).getDistance());
				}
				if(tests % EventHandler.SAMPLE_SIZE >= EventHandler.SAMPLE_SIZE-1) {
					//log.info(coverageInfo.toString());
					if(EventHandler.TERMINATION_CONDITION == Termination.COVERAGE && coverageInfo.getCoverage() >= EventHandler.COVERAGE) {
						log.info("[Monitor] convergence reached.");
						addEvent(Event.stopEvent());
					}
//					else if(EventHandler.TERMINATION_CONDITION == Termination.LIMIT && tests >= EventHandler.LIMIT-1) {
//						log.info("[Monitor] #test limit reached.");
//						addEvent(Event.stopEvent());
//					}
					if(EventHandler.TERMINATION_CONDITION == Termination.BOUNDS) {
						log.info("[Monitor] BOUNDS termination checking.");
						List<HyperRectangle> toRemove = new ArrayList<>();
						for (HyperRectangle rect: hyperRectangles) {
							boolean contains = true;
							boolean disjoint = false;
							for(State s: posterior.keySet()) {
								double[][] region = posterior.get(s).hpdRegion(0.95);
								contains &= rect.contains(s, region);
								disjoint |= rect.disjoint(s, region);
							}
							if (contains) {
								log.info("[Monitor] All HDR inside bounds: requirements OK.");
								addEvent(Event.stopEvent());
							}
							else if (disjoint){
								toRemove.add(rect);
								//log.info("[Monitor] Disjoint HDR found: requirements VIOLATED.");
								//addEvent(Event.stopEvent());
							}
						}
						for (HyperRectangle rect: toRemove)
							hyperRectangles.remove(rect);
						log.info("[Monitor] Remaining hyper-rectangles: " + hyperRectangles.size());
						if (hyperRectangles.isEmpty()) {
							log.info("[Monitor] Disjoint HDR found: requirements VIOLATED.");
							addEvent(Event.stopEvent());
						}
					}
				}
				if((EventHandler.TERMINATION_CONDITION == Termination.LIMIT || EventHandler.TERMINATION_CONDITION == Termination.BOUNDS)
						&& eventCount >= EventHandler.LIMIT-1) {
					log.info("[Monitor] #test limit reached.");
					addEvent(Event.stopEvent());
				}

				// update state
				currentState = a.getDst();
				log.debug("Set current state: " + currentState.getName());
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

	public void report() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.warn("********* Monitor report *********");
		log.warn("Total #tests:" + eventCount);
		log.warn("Uncertain MDP parameters:");
		List<State> uStates = new ArrayList<>(prior.keySet());
		Collections.sort(uStates, (State s1, State s2) -> new Integer(s1.getName().substring(1)).compareTo(new Integer(s2.getName().substring(1))) );
		for(State s: uStates) {
			log.warn(s.getName() + ":=");
			log.warn("    Action: " + prior.get(s).action());
			log.warn("    Prior: " + prior.get(s).printParams() + " --> Posterior: " + posterior.get(s).printParams());
			log.warn("    #test: " + posterior.get(s).getSampleSize());
			//System.out.println("    Pr(D|M): " + posterior.get(s).pdf());
			log.warn("    Mode x_i: " + posterior.get(s).printMode());
			log.warn("    Mean E[x_i]: " + posterior.get(s).printMean());
			log.warn("    95% HPD region: " + Arrays.deepToString(posterior.get(s).hpdRegion(0.95)));
			log.warn("    HPD region size: " + posterior.get(s).getDistance());
		}
	}

	public static class CheckPoint {

		private Thread waitingThread = null;
		private static CheckPoint instance = null;
		private String state = null;

		private CheckPoint() { }

		public static CheckPoint getInstance() {
			if(instance == null) {
				synchronized (CheckPoint.class) {
					if(instance == null)
						instance = new CheckPoint();
				}
			}
			return instance;
		}

		public synchronized void join(Thread executingThread, String state) {
			this.state = state;
			if(waitingThread != null) {
				waitingThread = null;
				notifyAll();
			}
			else {
				waitingThread = executingThread;
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public synchronized String join(Thread executingThread) {
			if(waitingThread != null) {
				waitingThread = null;
				notifyAll();
			}
			else {
				waitingThread = executingThread;
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return state;
		}
	}
}
