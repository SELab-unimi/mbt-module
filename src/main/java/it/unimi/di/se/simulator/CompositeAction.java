package it.unimi.di.se.simulator;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;

public class CompositeAction extends WebAppAction {
	
	private long executionTime = 0;
	private boolean success = false;
	
	private List<WebAppAction> actions = new ArrayList<>();
	private List<String[]> args = new ArrayList<>();
	
	public void addAction(WebAppAction a, String... currentArgs) {
		actions.add(a);
		args.add(currentArgs);
	}

	public CompositeAction(WebDriver driver) {
		super(driver);
	}

	@Override
	public boolean executeAction(String... currentArgs) {
		executionTime = 0;
		success = true;
		for(int i=0; i<actions.size(); i++) {
			success &= actions.get(i).executeAction(args.get(i));
			executionTime += actions.get(i).executionTime();
		}
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
