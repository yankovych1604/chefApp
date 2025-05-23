package com.maksy.chefapp.ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FooterComponentTest extends AbstractUiTest {

    @Test
    @Order(1)
    void testFooterContentAndLinks() throws InterruptedException {
        driver.get("http://localhost:8081/");
        Thread.sleep(500);

        WebElement footer = driver.findElement(By.tagName("footer"));
        Assertions.assertTrue(footer.isDisplayed(), "Футер має бути видимий");

        Assertions.assertTrue(footer.getText().contains("Cookbook"), "Має бути назва 'Cookbook'");
        Assertions.assertTrue(footer.getText().contains("Cook with passion"), "Має бути девіз");

        List<WebElement> quickLinks = footer.findElements(By.cssSelector(".col-lg-3 ul li a"));
        Assertions.assertEquals(4, quickLinks.size(), "Очікується 4 посилання у Quick Links");

        String[][] expectedQuickLinks = {
                {"/", "Home"},
                {"/dishes/all", "Dishes"},
                {"/ingredients", "Ingredients"},
                {"/about", "About"}
        };

        for (int i = 0; i < expectedQuickLinks.length; i++) {
            WebElement link = quickLinks.get(i);
            Assertions.assertEquals(expectedQuickLinks[i][1], link.getText().trim());
            Assertions.assertTrue(link.getAttribute("href").endsWith(expectedQuickLinks[i][0]), "Посилання має закінчуватись на " + expectedQuickLinks[i][0]);
        }

        List<WebElement> socials = footer.findElements(By.cssSelector(".col-lg-3.text-center .listItem"));
        Assertions.assertEquals(3, socials.size(), "Очікується 3 соціальні посилання");

        Assertions.assertTrue(socials.get(0).getText().contains("Facebook"));
        Assertions.assertTrue(socials.get(1).getText().contains("Instagram"));
        Assertions.assertTrue(socials.get(2).getText().contains("Twitter"));
    }

    @Test
    @Order(2)
    void testFooterVisibleOnOtherPages() throws InterruptedException {
        driver.get("http://localhost:8081/");
        Thread.sleep(500);

        String[] urls = {
                "/dishes/all",
                "/ingredients",
                "/about"
        };

        for (String url : urls) {
            driver.get("http://localhost:8081" + url);
            Thread.sleep(500);
            WebElement footer = driver.findElement(By.tagName("footer"));
            Assertions.assertTrue(footer.isDisplayed(), "Футер має бути на сторінці " + url);
        }
    }
}
