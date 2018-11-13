package it.unimi.di.se.simulator;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TextFieldAction extends WebAppAction {
	
	private long executionTime = 0;
	private boolean success = false;

	public TextFieldAction(WebDriver driver) {
		super(driver);
	}

	/**
	 * args[0] id of the textField element
	 * args[1] input string to be put inside the textField
	 */
	@Override
	public boolean executeAction(String... args) {
		if(args.length < 2)
			return false;
		
		try {
			WebElement element = driver.findElement(By.id(args[0]));
			element.sendKeys(args[1]);
			success = true;
		}
		catch(NoSuchElementException e){
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
