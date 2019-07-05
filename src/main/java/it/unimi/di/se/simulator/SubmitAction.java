package it.unimi.di.se.simulator;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptException;
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
	 * args[0] id of the textField element
	 * args[1] input string to be put inside the textField
	 * args[2] TODO id of the element to be loaded after click (currently it looks for text)
	 */
	@Override
	public boolean executeAction(String... args) {
		if(args.length == 0)
			return false;
		
		int timeOut = DEFAULT_TIMEOUT;
		if(args.length > 1)
			timeOut = Integer.parseInt(args[1]);
		
		try {
			WebElement element = driver.findElement(By.id(args[0]));
			element.submit();
			if(args.length > 2)
				new WebDriverWait(driver, timeOut).until(new ExpectedCondition<Boolean>() {
		            @Override
					public Boolean apply(WebDriver d) {
		            		// return d.findElement(By.id(args[2])).isDisplayed();
		            		return d.getPageSource().contains(args[2]);
		            }
		        });
			executionTime = (Long)((JavascriptExecutor)driver).
					executeScript("return performance.timing.loadEventEnd - performance.timing.navigationStart;");
			success = true;
		}
		catch(TimeoutException | JavascriptException e){
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