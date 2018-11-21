package it.unimi.di.se.simulator;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WebAppAPI {
	
	private Map<String, WebAppAction> actions = new HashMap<>();
	WebDriver driver = null;
	private WebAppAction logout = null;
	
	public WebAppAPI() {
		System.setProperty("webdriver.gecko.driver", "src/main/resources/geckodriver");
		System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE,"true");
		System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"/dev/null");
		driver = new FirefoxDriver();
		
		actions.put("GET", new GetAction(driver));
		actions.put("CLICK", new ClickAction(driver));
		actions.put("TEXT", new TextFieldAction(driver));
		actions.put("SUBMIT", new SubmitAction(driver));
		
		logout = new LogoutAction(driver, actions.get("CLICK"));
	}
	
	public WebAppAction getAction(String actionKey) {
		return actions.get(actionKey);
	}
	
	public void shutDown() {
		driver.quit();
	}
	
	public void resetApp() {
		driver.manage().deleteAllCookies();
		logout.executeAction("show-usr", "1", "logout", "logout", "1", "open-login");
		actions.get("GET").executeAction("http://127.0.0.1:8000/index.html", "5000");
	}

}
