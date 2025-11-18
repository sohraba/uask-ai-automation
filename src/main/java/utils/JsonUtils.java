package utils;

import java.io.FileReader;
import java.util.List;

import com.google.gson.Gson;

public class JsonUtils {

	/**
	 * To get the ui question details based on id
	 * 
	 * @param fileName
	 * @param id
	 * @return
	 */
	public static TestData getQuestionById(String fileName, String id) {
		try {
			Gson gson = new Gson();
			TestDataContainer container = gson.fromJson(new FileReader(fileName), TestDataContainer.class);
			if (container != null && container.getUi() != null) {
				return container.getUi().stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * To get all ui questions
	 * 
	 * @param fileName
	 * @return
	 */
    public static List<TestData> getAllUIQuestions(String fileName) {
        try {
            Gson gson = new Gson();
            TestDataContainer container = gson.fromJson(new FileReader(fileName), TestDataContainer.class);
            if (container != null && container.getUi() != null) {
                return container.getUi();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
	 * To get the security question details based on id
	 * 
	 * @param fileName
	 * @param id
	 * @return
	 */
	public static TestData getSecurityTestById(String fileName, String id) {
	    try {
	        Gson gson = new Gson();
	        TestDataContainer container = gson.fromJson(new FileReader(fileName), TestDataContainer.class);
	        if (container != null && container.getSecurityTests() != null) {
	            return container.getSecurityTests().stream()
	                    .filter(c -> c.getId().equals(id))
	                    .findFirst()
	                    .orElse(null);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	/**
	 * To get all security questions
	 * 
	 * @param fileName
	 * @return
	 */
    public static List<TestData> getAllSecurityTests(String fileName) {
        try {
            Gson gson = new Gson();
            TestDataContainer container = gson.fromJson(new FileReader(fileName), TestDataContainer.class);
            if (container != null && container.getSecurityTests() != null) {
                return container.getSecurityTests();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
    /**
	 * To get the api question details based on id
	 * 
	 * @param fileName
	 * @param id
	 * @return
	 */
	public static TestData getApiQuestionById(String fileName, String id) {
	    try {
	        Gson gson = new Gson();
	        TestDataContainer container = gson.fromJson(new FileReader(fileName), TestDataContainer.class);
	        if (container != null && container.getApi() != null) {
	            return container.getApi().stream()
	                    .filter(c -> c.getId().equals(id))
	                    .findFirst()
	                    .orElse(null);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}
}
