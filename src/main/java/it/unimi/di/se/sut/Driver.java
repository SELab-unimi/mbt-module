package it.unimi.di.se.sut;

import jmarkov.basic.Actions;
import jmarkov.jmdp.StringAction;
import jmarkov.jmdp.IntegerState;
import picocli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static picocli.CommandLine.usage;


public class Driver {

    private static Logger logger = LoggerFactory.getLogger(Driver.class);
    private static final String WAIT_ACTION = "w";
    public static final double VERSION = 1.0;

    private int limit = 1;
    private MDPExecutor mdp = null;

    public Driver(Reader input, int limit) {
        this.limit = limit;
        mdp = new MDPExecutor(input);
    }

    public void start() {
        int times = 0;
        while(!goal(times)) {
            logger.trace("new run");
            mdp.resetExecution();
            while(!mdp.isCurrentStateAbsorbing()) {
                logger.trace("current state: " + mdp.getCurrentState().label());
                if(isObservable(mdp.getCurrentState()))
                    mdp.setCurrentState(mdp.doAction(mdp.getCurrentState(), WAIT_ACTION));
                else {
                    String action = waitForAction(mdp.getFeasibleActions(), System.in);
                    mdp.setCurrentState(mdp.doAction(mdp.getCurrentState(), action));
                }
            }
            logger.trace("absorbing state reached: " + mdp.getCurrentState().label());
            times++;
        }
    }

    private boolean isObservable(IntegerState state) {
        Actions<StringAction> actions = mdp.getFeasibleActions();
        return actions.size() == 1 && actions.iterator().next().actionLabel() == WAIT_ACTION;
    }

    private String waitForAction(Actions<StringAction> actions, InputStream stream) {
        StringBuilder availableActions = new StringBuilder("Available actions: { ");
        Iterator<StringAction> iterator = actions.iterator();
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
                if(iterator.next().actionLabel().equals(inputAction))
                    return inputAction;
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
            Integer limit = 1000000;
            @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help message")
            boolean usageHelpRequested;
            @CommandLine.Option(names = {"-v", "--version"}, versionHelp = true, description = "Display the version")
            boolean versionHelpRequested;
        }

        MyCommandLine command = new MyCommandLine();
        CommandLine parser = new CommandLine(command);


        logger.trace("** MDP Driver **");

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
            Driver simulator = new Driver(
                    new InputStreamReader(new FileInputStream(command.inputMDP)),
                    command.limit);
            logger.trace("Done.");

            logger.trace("Start simulation...");
            simulator.start();
            logger.trace("Done.");

            Long diffNanoseconds = System.nanoTime() - start;

            logger.trace("Driver terminated in: {}s {}ms.",
                    TimeUnit.NANOSECONDS.toSeconds(diffNanoseconds),
                    TimeUnit.NANOSECONDS.toMillis(diffNanoseconds) - TimeUnit.NANOSECONDS.toSeconds(diffNanoseconds) * 1000);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
