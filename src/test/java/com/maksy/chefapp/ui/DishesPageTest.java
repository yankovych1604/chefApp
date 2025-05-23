package com.maksy.chefapp.ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DishesPageTest extends AbstractUiTest {

    @Test
    @Order(1)
    void testDishesPageLoadsWithCards() throws InterruptedException {
        driver.get("http://localhost:8081/dishes/all");
        Thread.sleep(500);

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h2")));
        Assertions.assertEquals("All Dishes", title.getText());

        List<WebElement> cards = driver.findElements(By.cssSelector(".card"));
        Assertions.assertFalse(cards.isEmpty(), "Має бути хоча б одна страва");
    }

    @Test
    @Order(2)
    void testFilteringByDishType() throws InterruptedException {
        driver.get("http://localhost:8081/dishes/all");
        Thread.sleep(500);

        Select dishTypeSelect = new Select(driver.findElement(By.id("dishType")));
        dishTypeSelect.selectByValue("SALAD");
        Thread.sleep(500);

        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("dishType=SALAD"));
        Thread.sleep(500);

        List<WebElement> types = driver.findElements(By.xpath("//p[strong[text()='Type:']]/span"));
        Assertions.assertFalse(types.isEmpty(), "Після фільтру має бути хоч одна страва");

        for (WebElement type : types) {
            Assertions.assertEquals("SALAD", type.getText(), "Тип страви має бути SALAD");
        }
    }

    @Test
    @Order(3)
    void testPaginationNavigation() throws InterruptedException {
        driver.get("http://localhost:8081/dishes/all");
        Thread.sleep(500);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        List<WebElement> pages = driver.findElements(By.cssSelector(".pagination .page-link"));
        Assumptions.assumeTrue(pages.size() > 1, "Пагінація відсутня — пропускаємо тест");

        WebElement page2 = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".pagination .page-link[href*='page=1']")));

        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", page2);
        Thread.sleep(500);

        try {
            page2.click();
        } catch (ElementClickInterceptedException e) {
            js.executeScript("arguments[0].click();", page2);
        }

        wait.until(ExpectedConditions.urlContains("page=1"));
        Thread.sleep(500);
        Assertions.assertTrue(driver.getCurrentUrl().contains("page=1"), "URL має містити page=1 (друга сторінка)");

        WebElement previous = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".pagination .page-link[href*='page=0']")));

        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", previous);
        Thread.sleep(500);

        try {
            previous.click();
        } catch (ElementClickInterceptedException e) {
            js.executeScript("arguments[0].click();", previous);
        }

        wait.until(ExpectedConditions.urlContains("page=0"));
        Thread.sleep(500);
        Assertions.assertTrue(driver.getCurrentUrl().contains("page=0"), "URL має містити page=0 (перша сторінка)");

        WebElement next = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".pagination .page-link[href*='page=1']")));

        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", next);
        Thread.sleep(500);

        try {
            next.click();
        } catch (ElementClickInterceptedException e) {
            js.executeScript("arguments[0].click();", next);
        }

        wait.until(ExpectedConditions.urlContains("page=1"));
        Thread.sleep(500);
        Assertions.assertTrue(driver.getCurrentUrl().contains("page=1"), "URL має містити page=1 після Next");

        WebElement active = driver.findElement(By.cssSelector(".pagination .page-item.active .page-link"));
        Assertions.assertEquals("2", active.getText().trim(), "Активна сторінка має бути 2");
    }

    @Test
    @Order(4)
    void testDishBlockNavigation() throws InterruptedException {
        driver.get("http://localhost:8081/dishes/all");
        Thread.sleep(500);

        List<WebElement> dishes = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".dishBlock")));

        Assumptions.assumeTrue(!dishes.isEmpty(), "Жодної страви не знайдено — тест пропущено");

        WebElement firstDish = dishes.get(0);

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", firstDish);
        Thread.sleep(500);

        try {
            firstDish.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstDish);
        }

        wait.until(driver -> driver.getCurrentUrl().matches("http://localhost:8081/dishes/\\d+"));
        Thread.sleep(500);

        Assertions.assertTrue(driver.getCurrentUrl().matches("http://localhost:8081/dishes/\\d+"), "URL має бути сторінкою страви типу /dishes/N");
    }
}
