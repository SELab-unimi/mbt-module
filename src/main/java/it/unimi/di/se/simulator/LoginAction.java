package it.unimi.di.se.simulator;

import java.util.Arrays;

import org.openqa.selenium.WebDriver;

public class LoginAction extends WebAppAction {
	
	private WebAppAction clickAction = null;
	private WebAppAction textFieldAction = null;
	private WebAppAction submitAction = null;
	
	private long executionTime = 0;
	private boolean success = false;

	public LoginAction(WebDriver driver, WebAppAction click, WebAppAction fill, WebAppAction submit) {
		super(driver);
		clickAction = click;
		textFieldAction = fill;
		submitAction = submit;
	}

	@Override
	public boolean executeAction(String... args) {
		success = clickAction.executeAction(Arrays.copyOfRange(args, 0, 2));
		executionTime = clickAction.executionTime();
		success &= textFieldAction.executeAction(Arrays.copyOfRange(args, 3, 5));
		executionTime += textFieldAction.executionTime();
		success &= textFieldAction.executeAction(Arrays.copyOfRange(args, 6, 8));
		executionTime += textFieldAction.executionTime();
		success &= submitAction.executeAction(Arrays.copyOfRange(args, 9, 11));
		executionTime += submitAction.executionTime();
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
