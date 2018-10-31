package it.unimi.di.se.simulator;

import org.openqa.selenium.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

public class GetAction implements WebAppAction {
	
	private WebDriver driver = null;
	private long executionTime = 0;
	private boolean success = false;
	
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
			
			success = !driver.getPageSource().contains("ERROR IN GET ELEMENT");
		} catch (TimeoutException te){
			executionTime = timeOut;
			success = false;
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
