package com.qadl;

import java.time.Duration;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CommonLibs {

    public int msTimeout = 200;
    private static String url = "https://www.amazon.com";
    private static String pathToChromeDriver = "/usr/bin/chromedriver";


    static String getStartUrl() {
        return url;
    }
    
    static void teardownTest(WebDriver driver) {
        //driver.quit();
        System.out.println("deactivated teardown");
    }

    static WebDriver setupTest() {
        System.out.println("Configure the webbrowser");
        System.setProperty("webdriver.chrome.driver", pathToChromeDriver);

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.addArguments("start-maximized");
        //options.addArguments("--headless=new");


        System.out.println("Start the webbrowser");
        WebDriver driver = new ChromeDriver(options);

        return driver;
    }

    /**
    * Wait for a web page to be fully loaded
    *
    * @param driver any object of Class WebDriver
    */ 
    static void waitForPageLoad(WebDriver driver) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(webDriver -> "complete".equals(((JavascriptExecutor) webDriver).executeScript("return document.readyState")));
    }

    static void HandleBotCheck(WebDriver driver, String url) {
        String botMessage = "Sorry, we just need to make sure you're not a robot";
        driver.get(url);
        String first_try = driver.getPageSource();
        while (first_try.contains(botMessage)) {
            System.out.println("Waiting for completion of the test (or autoforce)");
            driver.get(url);
            first_try = driver.getPageSource();
        }
    }

}