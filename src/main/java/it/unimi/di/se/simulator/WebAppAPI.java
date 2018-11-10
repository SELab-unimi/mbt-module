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
		actions.put("CLICK", new ClickAction(driver));
		actions.put("TEXT", new TextFieldAction(driver));
		actions.put("SUBMIT", new SubmitAction(driver));
	}
	
	public WebAppAction getAction(String actionKey) {
		return actions.get(actionKey);
	}
	
	public void shutDown() {
		driver.quit();
	}
	
	public void resetApp() {
		driver.manage().deleteAllCookies();
		actions.get("GET").executeAction("http://127.0.0.1:8000/index.html", "5000");
	}

}
