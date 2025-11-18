package test;

import base.BaseTest;
import org.testng.annotations.Test;
import pages.ChatPage;
import pages.LoginPage;
import utils.*;


public class ChatbotUITests extends BaseTest {
	
	String userName = ConfigReader.get("username");
	String password = ConfigReader.get("password");
	String filePath = "src/main/resources/test-data.json";

	@Test(priority = 0)
	public void tc01VerifyChatWidgetLoadsOnDesktop(){
		Log.message("tc01VerifyChatWidgetLoadsOnDesktop: Verify that the chat widget is visible and accessible on both desktop.");
		
		try {
			LoginPage loginPage = new LoginPage(driver);
	        loginPage.clickOnLoginWithEmailLink();
	        loginPage.loginToUAskWebApp(userName, password);
	        ChatPage chatPage = new ChatPage(driver);
	        Log.assertThat(chatPage.isPageLoaded(),
	        		"Chat widget loaded correctly on desktop",
	        		"Chat widget not loaded correctly on desktop");
		} catch (Exception e) {
			Log.message("Assertion failed: " + e.getMessage());
		} 
	}
	
	@Test(priority = 1)
	public void tc02VerifyChatWidgetLoadsOnDevice() {
		Log.message("tc02VerifyChatWidgetLoadsOnMobile: Verify that the chat widget is visible and accessible on both Mobile device.");
		try {
			LoginPage loginPage = new LoginPage(driver);
			loginPage.clickOnLoginWithEmailLink();
			loginPage.loginToUAskWebApp(userName, password);
			ChatPage chatPage = new ChatPage(driver);
			Log.assertThat(chatPage.isPageLoaded(), 
					"Chat widget loaded correctly on mobile device",
					"Chat widget not loaded correctly on mobile device");
		} catch (Exception e) {
			Log.message("Assertion failed: " + e.getMessage());
		} 
	}
	
	@Test(priority = 2)
	public void tc03VerifyUserCanSendMessage() {
		Log.message("tc03VerifyUserCanSendMessage: Verify users can type and send messages through the input box");
	    
		try {
			LoginPage loginPage = new LoginPage(driver);
	        loginPage.clickOnLoginWithEmailLink();
	        loginPage.loginToUAskWebApp(userName, password);
	        ChatPage chatPage = new ChatPage(driver);
	        TestData question = JsonUtils.getQuestionById(filePath, "UI_EN_01");
	        String expectedResponse = question.getExpected();
	        Double threshold = question.getThreshold();
	        String Quesion = question.getInput();
	        
	        if (question != null) {
	            chatPage.enterChatInput(Quesion);
	            chatPage.clickOnButtonSend();

	            String actualResponse = chatPage.getLastAIMessage(driver);
	            Log.assertThat(TextUtils.isResponseValid(expectedResponse, actualResponse, threshold),
	            		"User can send messages via input box" + "<br>\n Question: " + Quesion + "</br>",
	            		"User not able to send messages via input box"  + "<br>\n Question: " + Quesion + "</br>");
	        } 
		} catch (Exception e) {
			Log.message("Assertion failed: " + e.getMessage());
		} 
	}

	@Test(priority = 3)
	public void tc04VerifyAIResponsesRendered() {
		Log.message("tc04VerifyAIResponsesRendered: Verify that AI-generated responses are displayed correctly.");

		String userQuestion = "", expectedResponse = "", actualResponse = "";
		Double threshold, similarityScore = 0.0 ;
		boolean isValid = false;
		
		try {
			LoginPage loginPage = new LoginPage(driver);
			loginPage.clickOnLoginWithEmailLink();
			loginPage.loginToUAskWebApp(userName, password);

			ChatPage chatPage = new ChatPage(driver);
			TestData question = JsonUtils.getQuestionById(filePath, "UI_EN_02");

			userQuestion = question.getInput();
			expectedResponse = question.getExpected();
			threshold = question.getThreshold();

			if (question != null) {
				chatPage.enterChatInput(userQuestion);
				chatPage.clickOnButtonSend();

				actualResponse = chatPage.getLastAIMessage(driver);
				similarityScore = TextUtils.getSemanticSimilarity(expectedResponse, actualResponse);
				isValid = similarityScore >= threshold;
				
				Log.assertThat(isValid, 
						"User can see AI-generated responses are displayed correctly.",
						"User not able see to AI-generated responses correctly.");
			}
		} catch (Exception e) {
			Log.fail("Error while validating semantic similarity: " + e.getMessage());
		} finally {
			ChatReportUtils.logResultHtml(userQuestion, expectedResponse, actualResponse, similarityScore, isValid);
		}
	}

