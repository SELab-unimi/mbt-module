package it.unimi.di.se.simulator;

import org.openqa.selenium.WebDriver;

public abstract class WebAppAction {
	
	protected WebDriver driver = null;
	protected static int DEFAULT_TIMEOUT = 10000;
	
	public WebAppAction(WebDriver driver) {
		this.driver = driver;
	}
	
	public abstract boolean executeAction(String...args);
	public abstract long executionTime();
	public abstract boolean success();
}
