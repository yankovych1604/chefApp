package com.maksy.chefapp.ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AboutPageTest extends AbstractUiTest {

    @Test
    void testAboutPageContent() {
        driver.get("http://localhost:8081/about");

        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));
        Assertions.assertEquals("About Our Cookbook", heading.getText());

        List<WebElement> paragraphs = driver.findElements(By.tagName("p"));
        Assertions.assertTrue(paragraphs.stream().anyMatch(p -> p.getText().contains("Welcome to Cookbook")));
        Assertions.assertTrue(paragraphs.stream().anyMatch(p -> p.getText().contains("our mission is to inspire")));
        Assertions.assertTrue(paragraphs.stream().anyMatch(p -> p.getText().contains("Happy cooking")));

        WebElement subheading = driver.findElement(By.tagName("h5"));
        Assertions.assertEquals("What we present?", subheading.getText());

        WebElement list = driver.findElement(By.cssSelector("ul.mx-auto"));
        List<WebElement> listItems = list.findElements(By.tagName("li"));
        Assertions.assertEquals(4, listItems.size());
        Assertions.assertTrue(listItems.stream().anyMatch(li -> li.getText().contains("dishes to suit every taste")));
        Assertions.assertTrue(listItems.stream().anyMatch(li -> li.getText().contains("Ingredient management")));
        Assertions.assertTrue(listItems.stream().anyMatch(li -> li.getText().contains("Easy filtering")));
        Assertions.assertTrue(listItems.stream().anyMatch(li -> li.getText().contains("Regular updates")));
    }
}