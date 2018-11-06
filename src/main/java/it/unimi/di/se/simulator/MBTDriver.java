package it.unimi.di.se.simulator;

import jmarkov.basic.Actions;
import jmarkov.basic.States;
import jmarkov.jmdp.CharAction;
import jmarkov.jmdp.IntegerState;
import jmarkov.jmdp.SimpleMDP;
import picocli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unimi.di.se.mdp.mdpDsl.State;
import it.unimi.di.se.monitor.EventHandler;
import it.unimi.di.se.monitor.Monitor;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static picocli.CommandLine.usage;


public class MBTDriver {

    private static Logger logger = LoggerFactory.getLogger(MBTDriver.class);
    private static final char WAIT_ACTION = 'w';
    public static final double VERSION = 1.0;

    private int limit = 1;
    private SimpleMDP mdp = null;
    private IntegerState currentState = null;
    private Monitor monitor = null;
    
    private Comparator<IntegerState> stateComparator = new IntegerStateComparator();
    
    private WebAppAPI webAPI = null;

    public MBTDriver(Reader input, int limit) {
        this.limit = limit;
        mdp = new SimpleMDP(input);
        monitor = Monitor.getInstance();
        webAPI = new WebAppAPI();
        monitor.setWebAppAPI(webAPI);
    }

    public void start() {
        int times = 0;
        while(!goal(times)) {
            logger.info("new run");
            resetDriver();
            while(!mdp.isAbsorbing(currentState)) {
                logger.info("current state: " + currentState.label());
                if(isObservable(currentState)) {
                    doAction(currentState, WAIT_ACTION);
                    currentState = new IntegerState(getStateId(monitor.getCurrentState()));
                } 
                else {
                    char action = waitForAction(mdp.feasibleActions(currentState), System.in);
                    doAction(currentState, action);
                    currentState = new IntegerState(getStateId(monitor.getCurrentState()));
                }
            }
            logger.info("absorbing state reached: " + currentState.label());
            times++;
        }
    }
    
    private int getStateId(State s) {
    		return Integer.parseInt(s.getName().substring(1, s.getName().length()));
    }

    private boolean isObservable(IntegerState state) {
        Actions<CharAction> actions = mdp.feasibleActions(currentState);
        return actions.size() == 1 && actions.iterator().next().actionLabel() == WAIT_ACTION;
    }
    
    public WebAppAction doAction(IntegerState sourceState, char action) {
        Map.Entry<String, String[]> actionEntry = EventHandler.actionMap.get(action);
        WebAppAction webAction = webAPI.getAction(actionEntry.getKey());
        webAction.executeAction(actionEntry.getValue());
        return webAction;
    }
    
    private List<IntegerState> orderedReachableStates(States<IntegerState> reachableSet) {
        Iterator<IntegerState> iterator = reachableSet.iterator();
        List<IntegerState> orderedStates = new ArrayList<>();
        while (iterator.hasNext())
            orderedStates.add(iterator.next());
        Collections.sort(orderedStates, stateComparator);
        return orderedStates;
    }

    private char waitForAction(Actions<CharAction> actions, InputStream stream) {
    		StringBuilder availableActions = new StringBuilder("Available actions: { ");
        Iterator<CharAction> iterator = actions.iterator();
        boolean firstIteration = true;
        while(iterator.hasNext()) {
            if(firstIteration)
                firstIteration = false;
            else
                availableActions.append(", ");
            availableActions.append(iterator.next().actionLabel());
        }
        //System.out.println(availableActions.append(" }"));
        while(true) {
            //System.out.print("Input: ");
            Scanner in = new Scanner(stream);
            String inputAction = in.next();

            iterator = actions.iterator();
            while(iterator.hasNext())
                if(iterator.next().actionLabel() == inputAction.charAt(0))
                    return inputAction.charAt(0);
        }
    }

    private boolean goal(int times) {
        return times >= limit;
    }
    
    public void resetDriver() {
    		webAPI.resetApp();
        currentState = mdp.getInitialState();
    }

    public static void main(String[] args) {

        @CommandLine.Command(name="MBTDriver", header="** MBT Driver HELP **")
        class MyCommandLine {
            @CommandLine.Option(names = {"-i", "--input"}, required=true, description = "MDP auto-generated description")
            String inputMDP;
            @CommandLine.Option(names = {"-l", "--limit"}, description = "Number of simulations")
            Integer limit = 1;
            @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help message")
            boolean usageHelpRequested;
            @CommandLine.Option(names = {"-v", "--version"}, versionHelp = true, description = "Display the version")
            boolean versionHelpRequested;
        }

        MyCommandLine command = new MyCommandLine();
        CommandLine parser = new CommandLine(command);


        logger.info("** MBT Driver **");

        try {
            parser.parse(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            command.usageHelpRequested = true;
        }

        if(command.usageHelpRequested) {
            usage(command, System.err);
            System.exit(1);
        }
        if(command.versionHelpRequested) {
            System.err.println("Version " + VERSION);
            System.exit(1);
        }

        try {
            parser.parse(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            command.usageHelpRequested = true;
        }

        logger.debug("");
        try {
            Long start = System.nanoTime();

            logger.trace("MDP initialization...");
            MBTDriver driver = new MBTDriver(
                    new InputStreamReader(new FileInputStream(command.inputMDP)),
                    command.limit);
            logger.trace("Done.");

            logger.trace("Start simulation...");
            driver.start();
            logger.trace("Done.");

            Long diffNanoseconds = System.nanoTime() - start;

            logger.info("Simulation terminated in: {}s {}ms.",
                    TimeUnit.NANOSECONDS.toSeconds(diffNanoseconds),
                    TimeUnit.NANOSECONDS.toMillis(diffNanoseconds) - TimeUnit.NANOSECONDS.toSeconds(diffNanoseconds) * 1000);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
