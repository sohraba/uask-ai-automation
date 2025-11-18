package utils;

public class TestData {
	private String id;
	private String lang;
	private String input;
	private String expected;
	private String actual;
	private String expectedFallback;
	private double threshold;

	// getters and setters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getExpected() {
		return expected;
	}

	public void setExpected(String expected) {
		this.expected = expected;
	}
	
	public String getActual() {
		return actual;
	}

	public void setActual(String actual) {
		this.actual = actual;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	
	public String getExpectedFallback() {
		return expectedFallback;
	}

	public void setExpectedFallBack(String expectedFallback) {
		this.expectedFallback = expectedFallback;
	}
}
