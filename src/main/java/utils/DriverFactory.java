package utils;

import java.util.*;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverFactory {

	/**
	 * To creates WebDriver with desktop or mobile viewport
	 * 
	 * @param deviceName "desktop" or "mobile"
	 * @return WebDriver instance
	 */
	public static WebDriver createDriver(String deviceName) {
		ChromeOptions options = new ChromeOptions();

		switch (deviceName.toLowerCase()) {
		case "desktop":
			WebDriver driver = new ChromeDriver(options);
			driver.manage().window().maximize();
			return driver;
		case "iphone12pro":
			Map<String, Object> iphone = new HashMap<>();
			iphone.put("deviceName", "iPhone 12 Pro");
			options.setExperimentalOption("mobileEmulation", iphone);
			return new ChromeDriver(options);
		case "ipad":
			Map<String, Object> ipad = new HashMap<>();
			ipad.put("deviceName", "iPad");
			options.setExperimentalOption("mobileEmulation", ipad);
			return new ChromeDriver(options);
		case "android":
			Map<String, Object> android = new HashMap<>();
			android.put("deviceName", "Pixel 5"); // choose any Pixel/Android device
			options.setExperimentalOption("mobileEmulation", android);
			return new ChromeDriver(options);
		default:
			throw new IllegalArgumentException("Unknown device: " + deviceName);
		}
	}
}
