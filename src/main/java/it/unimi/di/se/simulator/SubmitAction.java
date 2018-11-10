package it.unimi.di.se.simulator;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SubmitAction extends WebAppAction {
	
	private long executionTime = 0;
	private boolean success = false;

	public SubmitAction(WebDriver driver) {
		super(driver);
	}

	/**
	 * args[0] name of the textField element
	 * args[1] input string to be put inside the textField
	 */
	@Override
	public boolean executeAction(String... args) {
		if(args.length < 3)
			return false;
		
		int timeOut = Integer.parseInt(args[1]);
		
		try {
			WebElement element = driver.findElement(By.name(args[0]));
			element.submit();
			new WebDriverWait(driver, timeOut).until(new ExpectedCondition<Boolean>() {
	            public Boolean apply(WebDriver d) {
	                return d.getPageSource().contains(args[2]);
	            }
	        });
			executionTime = (Long)((JavascriptExecutor)driver).
					executeScript("return performance.timing.loadEventEnd - performance.timing.navigationStart;");
			success = true;
		}
		catch(TimeoutException e){
			executionTime = DEFAULT_TIMEOUT;
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