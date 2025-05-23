package com.maksy.chefapp.ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IngredientDetailsPageTest extends AbstractUiTest {

    @Test
    @Order(1)
    void testIngredientDetailsPageDisplaysCorrectly() throws InterruptedException {
        driver.get("http://localhost:8081/ingredients/1");
        Thread.sleep(500);

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h3")));
        Assertions.assertFalse(title.getText().isBlank(), "Назва інгредієнта не повинна бути порожньою");

        WebElement calories = driver.findElement(By.xpath("//p[strong[text()='Calories per 100g:']]/span"));
        Assertions.assertFalse(calories.getText().isBlank(), "Значення калорій повинно бути відображене");
    }

    @Test
    @Order(2)
    void testEditButtonOpensEditPage() throws InterruptedException {
        driver.get("http://localhost:8081/ingredients/1");
        Thread.sleep(500);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement editBtn = driver.findElement(By.xpath("//a[contains(@href, '/ingredients/edit/')]"));
        Assertions.assertEquals("Edit", editBtn.getText());

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", editBtn);
        Thread.sleep(500);

        String editHref = editBtn.getAttribute("href");
        Assertions.assertTrue(editHref.contains("/ingredients/edit/"));

        js.executeScript("window.open(arguments[0], '_blank');", editHref);
        Thread.sleep(500);

        List<String> tabs = driver.getWindowHandles().stream().toList();
        driver.switchTo().window(tabs.get(1));

        wait.until(ExpectedConditions.urlMatches("http://localhost:8081/ingredients/edit/\\d+"));
        Assertions.assertTrue(driver.getCurrentUrl().matches("http://localhost:8081/ingredients/edit/\\d+"));

        driver.close();
        driver.switchTo().window(tabs.get(0));
    }

    @Test
    @Order(3)
    void testDeleteButtonShowsConfirmationPrompt() throws InterruptedException {
        driver.get("http://localhost:8081/ingredients/1");
        Thread.sleep(500);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement deleteForm = driver.findElement(By.xpath("//form[contains(@action, '/ingredients/delete/')]"));

        WebElement deleteBtn = deleteForm.findElement(By.cssSelector("button[type='submit']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", deleteBtn);
        Thread.sleep(500);

        js.executeScript("window.confirm = function(msg) { window._confirmMessage = msg; return false; };");
        deleteBtn.click();
        Thread.sleep(500);

        wait.until(d -> js.executeScript("return window._confirmMessage") != null);
        String confirmText = (String) js.executeScript("return window._confirmMessage;");
        Assertions.assertTrue(confirmText.toLowerCase().contains("are you sure"));
    }

    @Test
    @Order(4)
    void testBackToListButtonRedirectsCorrectly() throws InterruptedException {
        driver.get("http://localhost:8081/ingredients/1");
        Thread.sleep(500);

        WebElement backBtn = driver.findElement(By.xpath("//a[text()='Back to List']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", backBtn);
        Thread.sleep(500);

        backBtn.click();
        Thread.sleep(500);

        wait.until(ExpectedConditions.urlToBe("http://localhost:8081/ingredients"));
        Assertions.assertEquals("http://localhost:8081/ingredients", driver.getCurrentUrl());
    }
}