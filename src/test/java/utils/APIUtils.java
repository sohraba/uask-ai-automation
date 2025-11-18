package utils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class APIUtils {

	private static String baseUrl = "https://govgpt.sandbox.dge.gov.ae";
	private static String loginEndPoint = "/api/v1/auths/signin";
	private static String newChatEndPoint = "/api/v1/chats/new";
	public static String aiResponseEndPoint = "/api/chat/completed";

	/**
	 * To get access token
	 * 
	 * @param username
	 * @param password
	 * @return - String as token
	 */
	public static String getAccessToken(String username, String password) {
		Response loginResponse = RestAssured.given().contentType(ContentType.JSON)
				.body("{\"email\":\"" + username + "\", \"password\":\"" + password + "\"}")
				.post(baseUrl + loginEndPoint).then().statusCode(200).extract().response();

		return loginResponse.jsonPath().getString("token");
	}
	
	/**
	 * To get session id
	 * 
	 * @param username
	 * @param password
	 * @return - String as session id
	 */
	public static String getSessionId(String username, String password) {
		Response loginResponse = RestAssured.given().contentType(ContentType.JSON)
				.body("{\"email\":\"" + username + "\", \"password\":\"" + password + "\"}")
				.post(baseUrl + loginEndPoint).then().statusCode(200).extract().response();

		return loginResponse.jsonPath().getString("session_id");
	}

	/**
	 * To perform 'POST' request
	 * 
	 * @param endpoint
	 * @param token
	 * @param payload
	 * @return - response
	 */
	public static Response postRequest(String endpoint, String token, String payload) {
		return RestAssured.given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post(endpoint)
                .then()
                .extract().response();
	}

	/**
	 * To perform 'GET' request
	 * 
	 * @param endpoint
	 * @param token
	 * @return - response
	 */
	public static Response getRequest(String endpoint, String token) {
		return RestAssured.given()
				.baseUri(baseUrl)
				.header("Authorization", "Bearer " + token)
				.contentType(ContentType.JSON)
				.when()
				.get(endpoint)
				.then()
				.extract().response();
	}

	/**
	 * To generate unique UUID
	 * 
	 * @return - string
	 */
	public static String generateUUID() {
		return UUID.randomUUID().toString();
	}

	/**
	 * To create a new chat session
	 *
	 * @param token
	 * @return userMessage
	 */
	public static Map<String, String> createNewChat(String token, String userMessage) {
        String messageId = generateUUID();
        long timestamp = Instant.now().getEpochSecond();

        String payload = "{\n" +
                "  \"chat\": {\n" +
                "    \"id\": \"\",\n" +
                "    \"title\": \"New Chat\",\n" +
                "    \"models\": [\"gpt-4.1\"],\n" +
                "    \"params\": {},\n" +
                "    \"history\": {\n" +
                "      \"messages\": {\n" +
                "        \"" + messageId + "\": {\n" +
                "          \"id\": \"" + messageId + "\",\n" +
                "          \"parentId\": null,\n" +
                "          \"childrenIds\": [],\n" +
                "          \"role\": \"user\",\n" +
                "          \"content\": \"" + userMessage + "\",\n" +
                "          \"timestamp\": " + timestamp + ",\n" +
                "          \"models\": [\"gpt-4.1\"],\n" +
                "          \"features\": {\n" +
                "            \"web_search\": false,\n" +
                "            \"deep_search\": false,\n" +
                "            \"rag\": false,\n" +
                "            \"unifyAgent\": false\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"currentId\": \"" + messageId + "\"\n" +
                "    },\n" +
                "    \"messages\": [\n" +
                "      {\n" +
                "        \"id\": \"" + messageId + "\",\n" +
                "        \"parentId\": null,\n" +
                "        \"childrenIds\": [],\n" +
                "        \"role\": \"user\",\n" +
                "        \"content\": \"" + userMessage + "\",\n" +
                "        \"timestamp\": " + timestamp + ",\n" +
                "        \"models\": [\"gpt-4.1\"],\n" +
                "        \"features\": {\n" +
                "          \"web_search\": false,\n" +
                "          \"deep_search\": false,\n" +
                "          \"rag\": false,\n" +
                "          \"unifyAgent\": false\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"tags\": [],\n" +
                "    \"timestamp\": " + (Instant.now().toEpochMilli()) + ",\n" +
                "    \"agent_id\": null\n" +
                "  }\n" +
                "}";
        Response response = postRequest(newChatEndPoint, token, payload);
        response.then().statusCode(200);

        Map<String, String> result = new HashMap<>();
        result.put("chatId", response.jsonPath().getString("id"));
        result.put("userMessageId", messageId);

        return result;
    }

    /**
     * To get ai assistant response
     * 
     * @param token
     * @param sessionId
     * @param chatId
     * @param userMessageId
     * @param userMessage
     * @param assitantMessage
     * @return
     */
    public static Map<String, String> getAIResponse(String token, String sessionId, String chatId, String userMessageId, String userMessage, String assitantMessage) {
    	String assistantMessageId = generateUUID();
    	long timestamp = Instant.now().getEpochSecond();
        
        String payload = "{\n" +
                "  \"model\": \"gpt-4.1\",\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"id\": \"" + userMessageId + "\",\n" +
                "      \"role\": \"user\",\n" +
                "      \"content\": \"" + userMessage + "\",\n" +
                "      \"timestamp\":"  + timestamp + "\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \""+ assistantMessageId + "\",\n" +
                "      \"role\": \"assistant\",\n" +
                "      \"content\": \"" + assitantMessage + "\",\n" +
                "      \"timestamp\": " + timestamp + ",\n" +
                "      \"sources\": []\n" +
                "    }\n" +
                "  ],\n" +
                "  \"chat_id\": \"" + chatId + "\",\n" +
                "  \"session_id\": \"" + sessionId + "\",\n" +
                "  \"id\": \"" +  assistantMessageId + "\"\n" +
                "}";
        
        Response response = postRequest(aiResponseEndPoint, token, payload);
        response.then().statusCode(200);

        Map<String, String> result = new HashMap<>();
        List<Map<String, Object>> messages = response.jsonPath().getList("messages");
        String assistantResponse = "";

        for (Map<String, Object> message : messages) {
            if ("assistant".equals(message.get("role"))) {
                assistantResponse = (String) message.get("content");
                break;
            }
        }
        result.put("assistantResponse", assistantResponse);
        return result;
    }
}
