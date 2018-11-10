package it.unimi.di.se.simulator;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ClickAction extends WebAppAction {
	
	private long executionTime = 0;
	private boolean success = false;

	public ClickAction(WebDriver driver) {
		super(driver);
	}

	/**
	 * args[0] id of the clickable element
	 * args[1] is the timeout in seconds
	 * args[2] element to be loaded after click
	 */
	@Override
	public boolean executeAction(String... args) {
		if(args.length == 0)
			return false;
		
		int timeOut = DEFAULT_TIMEOUT;
		if(args.length > 1)
			timeOut = Integer.parseInt(args[1]);
		
		try {
			WebElement element = driver.findElement(By.className(args[0])); // TODO to be changed to find-by-id
			element.click();
			if(args.length > 2)
				new WebDriverWait(driver, timeOut)
					.until(new ExpectedCondition<Boolean>() {
				        @Override
				        public Boolean apply(WebDriver d) {
				        		return d.findElement(By.id(args[2])).isDisplayed();
				        }
					});
			success = true;
		} catch (TimeoutException | NoSuchElementException e){
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
