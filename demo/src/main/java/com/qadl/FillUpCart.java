package com.qadl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class FillUpCart {

    /**
    * Search for a given string using amazon.com
    *
    * @param driver a WebDriver of a given browser (selenium)
    * @param researchedItem a string to research
    * @since 1.0
    */
    static void amazonSearch(WebDriver driver, String researchedItem) {
        String idSearchBox = "twotabsearchtextbox";
        String idSearchButton = "nav-search-submit-button";

        WebElement searchBox = driver.findElement(By.id(idSearchBox));
        WebElement searchButton = driver.findElement(By.id(idSearchButton));

        searchBox.sendKeys(researchedItem);
        searchButton.click();
    }

    /**
    * Check if a given element contains the "discount" markers
    *
    * @param elt a WebElement from a page
    * @returns true if the element does not contain "discount" markers, else false
    * @since 1.0
    */
    static boolean noPromoFiltering (WebElement elt) {
        try {
            elt.findElement(By.cssSelector("span[data-component-type='s-coupon-component']"));
        } catch (Exception e) {
            return true;
        }
        
        return false;
    }

    /**
    * Click the "add to cart" buttons of a List of WebElement
    *
    * @param elements a list of WebElements from a page that contains "add to cart" buttons
    * @since 1.0
    */
    static void addProductsToCart(List<WebElement> elements) {
        String addButtonSelector = "button[class=\"a-button-text\"]";
        for (WebElement resultRow : elements) {
            WebElement addButton = resultRow.findElement(By.cssSelector(addButtonSelector));
            addButton.click();

            addButton = resultRow.findElement(By.cssSelector(addButtonSelector));
            while (!addButton.getText().equals("Add to cart")) {
                addButton = resultRow.findElement(By.cssSelector(addButtonSelector));
            }
        }
    }

    public static void main(String[] args) {

        // Declare variable
        String researchedItem = "laptop";

        WebDriver driver = CommonLibs.setupTest();
        CommonLibs.HandleBotCheck(driver, CommonLibs.getStartUrl());

        // Go to the main page
        driver.get(CommonLibs.getStartUrl());
        CommonLibs.waitForPageLoad(driver);

        // Search for the item
        amazonSearch(driver, researchedItem);
        CommonLibs.waitForPageLoad(driver);

        // retrieve the list of products (rows)
        List<WebElement> resultRows = driver.findElements(By.cssSelector("div[data-component-type=\"s-search-result\"]"));

        // filter out the discounted products
        List<WebElement> productList = resultRows.stream().filter(elt -> noPromoFiltering(elt)).collect(Collectors.toList());
        addProductsToCart(productList);

        // store some info for future usage
        List<String> textProductList = new ArrayList<>();
        for (WebElement resultRow : productList) {
            textProductList.add(resultRow.getAttribute("data-asin"));
        }

        // go to the cart (we could use the button as well...)
        driver.get("https://www.amazon.com/gp/cart/view.html?ref_=nav_cart");
        CommonLibs.waitForPageLoad(driver);

        // List the products from the cart
        String cartGlobalFrameSelector = "div[data-name=\"Active Items\"]";
        String cartActiveItemsSelector = "div[class=\"a-row sc-list-item sc-java-remote-feature\"]";
        WebElement actItems = driver.findElement(By.cssSelector(cartGlobalFrameSelector));
        List<WebElement> cartList = actItems.findElements(By.cssSelector(cartActiveItemsSelector)); 

        // validate the data
        // we could (in spending more time) build a validation count with more different fields taken in account... 
        int valid_count = 0;
        for (WebElement cartElt : cartList) {
            if (textProductList.contains(cartElt.getAttribute("data-asin"))) {
                valid_count++;
            }
        }

        // VALIDATE DATA
        if (valid_count == cartList.size()) {
            System.out.println("PASSED");
            System.exit(0);
        } else {
            System.err.println("FAILED");
            System.exit(1);
        }
  
        CommonLibs.teardownTest(driver);
  
    }
}