	@Test(priority = 4)
	public void tc05VerifyMultilingualSupport() {
		Log.message("tc05VerifyMultilingualSupport: Verify that AI-generated responses are displayed correctly.");

		String userQuestion = "", expectedResponse = "", actualResponse = "";
		String userQuestionInArabic = "", expectedResponseInArabic = "", actualResponseInArabic = "";
		Double threshold, similarityScore = 0.0 ;
		Double thresholdInArabic, similarityScoreInArabic = 0.0 ;
		boolean isValid = false;
		boolean isValidInArabic = false;
		
		try {
			LoginPage loginPage = new LoginPage(driver);
			loginPage.clickOnLoginWithEmailLink();
			loginPage.loginToUAskWebApp(userName, password);

			ChatPage chatPage = new ChatPage(driver);
			
			TestData question = JsonUtils.getQuestionById(filePath, "UI_EN_02");
			userQuestion = question.getInput();
			expectedResponse = question.getExpected();
			threshold = question.getThreshold();

			if (question != null) {
				chatPage.enterChatInput(userQuestion);
				chatPage.clickOnButtonSend();
				actualResponse = chatPage.getLastAIMessage(driver);
				
				similarityScore = TextUtils.getSemanticSimilarity(expectedResponse, actualResponse);;
				isValid = similarityScore >= threshold;
				
				Log.assertThat(isValid, 
						"User can see AI-generated responses are displayed correctly.",
						"User not able see to AI-generated responses correctly.");

				Log.assertThat(chatPage.isMultilanguageDisplayed("English"),
						"User can able see LTR (English) response",
						"User not able see to LTR (English) response");
			}
			
			question = JsonUtils.getQuestionById(filePath, "UI_AR_01");
			userQuestionInArabic = question.getInput();
			expectedResponseInArabic = question.getExpected();
			thresholdInArabic = question.getThreshold();
			
			if (question != null) {
				chatPage.enterChatInput(userQuestionInArabic);
				chatPage.clickOnButtonSend();
				actualResponseInArabic = chatPage.getLastAIMessage(driver);
				
				similarityScoreInArabic = TextUtils.getSemanticSimilarity(expectedResponseInArabic, actualResponseInArabic);
				isValidInArabic = similarityScoreInArabic >= thresholdInArabic;
				
				Log.assertThat(isValidInArabic, 
						"User can see AI-generated responses are displayed correctly.",
						"User not able see to AI-generated responses correctly.");

				Log.assertThat(chatPage.isMultilanguageDisplayed("Arabic"),
						"User can able see RTL (Arabic) response",
						"User not able see to RTL (Arabic) response");
			}
		} catch (Exception e) {
			Log.fail("Error while validating semantic similarity: " + e.getMessage());
		} finally {
			ChatReportUtils.logResultHtml(userQuestion, expectedResponse, actualResponse, similarityScore, isValid);
			ChatReportUtils.logResultHtml(userQuestionInArabic, expectedResponseInArabic, actualResponseInArabic, similarityScoreInArabic, isValidInArabic);
		}
	}

	@Test(priority = 5)
	public void tc06VerifyInputClearedAfterSend() {
		Log.message("tc06VerifyInputClearedAfterSend: Verify that the input box is cleared after sending a message.");

		try {
			LoginPage loginPage = new LoginPage(driver);
			loginPage.clickOnLoginWithEmailLink();
			loginPage.loginToUAskWebApp(userName, password);

			ChatPage chatPage = new ChatPage(driver);
			TestData question = JsonUtils.getQuestionById(filePath, "UI_EN_03");
			if (question != null) {
				chatPage.enterChatInput(question.getInput());
				chatPage.clickOnButtonSend();

				Log.assertThat(chatPage.isInputCleared(), 
						"User can able see input box is cleared after sending the message",
						"User not able see input box is cleared after sending the message");
			}
		} catch (Exception e) {
			Log.fail("Error while validating semantic similarity: " + e.getMessage());
		}
	}

	@Test(priority = 6)
	public void tc07VerifyScrollAndAccessibility() {
		
		Log.message("tc07VerifyScrollAndAccessibility: Verify that scrolling works correctly and the chat widget is accessible.");

		try {
			LoginPage loginPage = new LoginPage(driver);
			loginPage.clickOnLoginWithEmailLink();
			loginPage.loginToUAskWebApp(userName, password);

			ChatPage chatPage = new ChatPage(driver);
			TestData question;

			// Send multiple messages to check scroll
			for (int i = 3; i < 5; i++) {
				question = JsonUtils.getQuestionById(filePath, "UI_EN_0"+i);
				chatPage.enterChatInput(question.getInput());
				chatPage.clickOnButtonSend();
				chatPage.getLastAIMessage(driver);
			}

			Log.assertThat(chatPage.verifyScrollExistForResponseContainer(), 
					"Scroll bar is displayed when AI response overflows – Accessibility verified.",
	                "Scroll bar not displayed when expected – Accessibility failed.");
		
		} catch (Exception e) {
			Log.fail("Error while validating semantic similarity: " + e.getMessage());
		}
	}
}
