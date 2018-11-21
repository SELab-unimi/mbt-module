package it.unimi.di.se.simulator;

import java.util.Arrays;

import org.openqa.selenium.WebDriver;

public class LogoutAction extends WebAppAction {
	
	private WebAppAction clickAction = null;
	
	private long executionTime = 0;
	private boolean success = false;

	public LogoutAction(WebDriver driver, WebAppAction click) {
		super(driver);
		clickAction = click;
	}

	@Override
	public boolean executeAction(String... args) {
		success = clickAction.executeAction(Arrays.copyOfRange(args, 0, 3));
		executionTime = clickAction.executionTime();
		success &= clickAction.executeAction(Arrays.copyOfRange(args, 3, 6));
		executionTime += clickAction.executionTime();
		return success;
	}

	@Override
	public long executionTime() {
		return executionTime;
	}

	@Override
	public boolean success() {
		return success;
	}

}
