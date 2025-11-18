package base;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.*;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import utils.ChatReportUtils;
import utils.ConfigReader;
import utils.DriverFactory;
import utils.Log;

@Listeners(utils.TestListener.class)
public class BaseTest {

    protected WebDriver driver;
    protected static ExtentReports extent;
    protected static final Logger log = LogManager.getLogger(BaseTest.class);

    // Thread-safe ExtentTest for parallel execution
    protected static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    
    // Parent test for each test class
    protected ExtentTest parentTest;

    @BeforeSuite(alwaysRun = true)
    public void setupReport() {
        ExtentSparkReporter spark = new ExtentSparkReporter("test-output/ExtentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);
    }
    
    @AfterSuite(alwaysRun = true)
    public void tearDownReport() {
        if (extent != null) {
            extent.flush();
        }
    }
    
    @BeforeClass(alwaysRun = true)
    public void setupClass() {
        parentTest = extent.createTest(getClass().getSimpleName());
    }
    
    @BeforeMethod(alwaysRun = true)
    @Parameters("device")
    public void setup(Method method, @Optional("desktop") String device) {
        // Decide device type
    	String deviceToUse = device != null ? device.toLowerCase() : "desktop";
    	try {
            driver = DriverFactory.createDriver(deviceToUse);
        } catch (IllegalArgumentException e) {
            log.warn("Unknown device '" + deviceToUse + "'. Falling back to desktop.");
            driver = DriverFactory.createDriver("desktop");
        }

        // Launch app
        driver.get(ConfigReader.get("url"));

        // Create ExtentTest for this thread
        ExtentTest methodTest = parentTest.createNode(method.getName());
        extentTest.set(methodTest);
        Log.setExtentTest(methodTest);

        log.info("Browser launched and navigated to U-Ask application");
        Log.message("Starting test: " + method.getName());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        try {
            ExtentTest test = extentTest.get();

            // Capture screenshot on failure
             if (result.getStatus() == ITestResult.FAILURE && driver != null) {
                takeScreenshot(result.getName());
            } else if (result.getStatus() == ITestResult.SKIP) {
                if (test != null) {
                    test.skip("Test skipped: " + result.getThrowable());
                }
            }
        
            // Attach AI Test Result HTML table if any
            String htmlTable = ChatReportUtils.getHtmlTable();
            if (htmlTable != null && !htmlTable.isEmpty()) {
                if (test != null) {
                    test.info("Test Result Table:<br>" + htmlTable);
                }
                Reporter.log("<br><b>Test Result Table:</b><br>" + htmlTable + "<br>", true);
            }

        } catch (Exception e) {
            log.error("Error in AfterMethod: " + e.getMessage(), e);
        } finally {
            // Close browser
            if (driver != null) {
                driver.quit();
                log.info("Browser closed for test: " + result.getName());
            }

            // Clear AI test rows for next test
            ChatReportUtils.resetHtmlTable();

            // Flush ExtentReport after each test
           if (extent != null) {
                extent.flush();
            }
        }
    }
    
    // Screenshot Utility
    public void takeScreenshot(String name) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File src = ts.getScreenshotAs(OutputType.FILE);
            File dest = new File("screenshots/" + name + ".png");
            dest.getParentFile().mkdirs();
            FileUtils.copyFile(src, dest);
            log.info("Screenshot taken: " + dest.getAbsolutePath());

            ExtentTest test = extentTest.get();
            if (test != null) test.addScreenCaptureFromPath(dest.getAbsolutePath());
        } catch (IOException e) {
            log.error("Error taking screenshot: " + e.getMessage(), e);
        }
    }
}
