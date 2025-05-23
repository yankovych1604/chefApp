package com.maksy.chefapp.ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IngredientsPageTest extends AbstractUiTest {

    @Test
    @Order(1)
    void testPageLoadsWithIngredients() throws InterruptedException {
        driver.get("http://localhost:8081/ingredients");
        Thread.sleep(500);

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h2")));
        Assertions.assertEquals("All Ingredients", title.getText());

        List<WebElement> ingredientBlocks = driver.findElements(By.cssSelector(".ingredientBlock"));
        Assertions.assertFalse(ingredientBlocks.isEmpty(), "Має бути хоча б один інгредієнт");
    }

    @Test
    @Order(2)
    void testFilterByCategory() throws InterruptedException {
        driver.get("http://localhost:8081/ingredients");
        Thread.sleep(500);

        Select categorySelect = new Select(driver.findElement(By.id("ingredientCategory")));
        categorySelect.selectByValue("SPICE");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        Thread.sleep(500);

        wait.until(ExpectedConditions.urlContains("ingredientCategory=SPICE"));
        Thread.sleep(500);

        List<WebElement> categories = driver.findElements(By.xpath("//p[strong[text()='Category:']]/span"));
        Assertions.assertFalse(categories.isEmpty());

        for (WebElement cat : categories) {
            Assertions.assertEquals("SPICE", cat.getText());
        }
    }

    @Test
    @Order(3)
    void testFilterByCaloriesRange() throws InterruptedException {
        driver.get("http://localhost:8081/ingredients");
        Thread.sleep(500);

        WebElement fromInput = driver.findElement(By.id("caloriesFrom"));
        WebElement toInput = driver.findElement(By.id("caloriesTo"));

        fromInput.clear();
        Thread.sleep(500);
        fromInput.sendKeys("510");
        Thread.sleep(500);

        toInput.clear();
        Thread.sleep(500);
        toInput.sendKeys("940");
        Thread.sleep(500);

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("caloriesFrom=510"));
        Thread.sleep(500);

        List<WebElement> calories = driver.findElements(By.xpath("//p[strong[text()='Calories per 100g:']]/span"));

        for (WebElement cal : calories) {
            double value = Double.parseDouble(cal.getText());
            Assertions.assertTrue(value >= 510 && value <= 940, "Калорії мають бути між 510 і 940, але є: " + value);
        }
    }

    @Test
    @Order(4)
    void testSortByCaloriesDescending() throws InterruptedException {
        driver.get("http://localhost:8081/ingredients");
        Thread.sleep(500);

        Select sortSelect = new Select(driver.findElement(By.id("sortOrder")));
        sortSelect.selectByValue("highToLow");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        Thread.sleep(500);

        wait.until(ExpectedConditions.urlContains("sortOrder=highToLow"));
        Thread.sleep(500);

        List<WebElement> calories = driver.findElements(By.xpath("//p[strong[text()='Calories per 100g:']]/span"));

        List<Double> values = calories.stream()
                .map(e -> Double.parseDouble(e.getText()))
                .collect(Collectors.toList());

        List<Double> sorted = values.stream().sorted((a, b) -> Double.compare(b, a)).collect(Collectors.toList());
        Assertions.assertEquals(sorted, values, "Калорії мають бути відсортовані за спаданням");
    }

    @Test
    @Order(5)
    void testPaginationNavigation() throws InterruptedException {
        driver.get("http://localhost:8081/ingredients");
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
    @Order(6)
    void testClickOnIngredientCardRedirectsCorrectly() throws InterruptedException {
        driver.get("http://localhost:8081/ingredients");
        Thread.sleep(500);

        List<WebElement> ingredients = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".col-md-4.mb-4 a")));
        Assumptions.assumeTrue(!ingredients.isEmpty());

        WebElement first = ingredients.get(0);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", first);
        first.click();
        Thread.sleep(500);

        wait.until(driver -> driver.getCurrentUrl().matches("http://localhost:8081/ingredients/\\d+"));
        Assertions.assertTrue(driver.getCurrentUrl().matches("http://localhost:8081/ingredients/\\d+"));
    }
}
