# U-Ask AI/ML QA Automation Framework (Java + Selenium + Appium)

## 📌 Overview
This framework automates UI, AI-response validation, and security tests for the UAE Government’s U-Ask chatbot.

It validates:
✔ Chatbot UI behavior  
✔ AI-generated responses (EN/AR)  
✔ Hallucination & consistency checks  
✔ Security & prompt-injection handling

---

---
## 📂 Folder Structure
src/test/java  
├── base  
├── pages
├── utils  
└── resources

src/test/java   
├── tests  
├── utils

---
## 🚀 How to Execute Tests

#**Running the Tests**

**Using TestNG xml**
1. Ensure the testng.xml file is configured
2. Run in IDE:
    * IntelliJ IDEA: Right-click testng.xml -> Run 'testng.xml'
    * Eclipse: Right-click testng.xml -> Run As -> TestNG Suite

3. Run via Maven (Command Line):

   mvn test -DsuiteXmlFile=testng.xml

* Runs all tests defined in the TestNG XML file

## 📊 Reports

* **TestNG reports**: Generated automatically under test-output/index.html or ExtentReport.html

Screenshots auto-save in `/screenshots`.

---

## 🌍 Language Support
`test-data.json` stores English and Arabic input prompts.

Modify values to run multilingual tests.

---

## ✨ Key Features
- Page Object Model
- AI response validators
- JSON-based test data
- Mobile + Web execution
- Extent Reporting
