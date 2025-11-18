package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.ExtentTest;
import org.testng.Assert;
import org.testng.Reporter;

public class Log {
    private static final Logger log = LogManager.getLogger(Log.class);

    /** Optional: ExtentTest instance for reporting to ExtentReports */
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    /**
     * Set ExtentTest instance for the current thread/test
     */
    public static void setExtentTest(ExtentTest test) {
        extentTest.set(test);
    }

    /**
     * Get current thread's ExtentTest
     */
    public static ExtentTest getTest() {
        return extentTest.get();
    }
    
    // ===== Generic logging helper =====
    /**
     * Generic logging helper
     * @param message
     * @param status
     * @param color
     * @param toConsole
     * @param toLog4j
     * @param toReporter
     */
	private static void logMessage(String message, Status status, ExtentColor color, boolean toConsole, boolean toLog4j, boolean toReporter) {

		// ===== Log4j =====
		if (toLog4j) {
			switch (status) {
			case PASS:
				log.info("[PASS] " + message);
				break;
			case FAIL:
				log.error("[FAIL] " + message);
				break;
			case WARNING:
				log.warn("[WARN] " + message);
				break;
			case INFO:
			default:
				log.info(message);
				break;
			}
		}

		// ===== ExtentReports =====
		ExtentTest test = getTest();
		if (test != null) {
			if (status == Status.INFO) {
				test.log(Status.INFO, message);
			} else {
				if (color != null) {
					test.log(status, MarkupHelper.createLabel(message, color));
				} else {
					test.log(status, message);
				}
			}
		}

		// ===== TestNG Reporter =====
		if (toReporter) {
			String html;
			switch (status) {
			case PASS:
				html = "<span style='color:green;'>" + message + "</span><br>";
				break;
			case FAIL:
				html = "<span style='color:red;'>" + message + "</span><br>";
				break;
			case WARNING:
				html = "<span style='color:orange;'>" + message + "</span><br>";
				break;
			case INFO:
			default:
				html = message + "<br>";
				break;
			}
			Reporter.log(html);
		}

		// ===== Console =====
		if (toConsole) {
			String ansiColor = "\u001B[0m"; // default (no color)
		    if (status == Status.FAIL) {
		        ansiColor = "\u001B[31m"; // red
		    } else if (status == Status.WARNING) {
		        ansiColor = "\u001B[33m"; // yellow
		    } else if (status == Status.PASS && color != null) {
		        ansiColor = "\u001B[32m"; // green only if color is provided
		    }
		    System.out.println(ansiColor + message + "\u001B[0m");
		}
	}

    /**
     * To print the given message
     * 
     * @param message
     * 		- The text message to log
     */
    public static void message(String message) {
    	logMessage(message, Status.INFO, null, true, true, true);
    }

    /**
     * To log a debug event
     * 
     * @param message
     * 		- The debug message to log
     */
    public static void event(String message) {
    	logMessage(message, Status.INFO, null, false, true, false);
    }

    /**
     * To logs an error-level event message to Log4j.
     * 
     * @param message 
     * 		- The error message to log
     */
    public static void errorEvent(String message) {
    	logMessage(message, Status.FAIL, ExtentColor.RED, true, true, true);
    }
    
    /**
     * To logs an error-level event message
     * 
     * @param message
     * 		- message to print
     * @param throwable
     */
    public static void errorEvent(String message, Throwable throwable) {
    	log.error(message, throwable);
        ExtentTest test = getTest();
        if (test != null) test.fail(throwable);
        logMessage(message, Status.FAIL, ExtentColor.RED, true, false, true);
    }
    
    /**
     * To pass the log with green highlight
     * 
     * @param message
     * 		- message to print
     */
    public static void pass(String message) {
    	logMessage(message, Status.PASS, ExtentColor.GREEN, true, true, true);
    }

    /**
     * To logs a failure message and immediately fails the test.
     * 
     * @param message 
     * 		- the failure message to log and report
     */
    public static void fail(String message) {
    	logMessage(message, Status.FAIL, ExtentColor.RED, true, true, true);
        throw new AssertionError(message); 
    }

    /**
     * To logs a warning message to console, Log4j, and ExtentReports.
     * 
     * @param message 
     * 		- The warning message to log
     */
    public static void warnEvent(String message) {
    	logMessage(message, Status.WARNING, ExtentColor.ORANGE, true, true, true);
    }

    // ===== Assertions with Logging =====

    /**
     * To asserts a boolean condition and logs the result.
     *
     * @param condition   
     * 		- The boolean condition to check. If true, the test passes; otherwise, it fails.
     * @param passMessage 
     * 		- The message to log if the condition is true.
     * @param failMessage 
     * 		- The message to log and fail the test if the condition is false.
     */
    public static void assertThat(boolean condition, String passMessage, String failMessage) {
    	
        if (condition) {
        	logMessage(passMessage, Status.PASS, null, true, true, true);
            Assert.assertTrue(true, passMessage);
        } else {
        	logMessage(failMessage, Status.FAIL, ExtentColor.RED, true, true, true);
            Assert.fail(failMessage);
        }
    }

    /**
     * To asserts that a boolean condition is true and logs the result.
     * 
     * @param condition 
     * 		- The boolean condition expected to be true
     * @param message   
     * 		- The message describing the assertion
     */
    public static void assertTrue(boolean condition, String message) {
        assertThat(condition, message, "Assertion failed: " + message);
    }

    /**
     * To asserts that a boolean condition is false and logs the result.
     * 
     * @param condition 
     * 		- The boolean condition expected to be false
     * @param message   
     * 		- The message describing the assertion
     */
    public static void assertFalse(boolean condition, String message) {
        assertThat(!condition, message, "Assertion failed: " + message);
    }

    /**
     * To asserts that two objects are equal and logs the result.
     * 
     * @param actual   
     * 		- The actual object value
     * @param expected 
     * 		- The expected object value
     * @param message  
     * 		- The message describing the assertion
     */
    public static void assertEquals(Object actual, Object expected, String message) {
    	
    	String fullMessage = message + " | Expected = " + expected + ", Actual = " + actual;
        if ((actual == null && expected == null) || (actual != null && actual.equals(expected))) {
        	logMessage(fullMessage, Status.PASS, null, true, true, true);
            Assert.assertEquals(actual, expected, message);
        } else {
        	 logMessage(fullMessage, Status.FAIL, ExtentColor.RED, true, true, true);
            Assert.fail(fullMessage);
        }
    }
}
