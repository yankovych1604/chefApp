package com.maksy.chefapp.ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.ExpectedConditions;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IngredientFormPageTest extends AbstractUiTest {

    @AfterEach
    void clearAlerts() {
        try {
            Alert alert = driver.switchTo().alert();
            alert.dismiss();
        } catch (NoAlertPresentException ignored) {}
    }

    @Test
    @Order(1)
    void testCreateFormInitialStateAndSubmission() throws InterruptedException {
        driver.get("http://localhost:8081/ingredients/create-ingredient");
        Thread.sleep(500);

        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h2")));
        Assertions.assertTrue(heading.getText().toLowerCase().startsWith("create ingredient"));

        WebElement nameInput = driver.findElement(By.id("name"));
        WebElement calInput = driver.findElement(By.id("caloriesPer100g"));
        Select categorySelect = new Select(driver.findElement(By.id("category")));

        Assertions.assertTrue(nameInput.getAttribute("value").isEmpty());
        Assertions.assertTrue(calInput.getAttribute("value").isEmpty());
        Assertions.assertEquals("VEGETABLE", categorySelect.getFirstSelectedOption().getText().toUpperCase());

        nameInput.sendKeys("Tested Tomato");
        Thread.sleep(500);
        calInput.sendKeys("18.0");
        Thread.sleep(500);
        categorySelect.selectByVisibleText("VEGETABLE");
        Thread.sleep(500);

        WebElement submitBtn = driver.findElement(By.cssSelector("form button[type='submit']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", submitBtn);
        Thread.sleep(500);
        submitBtn.click();

        wait.until(ExpectedConditions.urlContains("http://localhost:8081/ingredients"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("http://localhost:8081/ingredients"));
    }

    @Test
    @Order(2)
    void testCreateFormBackButton() throws InterruptedException {
        driver.get("http://localhost:8081/ingredients/create-ingredient");
        Thread.sleep(500);

        WebElement backBtn = driver.findElement(By.linkText("Back to Ingredients"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", backBtn);
        Thread.sleep(500);
        backBtn.click();

        wait.until(ExpectedConditions.urlToBe("http://localhost:8081/ingredients"));
        Assertions.assertEquals("http://localhost:8081/ingredients", driver.getCurrentUrl());
    }

    @Test
    @Order(3)
    void testEditFormInitialStateAndUpdate() throws InterruptedException {
        driver.get("http://localhost:8081/ingredients/edit/1");
        Thread.sleep(500);

        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h2")));
        Assertions.assertTrue(heading.getText().toLowerCase().startsWith("edit ingredient"));

        WebElement nameInput = driver.findElement(By.id("name"));
        WebElement calInput = driver.findElement(By.id("caloriesPer100g"));
        Select categorySelect = new Select(driver.findElement(By.id("category")));

        Assertions.assertFalse(nameInput.getAttribute("value").isEmpty());
        Assertions.assertFalse(calInput.getAttribute("value").isEmpty());

        nameInput.clear();
        Thread.sleep(500);
        nameInput.sendKeys("Tomato");
        Thread.sleep(500);
        calInput.clear();
        Thread.sleep(500);
        calInput.sendKeys("18.0");
        Thread.sleep(500);
        categorySelect.selectByVisibleText("VEGETABLE");
        Thread.sleep(500);

        WebElement submitBtn = driver.findElement(By.cssSelector("form button[type='submit']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", submitBtn);
        Thread.sleep(500);
        submitBtn.click();

        wait.until(ExpectedConditions.urlToBe("http://localhost:8081/ingredients"));
        Assertions.assertEquals("http://localhost:8081/ingredients", driver.getCurrentUrl());
    }

    @Test
    @Order(4)
    void testEditFormBackButton() throws InterruptedException {
        driver.get("http://localhost:8081/ingredients/edit/1");
        Thread.sleep(500);

        WebElement backBtn = driver.findElement(By.linkText("Back to Ingredients"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", backBtn);
        Thread.sleep(500);
        backBtn.click();

        wait.until(ExpectedConditions.urlToBe("http://localhost:8081/ingredients"));
        Assertions.assertEquals("http://localhost:8081/ingredients", driver.getCurrentUrl());
    }
}