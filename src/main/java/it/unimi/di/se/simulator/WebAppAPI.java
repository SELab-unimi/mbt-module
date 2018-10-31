package it.unimi.di.se.simulator;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WebAppAPI {
	
	private Map<String, WebAppAction> actions = new HashMap<>();
	WebDriver driver = null;
	
	public WebAppAPI() {
		System.setProperty("webdriver.gecko.driver", "src/main/resources/geckodriver");
		driver = new FirefoxDriver();
		actions.put("GET", new GetAction(driver));
	}
	
	public WebAppAction getAction(String actionKey) {
		return actions.get(actionKey);
	}
	
	public void shutDown() {
		driver.quit();
	}

}
