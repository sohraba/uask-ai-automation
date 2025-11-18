package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;


import utils.Log;
import utils.UAskUtils;


public class LoginPage {

    WebDriver driver;

    //@FindBy(xpath = "//button[text()='Login using Credentials']")
    @FindBy(xpath = "//button[text()='Log in with email']")
    WebElement lnkLoginUsingEmail;

    @FindBy(id = "email")
    WebElement txtEmail;

    @FindBy(id = "password")
    WebElement txtPassWord;

    @FindBy(xpath = "//button[text()='Sign in']")
    WebElement btnSignIn;

    @FindBy(xpath = "//button[text()='Log in']")
    WebElement btnLogIn;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        UAskUtils.waitForPageLoad(driver, d -> lnkLoginUsingEmail.isDisplayed());
    }

    /**
     * To click on Login Using Credential
     *
     */
    public void clickOnLoginWithEmailLink() {
        Log.event("Clicking on Login Using Credential");
        lnkLoginUsingEmail.click();
        Log.message("Clicked on Login Using Credential");
    }

    /**
     * To login in to U-Ask application
     *
     * @param email
     * 		- Email/phone number
     * @param password
     * 		- password
     */
    public void loginToUAskWebApp(String email, String password) {
        Log.event("Login to the U-Ask");
        txtEmail.sendKeys(email);
        txtPassWord.sendKeys(password);
        btnLogIn.click();
        Log.message("Logged into U-Ask as (" + email + "/" + password + ")");
    }
}

