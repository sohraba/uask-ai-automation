package pages;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.Log;
import utils.UAskUtils;


public class ChatPage {

    WebDriver driver;

    @FindBy(xpath = "//div[@class='welcome-text']")
    WebElement txtTitle;

    @FindBy(id = "chat-input")
    WebElement txtChatInput;

    @FindBy(id = "send-message-button")
    WebElement btnSend;

    @FindBy(css = "#response-content-container div[class='relative']")
    List<WebElement> txtAIResponseContentContainer;

    @FindBy(css = "#response-content-container")
    List<WebElement> txtResponseContentContainer;

    @FindBy(xpath = "//span[text()='Searching the web']")
    WebElement txtloading;

    public ChatPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        UAskUtils.waitForPageLoad(driver, d -> txtTitle.isDisplayed());
    }

    /**
     * To verify if page is loaded
     *
     * @return
     * 		- true if title is correct else false
     */
    public boolean isPageLoaded() {
        return txtTitle.isDisplayed();
    }

    /**
     * Enter text in chat text area
     *
     * @param input
     */
    public void enterChatInput(String input) {
        Log.event("Entering '" + input + "' in chat input box");
        UAskUtils.waitForElement(driver, txtChatInput);
        txtChatInput.sendKeys(input);
        Log.message("Entered '" + input + "' in chat input box");
    }

    /**
     * To click 'Send' button
     */
    public void clickOnButtonSend() {
        Log.event("Clicking 'Send' button");
        UAskUtils.waitForElement(driver, btnSend);
        btnSend.click();
        Log.message("Clicked 'Send' button");

    }

    /**
     * To verify Chat input box area is empty or not
     *
     * @return
     * 		- boolean
     */
    public boolean isInputCleared() {
        Log.event("Verifying chat box is empty or not");
        try {
            String value = txtChatInput.getAttribute("value");
            return txtChatInput.findElement(By.cssSelector(".is-empty.is-editor-empty")).isDisplayed() &&
                    (value == null || value.trim().isEmpty());
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * To returns the text of the latest AI response in the chat window
     *
     * @param driver
     * 		- WebDriver instance
     * @return
     * 		- Latest AI message text, or null if none found
     * @throws Exception
     */
    public String getLastAIMessage(WebDriver driver) {
        Log.event("Getting latest response from AI");

        try {
            UAskUtils.waitForElementToDisappear(driver, txtloading, 60);

            int oldCount = txtAIResponseContentContainer.size();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
            wait.until(d -> {
                int newCount = txtAIResponseContentContainer.size();
                return newCount > oldCount;
            });

            String lastMessage = txtAIResponseContentContainer.get(txtAIResponseContentContainer.size() - 1).getText().trim();
            Log.message("Latest AI response: " + lastMessage);
            return lastMessage;

        } catch (Exception e) {
            Log.message("Failed to fetch AI response: " + e.getMessage());
            return null;
        }
    }

    /**
     * To verify multi-language support
     *
     * @param languageName
     * 		- name of the language
     * @return
     * 		- boolean value
     */
    public boolean isMultilanguageDisplayed(String languageName) {
        Log.event("Verifying multilanguage support");
        boolean langStatus = false;
        WebElement element = txtResponseContentContainer.get(txtResponseContentContainer.size() - 1).findElement(By.xpath("../../../../..")).findElement(By.xpath("//div[contains(@class,'message') and not(contains(@class,'group'))]"));
        switch (languageName) {
            case "English":
                langStatus = element.getCssValue("direction").equals("ltr");
                break;
            case "Arabic":
                langStatus = element.getCssValue("direction").equals("rtl");
                break;
            default:
                break;
        }
        return langStatus;
    }

    /**
     * To verify scrollbar is displayed or not
     *
     * @return boolean
     * 		   true if scrollbar is displayed
     */
    public boolean verifyScrollExistForResponseContainer(){
        return UAskUtils.verifyScrollExistForElement(driver, txtResponseContentContainer.get(txtResponseContentContainer.size() - 1));
    }

}
