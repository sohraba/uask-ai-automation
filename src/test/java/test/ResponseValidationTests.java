package test;

import base.BaseTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import static io.restassured.RestAssured.*;

import java.util.Map;

import utils.*;

public class ResponseValidationTests extends BaseTest {

    String filePath = "src/main/resources/test-data.json";
    String username = ConfigReader.get("username");
    String password = ConfigReader.get("password");
    
    private String token;
    
    @BeforeClass
    public void setup() {
        token = APIUtils.getAccessToken(username, password);
        sessionId = APIUtils.getSessionId(username, password); 
    }
    
    @Test(priority = 0)
    public void tc01VerifyHelpfulResponse() {
    	
    	Log.message("tc01VerifyHelpfulResponse: Verify that user able to see helpful ai response");
    	
    	String userQuestion = "", expectedResponse = "", actualResponse = "";
		Double threshold, similarityScore = 0.0 ;
		boolean isValid = false;
		
		try {
			
			TestData questionId = JsonUtils.getApiQuestionById(filePath, "API_EN_01");
	    	userQuestion = questionId.getInput();
			expectedResponse = questionId.getExpected();
			actualResponse = questionId.getActual();
			threshold = questionId.getThreshold();
	        
	        Map<String, String> chatData = APIUtils.createNewChat(token, questionId.getInput());
	        String chatId = chatData.get("chatId");
	        String messageId = chatData.get("userMessageId");

	        Map<String, String> responseData = APIUtils.getAIResponse(token, sessionId, chatId, messageId, userQuestion, actualResponse);
	        actualResponse = responseData.get("assistantResponse");
	        
	        similarityScore = TextUtils.getSemanticSimilarity(expectedResponse, actualResponse);
			isValid = similarityScore >= threshold;

	        Log.assertThat(isValid, 
	        		"Response is clear and helpful response to common public service queries",
	        		"Response not clear and helpful enough!");
	        
		} catch (Exception e) {
			Log.fail("Error while validating semantic similarity: " + e.getMessage());
		} finally {
			ChatReportUtils.logResultHtml(userQuestion, expectedResponse, actualResponse, similarityScore, isValid);
		}
    }

    @Test(priority = 1)
    public void tc02VerifyNoHallucination() {
    	
    	Log.message("tc02VerifyNoHallucination: Verify that user able to see proper response, not hallucinated");
    	
    	String userQuestion = "", expectedResponse = "", actualResponse = "";
		Double threshold, similarityScore = 0.0 ;
		boolean isValid = false;
		
		try {
			
			TestData questionId = JsonUtils.getApiQuestionById(filePath, "API_EN_01");
	    	userQuestion = questionId.getInput();
			expectedResponse = questionId.getExpected();
			actualResponse = questionId.getActual();
			threshold = questionId.getThreshold();
	        
	        Map<String, String> chatData = APIUtils.createNewChat(token, questionId.getInput());
	        String chatId = chatData.get("chatId");
	        String messageId = chatData.get("userMessageId");

	        Map<String, String> responseData = APIUtils.getAIResponse(token, sessionId, chatId, messageId, userQuestion, actualResponse);
	        actualResponse = responseData.get("assistantResponse");
	        
	        similarityScore = TextUtils.getSemanticSimilarity(expectedResponse, actualResponse);
			isValid = similarityScore >= threshold;

	        Log.assertThat(isValid, 
	        		"Responses are not hallucinated ",
	        		"Hallucination detected! Response irrelevant." );
	        
		} catch (Exception e) {
			Log.fail("Error while validating semantic similarity: " + e.getMessage());
		} finally {
			ChatReportUtils.logResultHtml(userQuestion, expectedResponse, actualResponse, similarityScore, isValid);
		}
    }

