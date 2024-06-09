package com.qadl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class AmazonCrawler {

    /**
    * Open the drop menu of "www.amazon.com"
    *
    * @param driver a WebDriver of a given browser (selenium)
    * @throws java.lang.InterruptedException
    * @since 1.0
    */
    static void openDropMenu(WebDriver driver) throws InterruptedException {
        driver.findElement(By.linkText("All")).click();
        Thread.sleep(500);
    }

    /**
    * Click the different "See all" buttons of a page 
    *
    * @param driver a WebDriver of a given browser (selenium)
    * @throws java.lang.InterruptedException
    * @since 1.0
    */
    static void extendMenu(WebDriver driver) throws InterruptedException {
        List<WebElement> seeAllButtons = driver.findElements(By.linkText("See all"));
        for (WebElement seeAllButton : seeAllButtons) {
            seeAllButton.click();
        }
        Thread.sleep(500);
    }

    /**
    * Returns the elements of the "Shop by Department" section of the menu
    *
    * @param driver a WebDriver of a given browser (selenium)
    * @return A List of WebElement of the "Shop by Department" section of the menu
    * @since 1.0
    */
    static List<WebElement> parseMenu(WebDriver driver) {
        WebElement menuContent = driver.findElement(By.cssSelector("#hmenu-content > ul.hmenu.hmenu-visible"));
        System.out.println(menuContent.getText());

        List<WebElement> items = menuContent.findElements(By.tagName("li"));

        List<WebElement> filteredItems = new ArrayList<>();
        boolean captureElements = false;
        for (WebElement item : items) {

            // TODO: make the methods more generic with START_STRING & END_STRING as arguments
            if (item.getText().contains("See less")) {
                captureElements = false;
                continue;
            }

            if (captureElements) {
                if (item.getText().strip().equals("")) {
                    continue;
                }
                filteredItems.add(item);
            }

            if (item.getText().contains("Shop by Department")) {
                captureElements = true;
            }
        }
        
        return filteredItems;
    }

    /**
    * Returns all the links of the "Shop by Deparment section
    *
    * @param driver a WebDriver of a given browser (selenium)
    * @param items a List of WebElement representing the different element of the menu sections
    * @return A List of WebElement of the "Shop by Department" section of the menu
    * @throws InterruptedException
    * @since 1.0
    */
    static Dictionary<String, Dictionary> collectAllLinks(WebDriver driver, List<WebElement> items) throws InterruptedException {

        Dictionary<String, Dictionary> fullDict= new Hashtable<>();
        for (WebElement item : items) {
            // go forward
            System.out.println("Dig for the category: " + item.getText());
            String mainCategory = item.getText();
            item.click();
            Thread.sleep(500);

            // work within the subsection menu
            WebElement menuContent = driver.findElement(By.cssSelector("#hmenu-content > ul.hmenu.hmenu-visible"));
            List<WebElement> subItems = menuContent.findElements(By.cssSelector("#hmenu-content > ul > li > a[class=\"hmenu-item\"]"));

            Dictionary<String, String> linkDict= new Hashtable<>();
            for (WebElement subItem : subItems) {
                if (subItem.getText().strip().equals("")) {continue;}
                // key = subcategory ; value = url ;
                linkDict.put(subItem.getText(), subItem.getAttribute("href"));
                fullDict.put(mainCategory, linkDict);
            }

            // go backward
            WebElement backButton = driver.findElement(By.linkText("MAIN MENU")); // By.xpath("//*[@id=\"hmenu-content\"]/ul[5]/li[1]/a"));
            Actions a= new Actions(driver);
            a.moveToElement(backButton).click().perform();
            Thread.sleep(500);
            // Retry in case of a buggy artifact (superposition of frames)
            a.moveToElement(backButton).click().perform();
            Thread.sleep(500);
        }
        return fullDict;
    }

    /**
    * Visit all the provided links and report its status
    *
    * @param driver a WebDriver of a given browser (selenium)
    * @param infoDict a Dictionnary that contains (key: subcategory, value: url)
    * @throws IOException
    * @since 1.0
    */
    static void testAndReport (WebDriver driver, Dictionary<String, Dictionary> infoDict) throws IOException {

        String fileName = new SimpleDateFormat("yyyyMMddHHmmss'_results.txt'").format(new Date());
        String headerFile = "#link, page title, status";

        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(headerFile + '\n'); 
        
        Enumeration<String> k = infoDict.keys();
        while (k.hasMoreElements()) {
            String mainCategory = k.nextElement();

            Dictionary <String, String> subDict = infoDict.get(mainCategory);
            Enumeration<String> j = subDict.keys();
            while (j.hasMoreElements()) {
                String subCategory = j.nextElement();
                String url = subDict.get(subCategory);

                String fullCategory = mainCategory + " > " + subCategory;

                String status = "Dead link";
                try {
                    driver.get(url);
                    // pageTitle = driver.getTitle();
                    status = "OK";
                } catch (Exception e) {}

                // System.out.println(url + ",  " + fullCategory + ", " + status);
                writer.write(url + ",  " + fullCategory + ", " + status + '\n');

            }
        }
        writer.close();
    }

    /**
    * Parse the drop menu to retrieve the different categories of the main menu
    *
    * @param driver a WebDriver of a given browser (selenium)
    * @returns List of WebElements representing each main categories
    * @throws InterruptedException
    * @since 1.0
    */
    static List<WebElement> collectAllCategories (WebDriver driver) throws InterruptedException {
        openDropMenu(driver);
        extendMenu(driver);

        return parseMenu(driver);
        
    }

    public static void main(String[] args) throws InterruptedException, MalformedURLException, ProtocolException, IOException {

        try {
            WebDriver driver = CommonLibs.setupTest();
            CommonLibs.HandleBotCheck(driver, CommonLibs.getStartUrl());

            System.out.println("Go to the main page");
            driver.get(CommonLibs.getStartUrl());
            CommonLibs.waitForPageLoad(driver);

            List<WebElement> items = collectAllCategories(driver);
            Dictionary<String, Dictionary> fullDict = collectAllLinks(driver, items);
            testAndReport(driver, fullDict);

            CommonLibs.teardownTest(driver);
        }
        catch (MalformedURLException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }
        catch (IOException e) {
            System.err.println("Something went wrong when creating the report file:\n" + e.getMessage());
            System.exit(1);
        }
        

        System.exit(0);
   
    }
    
}
