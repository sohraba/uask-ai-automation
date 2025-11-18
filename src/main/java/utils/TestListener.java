package utils;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {
	
    @Override
    public void onTestSuccess(ITestResult result) {
        Log.pass("Test Passed"); // green in ExtentReport + console
    }

    @Override
    public void onTestFailure(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		Throwable throwable = result.getThrowable();

		System.out.println("Test FAILED: " + testName);
		if (throwable != null) {
			String reason = throwable.toString().split("\n")[0];
			System.out.println("   Reason: " + reason);
		}
	}

    @Override
    public void onTestSkipped(ITestResult result) {
        Log.warnEvent("Test Skipped");
    }

    // Keeping other methods empty
    @Override public void onTestStart(ITestResult result) {}
    @Override public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}
    @Override public void onTestFailedWithTimeout(ITestResult result) {}
    @Override public void onStart(ITestContext context) {}
    @Override public void onFinish(ITestContext context) {}
}
