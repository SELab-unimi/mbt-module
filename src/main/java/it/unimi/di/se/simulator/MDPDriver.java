package it.unimi.di.se.simulator;

import jmarkov.basic.Actions;
import jmarkov.jmdp.CharAction;
import jmarkov.jmdp.IntegerState;
import picocli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static picocli.CommandLine.usage;


public class MDPDriver {

    private static Logger logger = LoggerFactory.getLogger(MDPDriver.class);
    private static final char WAIT_ACTION = 'w';
    public static final double VERSION = 1.0;

    private int limit = 1;
    private MDPSimulator mdp = null;

    public MDPDriver(Reader input, int limit) {
        this.limit = limit;
        mdp = new MDPSimulator(input);
    }

    public void start() {
        int times = 0;
        while(!goal(times)) {
            logger.info("new run");
            mdp.resetSimulation();
            while(!mdp.isCurrentStateAbsorbing()) {
                logger.info("current state: " + mdp.getCurrentState().label());
                if(isObservable(mdp.getCurrentState()))
                    mdp.setCurrentState(mdp.doAction(mdp.getCurrentState(), WAIT_ACTION));
                else {
                    char action = waitForAction(mdp.getFeasibleActions(), System.in);
                    mdp.setCurrentState(mdp.doAction(mdp.getCurrentState(), action));
                }
            }
            logger.info("absorbing state reached: " + mdp.getCurrentState().label());
            times++;
        }
    }

    private boolean isObservable(IntegerState state) {
        Actions<CharAction> actions = mdp.getFeasibleActions();
        return actions.size() == 1 && actions.iterator().next().actionLabel() == WAIT_ACTION;
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

    public static void main(String[] args) {

        @CommandLine.Command(name="MDPDriver", header="** MDP Simulator HELP **")
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


        logger.info("** MDP Simulator **");

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
            MDPDriver simulator = new MDPDriver(
                    new InputStreamReader(new FileInputStream(command.inputMDP)),
                    command.limit);
            logger.trace("Done.");

            logger.trace("Start simulation...");
            simulator.start();
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