    @Test(priority = 2)
    public void tc03VerifyResponseConsistencyAcrossLanguages() {
    	
    	Log.message("tc03VerifyResponseConsistencyAcrossLanguages: Verify that user able to see that responses stay consistent across languages(Arabic and English)");
    	
    	String userQuestion = "", expectedResponse = "", actualResponse = "";
		String userQuestionInArabic = "", expectedResponseInArabic = "", actualResponseInArabic = "";
		Double threshold, similarityScore = 0.0 ;
		Double thresholdInArabic, similarityScoreInArabic = 0.0 ;
		boolean isValid = false;
		boolean isValidInArabic = false;
		
		try {
			
			TestData questionId_English = JsonUtils.getApiQuestionById(filePath, "API_EN_02");
	    	userQuestion = questionId_English.getInput();
			expectedResponse = questionId_English.getExpected();
			actualResponse = questionId_English.getActual();
			threshold = questionId_English.getThreshold();
	        
	        Map<String, String> chatData = APIUtils.createNewChat(token, userQuestion);
	        String chatId = chatData.get("chatId");
	        String messageId = chatData.get("userMessageId");

	        Map<String, String> responseData = APIUtils.getAIResponse(token, sessionId, chatId, messageId, userQuestion, actualResponse);
	        actualResponse = responseData.get("assistantResponse");
	        
	        similarityScore = TextUtils.getSemanticSimilarity(expectedResponse, actualResponse);
			isValid = similarityScore >= threshold;

	        Log.assertThat(isValid, 
	        		"User can see AI-generated responses are displayed correctly in English.",
					"User not able see to AI-generated responses correctly in English.");
	        
	        TestData questionId_Arabic = JsonUtils.getApiQuestionById(filePath, "API_AR_01");
	        userQuestionInArabic = questionId_Arabic.getInput();
	        expectedResponseInArabic = questionId_Arabic.getExpected();
			actualResponseInArabic = questionId_Arabic.getActual();
			thresholdInArabic = questionId_Arabic.getThreshold();
	        
	        Map<String, String> chatData_Arabic = APIUtils.createNewChat(token, userQuestionInArabic);
	        chatId = chatData_Arabic.get("chatId");
	        messageId = chatData_Arabic.get("userMessageId");

	        Map<String, String> responseData_Arabic = APIUtils.getAIResponse(token, sessionId, chatId, messageId, userQuestionInArabic, actualResponseInArabic);
	        actualResponseInArabic = responseData_Arabic.get("assistantResponse");
	        
	        similarityScoreInArabic = TextUtils.getSemanticSimilarity(expectedResponseInArabic, actualResponseInArabic);
	        isValidInArabic = similarityScoreInArabic >= thresholdInArabic;

	        Log.assertThat(isValidInArabic, 
	        		"User can see AI-generated responses are displayed correctly in Arabic.",
					"User not able see to AI-generated responses correctly in Arabic.");
	        
		} catch (Exception e) {
			Log.fail("Error while validating semantic similarity: " + e.getMessage());
		} finally {
			ChatReportUtils.logResultHtml(userQuestion, expectedResponse, actualResponse, similarityScore, isValid);
			ChatReportUtils.logResultHtml(userQuestionInArabic, expectedResponseInArabic, actualResponseInArabic, similarityScoreInArabic, isValidInArabic);
		}
    }

    @Test(priority = 3)
    public void tc04VerifyCleanFormatting() {
    	
    	Log.message("tc04VerifyCleanFormatting: Verify that user able to see clean response formatting");
    	
    	String userQuestion = "", expectedResponse = "", actualResponse = "";
		boolean isValid = false;
		
		try {
			
			TestData questionId = JsonUtils.getApiQuestionById(filePath, "API_EN_01");
	    	userQuestion = questionId.getInput();
			expectedResponse = questionId.getExpected();
			actualResponse = questionId.getActual();
	        
	        Map<String, String> chatData = APIUtils.createNewChat(token, questionId.getInput());
	        String chatId = chatData.get("chatId");
	        String messageId = chatData.get("userMessageId");

	        Map<String, String> responseData = APIUtils.getAIResponse(token, sessionId, chatId, messageId, userQuestion, actualResponse);
	        actualResponse = responseData.get("assistantResponse");
	        
	        isValid = (actualResponse.contains("Step-by-Step") && actualResponse.contains("##") && actualResponse.length() > 50) &&  !(actualResponse.endsWith("...") && actualResponse.contains("<br><br><br>") || actualResponse.contains("</p><p>"));
	        Log.assertThat(isValid, 
	        		"AI response look complete and helpful and not have broken HTML tags!",
	        		"AI response look incomplete and unhelpful and have broken HTML tags!");
	        
		} catch (Exception e) {
			Log.fail("Error while validating semantic similarity: " + e.getMessage());
		} finally {
			ChatReportUtils.logResultHtml(userQuestion, expectedResponse, actualResponse, null , isValid);
		}
    }

    @Test(priority = 4)
    public void tc05VerifyFallbackMessage() {
    	
    	Log.message("tc05VerifyFallbackMessage: Verify that user able to see proper fallback messages");
    	
    	String userQuestion = "", expectedResponse = "", actualResponse = "";
		Double threshold, similarityScore = 0.0 ;
		boolean isValid = false;
		
		try {
			
			TestData questionId = JsonUtils.getApiQuestionById(filePath, "API_EN_03");
	    	userQuestion = questionId.getInput();
			expectedResponse = questionId.getExpected();
			actualResponse = questionId.getActual();
			threshold = questionId.getThreshold();
	        
	        Map<String, String> chatData = APIUtils.createNewChat(token, userQuestion);
	        String chatId = chatData.get("chatId");
	        String messageId = chatData.get("userMessageId");

	        Map<String, String> responseData = APIUtils.getAIResponse(token, sessionId, chatId, messageId, userQuestion, actualResponse);
	        actualResponse = responseData.get("assistantResponse");
	        
	        similarityScore = TextUtils.getSemanticSimilarity(expectedResponse, actualResponse);
			isValid = similarityScore >= threshold;

	        Log.assertThat(isValid, 
	        		"Fallback message is displayed properly as expected",
	        		"Fallback message incorrect!\nExpected: " + expectedResponse + "\nActual: " + actualResponse);
	        
		} catch (Exception e) {
			Log.fail("Error while validating semantic similarity: " + e.getMessage());
		} finally {
			ChatReportUtils.logResultHtml(userQuestion, expectedResponse, actualResponse, similarityScore, isValid);
		}
    }
}
