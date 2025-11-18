package utils;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.*;

public class UAskUtils {

	private static final int DEFAULT_WAIT = 10;   // max wait time
    private static final int POLLING_INTERVAL = 500; // ms
    
    protected WebDriver driver;
    
    /**
     * Generic wait using FluentWait until the given condition returns true
     * 
     * @param driver
     * 		- WebDriver instance
     * @param condition
     * 		- Lambda that returns boolean (page or element ready)
     */
    public static void waitForPageLoad(WebDriver driver, Function<WebDriver, Boolean> condition) {
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(DEFAULT_WAIT))
                .pollingEvery(Duration.ofMillis(POLLING_INTERVAL))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);

        wait.until(condition);
    }
    
    /**
     * To wait for the specific element on the page
     * 
     * @param driver 
     * 		- WebDriver instance
     * @param element 
     * 		- webelement to wait for to appear
     * @return boolean 
     * 		- return true if element is present else return false
     */
    public static boolean waitForElement(WebDriver driver, WebElement element) {
        return waitForElement(driver, element, Duration.ofSeconds(DEFAULT_WAIT));
    }
    
    /**
     * To wait for the specific element on the page
     * 
     * @param driver
     * 		- WebDriver instance
     * @param element
     * 		- webelement to wait for to appear
     * 		- how long to wait for
     * @return boolean 
     * 		- return true if element is present else return false
     */
    public static boolean waitForElement(WebDriver driver, WebElement element, Duration timeout) {
        try {
            new WebDriverWait(driver, timeout).until(ExpectedConditions.visibilityOf(element));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * To wait for the specific list elements on the page
     * 
     * @param driver 
     * 		-
     * @param elements 
     * 		- List elements to wait for to appear
     * @param maxWait 
     * 		- how long to wait for
     * @return boolean 
     * 		- return true if element is present else return false
     */
    
    public static boolean waitForListElement(WebDriver driver, List<WebElement> elements, int maxWait) {
		boolean statusOfElementToBeReturned = false;
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(maxWait));
		try {
			wait.until(ExpectedConditions.visibilityOfAllElements(elements));
			statusOfElementToBeReturned = true;
		} catch (Exception ex) {
			statusOfElementToBeReturned = false;
        }
        return statusOfElementToBeReturned;
    }
    
    /**
     * To check whether scrollbar is displayed for an element
     * 
     * @param driver
     * @param element
     * @return boolean
     * 		   true if scrollbar is displayed
     */
    public static boolean verifyScrollExistForElement(final WebDriver driver, WebElement element){
 		JavascriptExecutor js = (JavascriptExecutor) driver;
 		double clientHeight = Double.valueOf(js.executeScript("return arguments[0].clientHeight;", element).toString());
 		double scrollHeight = Double.valueOf(js.executeScript("return arguments[0].scrollHeight;", element).toString());
 		return clientHeight < scrollHeight;
    }
    
    /**
     * To wait until the given element disappears from the page
     *
     * @param driver  
     * 		-	WebDriver instance
     * @param element 
     * 		- WebElement to wait for disappearance
     * @param timeout
     * 		- Maximum wait time in seconds
     * @return 
     * 		- true if element disappears within timeout, false otherwise
     */
    public static boolean waitForElementToDisappear(WebDriver driver, WebElement element, int timeout) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            return wait.until(ExpectedConditions.invisibilityOf(element));
        } catch (Exception e) {
            System.out.println("Element did not disappear: " + element);
            return false;
        }
    }
}
