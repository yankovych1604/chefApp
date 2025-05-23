package com.maksy.chefapp.ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HomePageTest extends AbstractUiTest {

    @Test
    @Order(1)
    void testHomePageLoadsAndHasAddDishButton() throws InterruptedException {
        driver.get("http://localhost:8081/");
        Thread.sleep(500);

        WebElement addDishButton = driver.findElement(By.id("add-dish-button"));
        Assertions.assertTrue(addDishButton.isDisplayed(), "Кнопка додавання страви має бути видимою");

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", addDishButton);

        addDishButton.click();
        Thread.sleep(500);

        Assertions.assertEquals("http://localhost:8081/dishes/create-dish", driver.getCurrentUrl(), "Має бути перехід на створення страви");
    }

    @Test
    void testDishBlocksRedirectCorrectly() throws InterruptedException {
        driver.get("http://localhost:8081/");
        Thread.sleep(500);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebElement firstDishBlock = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".dish-block a")));

        firstDishBlock.click();
        Thread.sleep(500);

        Assertions.assertTrue(driver.getCurrentUrl().matches("http://localhost:8081/dishes/\\d+"));
    }
}
