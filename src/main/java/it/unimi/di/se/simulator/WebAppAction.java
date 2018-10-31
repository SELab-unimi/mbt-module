package it.unimi.di.se.simulator;

public interface WebAppAction {
	boolean executeAction(String...args);
	long executionTime();
}
