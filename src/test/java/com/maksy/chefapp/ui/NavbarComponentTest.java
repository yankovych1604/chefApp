package com.maksy.chefapp.ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NavbarComponentTest extends AbstractUiTest {

    @Test
    @Order(1)
    void testNavbarLinksExistAndRedirectCorrectly() throws InterruptedException {
        driver.get("http://localhost:8081/");
        Thread.sleep(500);

        List<WebElement> navLinks = driver.findElements(By.cssSelector(".navbar-nav .nav-link"));
        Assertions.assertEquals(5, navLinks.size(), "Очікується 5 посилань у навбарі");

        String[][] expectedLinks = {
                {"All Dishes", "/dishes/all"},
                {"Create Dish", "/dishes/create-dish"},
                {"Ingredients", "/ingredients"},
                {"Create Ingredient", "/ingredients/create-ingredient"},
                {"About Cookbook", "/about"}
        };

        for (int i = 0; i < expectedLinks.length; i++) {
            WebElement link = navLinks.get(i);
            Assertions.assertEquals(expectedLinks[i][0], link.getText().trim());

            String href = link.getAttribute("href");
            Assertions.assertTrue(href.endsWith(expectedLinks[i][1]), "Посилання має закінчуватись на " + expectedLinks[i][1]);
        }
    }

    @Test
    @Order(2)
    void testEachNavbarLinkNavigation() throws InterruptedException {
        driver.get("http://localhost:8081/");
        Thread.sleep(500);

        String[][] pages = {
                {"/dishes/all", "All Dishes"},
                {"/dishes/create-dish", "Create Dish"},
                {"/ingredients", "All Ingredients"},
                {"/ingredients/create-ingredient", "Create Ingredient"},
                {"/about", "About Cookbook"}
        };

        for (String[] page : pages) {
            driver.get("http://localhost:8081" + page[0]);
            Thread.sleep(500);
            WebElement navLink = driver.findElement(By.cssSelector(".navbar .nav-link.active, .navbar .nav-link[href*='" + page[0] + "']"));
            Assertions.assertTrue(navLink.isDisplayed(), "Має бути активне або наявне посилання на " + page[0]);
        }
    }
}