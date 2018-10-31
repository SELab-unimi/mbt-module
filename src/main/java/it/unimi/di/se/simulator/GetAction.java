package it.unimi.di.se.simulator;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

public class GetAction implements WebAppAction {
	
	private WebDriver driver = null;
	private long executionTime = 0;
	
	private static int DEFAULT_TIMEOUT = 10000;

	public GetAction(WebDriver driver) {
		this.driver = driver;
	}

	/**
	 * args[0] is the url
	 * args[1] is the timeout in milliseconds
	 */
	@Override
	public boolean executeAction(String... args) {
		if(args.length == 0)
			return false;
		
		int timeOut = DEFAULT_TIMEOUT;
		if(args.length > 1)
			timeOut = Integer.parseInt(args[1]);
		driver.manage().timeouts().pageLoadTimeout(timeOut, TimeUnit.MILLISECONDS);
		try {
			driver.get(args[0]);
			executionTime = (Long)((JavascriptExecutor)driver).
					executeScript("return performance.timing.loadEventEnd - performance.timing.navigationStart;");
			return true;
		} catch (TimeoutException te){
			executionTime = timeOut;
			return false;
		}
	}

	@Override
	public long executionTime() {
		return executionTime;
	}
	
}
