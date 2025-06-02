package com.maksy.chefapp.ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DishDetailsPageTest extends AbstractUiTest {

    @Test
    @Order(1)
    void testPageLoadsCorrectly() throws InterruptedException {
        driver.get("http://localhost:8081/dishes/1");
        Thread.sleep(500);

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h3")));
        Assertions.assertFalse(title.getText().isBlank());

        Assertions.assertFalse(driver.findElement(By.xpath("//p[strong[text()='Type:']]/span")).getText().isBlank());
        Assertions.assertFalse(driver.findElement(By.xpath("//p[strong[text()='Description:']]/span")).getText().isBlank());
        Assertions.assertFalse(driver.findElement(By.xpath("//p[strong[text()='Total Weight:']]/span")).getText().isBlank());
        Assertions.assertFalse(driver.findElement(By.xpath("//p[strong[text()='Total Calories:']]/span")).getText().isBlank());
    }

    @Test
    @Order(2)
    void testIngredientsTableContent() throws InterruptedException {
        driver.get("http://localhost:8081/dishes/1");
        Thread.sleep(500);

        List<WebElement> rows = driver.findElements(By.cssSelector("table tbody tr"));
        Assertions.assertFalse(rows.isEmpty(), "Має бути хоча б один інгредієнт");

        for (WebElement row : rows) {
            List<WebElement> cols = row.findElements(By.tagName("td"));
            Assertions.assertEquals(3, cols.size());
            for (WebElement col : cols) {
                Assertions.assertFalse(col.getText().isBlank(), "Усі комірки повинні містити текст");
            }
        }
    }

    @Test
    @Order(3)
    void testCalorieFilteringAndHighlighting() throws InterruptedException {
        driver.get("http://localhost:8081/dishes/1");
        Thread.sleep(500);

        WebElement form = driver.findElement(By.id("calorie-form"));

        WebElement minInput = form.findElement(By.name("minCalories"));
        WebElement maxInput = form.findElement(By.name("maxCalories"));

        minInput.clear();
        Thread.sleep(500);
        minInput.sendKeys("10");
        Thread.sleep(500);

        maxInput.clear();
        Thread.sleep(500);
        maxInput.sendKeys("500");
        Thread.sleep(500);

        WebElement submitBtn = form.findElement(By.cssSelector("button[type='submit']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", submitBtn);
        Thread.sleep(500);
        submitBtn.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table")));
        Thread.sleep(500);

        List<WebElement> highlighted = driver.findElements(By.cssSelector("table tr.table-success"));
        Assertions.assertFalse(highlighted.isEmpty(), "Має бути хоча б один підсвічений рядок");
    }

    @Test
    @Order(4)
    void testEditButtonOpensEditPage() throws InterruptedException {
        driver.get("http://localhost:8081/dishes/1");
        Thread.sleep(500);

        WebElement editBtn = driver.findElement(By.xpath("//a[contains(@href, '/dishes/edit/')]"));
        String href = editBtn.getAttribute("href");
        Assertions.assertTrue(href.contains("/dishes/edit/1"));
    }

    @Test
    @Order(5)
    void testDeleteButtonShowsPrompt() throws InterruptedException {
        driver.get("http://localhost:8081/dishes/1");
        Thread.sleep(500);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.confirm = function(msg) { window._confirmMessage = msg; return false; };");

        WebElement deleteBtn = driver.findElements(By.xpath("//form[contains(@action, '/dishes/delete/')]/button"))
                .stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Visible delete button not found"));

        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", deleteBtn);
        Thread.sleep(500);
        deleteBtn.click();

        wait.until(d -> js.executeScript("return window._confirmMessage") != null);
        String confirmText = (String) js.executeScript("return window._confirmMessage;");
        Assertions.assertTrue(confirmText.toLowerCase().contains("are you sure"));
    }

    @Test
    @Order(6)
    void testBackToListButton() throws InterruptedException {
        driver.get("http://localhost:8081/dishes/1");
        Thread.sleep(500);

        WebElement backBtn = driver.findElement(By.linkText("Back to List"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", backBtn);
        Thread.sleep(500);
        backBtn.click();

        wait.until(ExpectedConditions.urlToBe("http://localhost:8081/dishes/all"));
        Assertions.assertEquals("http://localhost:8081/dishes/all", driver.getCurrentUrl());
    }
}