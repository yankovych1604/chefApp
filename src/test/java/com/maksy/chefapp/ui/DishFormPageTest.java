package com.maksy.chefapp.ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DishFormPageTest extends AbstractUiTest {

    @Test
    @Order(1)
    void testInitialStateOfCreateForm() {
        driver.get("http://localhost:8081/dishes/create-dish");

        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h2")));
        Assertions.assertTrue(heading.getText().toLowerCase().startsWith("create dish"));

        Assertions.assertEquals("", driver.findElement(By.id("name")).getAttribute("value"));
        Assertions.assertEquals("", driver.findElement(By.id("description")).getAttribute("value"));

        Select typeSelect = new Select(driver.findElement(By.id("type")));
        Assertions.assertEquals("SALAD", typeSelect.getFirstSelectedOption().getText().toUpperCase());
    }

    @Test
    @Order(2)
    void testSaveWithoutIngredientsShowsWarning() {
        driver.get("http://localhost:8081/dishes/create-dish");

        driver.findElement(By.id("saveChangesBtn")).click();

        WebElement warning = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ingredientWarning")));
        Assertions.assertTrue(warning.isDisplayed());
        Assertions.assertTrue(warning.getText().contains("Please add at least one ingredient"));
    }

    @Test
    @Order(3)
    void testAddIngredientFieldsAppear() {
        driver.get("http://localhost:8081/dishes/create-dish");

        driver.findElement(By.xpath("//button[text()='Add Another Ingredient']")).click();

        List<WebElement> rows = driver.findElements(By.cssSelector("#newIngredientsContainer .row"));
        Assertions.assertEquals(1, rows.size());

        WebElement select = rows.get(0).findElement(By.tagName("select"));
        WebElement input = rows.get(0).findElement(By.tagName("input"));
        WebElement removeBtn = rows.get(0).findElement(By.tagName("button"));

        Assertions.assertEquals("", input.getAttribute("value"));
        Assertions.assertEquals("", select.getAttribute("value"));
        Assertions.assertEquals("Remove", removeBtn.getText());
    }

    @Test
    @Order(4)
    void testRemoveIngredientFields() {
        driver.get("http://localhost:8081/dishes/create-dish");

        driver.findElement(By.xpath("//button[text()='Add Another Ingredient']")).click();
        WebElement removeBtn = driver.findElement(By.cssSelector("#newIngredientsContainer .row button"));
        removeBtn.click();

        List<WebElement> rowsAfter = driver.findElements(By.cssSelector("#newIngredientsContainer .row"));
        Assertions.assertEquals(0, rowsAfter.size());
    }

    @Test
    @Order(5)
    void testCreateDishWithManyIngredients() throws InterruptedException {
        driver.get("http://localhost:8081/dishes/create-dish");

        driver.findElement(By.id("name")).sendKeys("Tested Greek Salad");
        driver.findElement(By.id("description")).sendKeys("Tested classic Mediterranean salad");

        int[] ingredientIndices = {1, 3, 6, 10, 11, 12, 23};
        int[] weights = {100, 50, 30, 2, 15, 1, 50};

        for (int i = 0; i < ingredientIndices.length; i++) {
            WebElement addButton = driver.findElement(By.xpath("//button[text()='Add Another Ingredient']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", addButton);
            wait.until(ExpectedConditions.elementToBeClickable(addButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addButton);

            Thread.sleep(500);

            List<WebElement> rows = driver.findElements(By.cssSelector("#newIngredientsContainer .row"));
            WebElement latestRow = rows.get(rows.size() - 1);

            Select ingredientSelect = new Select(latestRow.findElement(By.tagName("select")));
            WebElement weightInput = latestRow.findElement(By.tagName("input"));

            ingredientSelect.selectByIndex(ingredientIndices[i]);
            Thread.sleep(500);

            weightInput.sendKeys(String.valueOf(weights[i]));
            Thread.sleep(500);
        }

        driver.findElement(By.id("saveChangesBtn")).click();

        wait.until(ExpectedConditions.urlContains("/dishes/all"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/dishes/all"));
    }


    @Test
    @Order(6)
    void testEditDishPageHasFieldsAndValues() {
        driver.get("http://localhost:8081/dishes/edit/1");

        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h2")));
        Assertions.assertTrue(heading.getText().toLowerCase().contains("edit dish"));

        WebElement nameInput = driver.findElement(By.id("name"));
        WebElement descriptionInput = driver.findElement(By.id("description"));
        WebElement typeSelect = driver.findElement(By.id("type"));

        Assertions.assertFalse(nameInput.getAttribute("value").isEmpty());
        Assertions.assertFalse(descriptionInput.getAttribute("value").isEmpty());
        Assertions.assertFalse(new Select(typeSelect).getFirstSelectedOption().getText().isEmpty());
    }

    @Test
    @Order(7)
    void testSaveChangesRedirectsToAll() throws InterruptedException {
        driver.get("http://localhost:8081/dishes/edit/1");

        Thread.sleep(500);

        WebElement saveBtn = driver.findElement(By.id("saveChangesBtn"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", saveBtn);

        Thread.sleep(500);

        saveBtn.click();

        wait.until(ExpectedConditions.urlContains("/dishes/all"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/dishes/all"));
    }

    @Test
    @Order(8)
    void testAddNewIngredientFieldsAppear() throws InterruptedException {
        driver.get("http://localhost:8081/dishes/edit/1");

        Thread.sleep(500);

        WebElement addButton = driver.findElement(By.xpath("//button[text()='Add Another Ingredient']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", addButton);
        wait.until(ExpectedConditions.elementToBeClickable(addButton));

        Thread.sleep(500);

        addButton.click();

        WebElement row = driver.findElement(By.cssSelector("#newIngredientsContainer .row"));
        Select select = new Select(row.findElement(By.tagName("select")));
        WebElement input = row.findElement(By.tagName("input"));
        WebElement removeBtn = row.findElement(By.tagName("button"));

        Assertions.assertEquals("", select.getFirstSelectedOption().getAttribute("value"));
        Assertions.assertEquals("", input.getAttribute("value"));
        Assertions.assertEquals("Remove", removeBtn.getText());
    }

    @Test
    @Order(9)
    void testRemoveNewIngredient() throws InterruptedException {
        driver.get("http://localhost:8081/dishes/edit/1");

        Thread.sleep(500);

        WebElement addBtn = driver.findElement(By.xpath("//button[text()='Add Another Ingredient']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", addBtn);

        Thread.sleep(500);

        addBtn.click();

        Thread.sleep(500);

        WebElement removeBtn = driver.findElement(By.cssSelector("#newIngredientsContainer .row button"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", removeBtn);

        Thread.sleep(500);

        removeBtn.click();

        List<WebElement> rows = driver.findElements(By.cssSelector("#newIngredientsContainer .row"));
        Assertions.assertEquals(0, rows.size());
    }

    @Test
    @Order(10)
    void testDeleteExistingIngredient() throws InterruptedException {
        driver.get("http://localhost:8081/dishes/edit/1");

        List<WebElement> before = driver.findElements(By.cssSelector(".delete-ingredient-btn"));
        if (before.isEmpty()) {
            Assertions.fail("No existing ingredients to delete.");
        }

        int initialCount = before.size();

        WebElement deleteBtn = before.get(6);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", deleteBtn);

        Thread.sleep(500);

        deleteBtn.click();

        Thread.sleep(500);

        WebElement saveBtn = driver.findElement(By.id("saveChangesBtn"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", saveBtn);

        Thread.sleep(500);

        saveBtn.click();

        Thread.sleep(500);

        wait.until(ExpectedConditions.urlContains("/dishes/all"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/dishes/all"));

        driver.get("http://localhost:8081/dishes/edit/1");

        List<WebElement> after = driver.findElements(By.cssSelector(".delete-ingredient-btn"));
        Assertions.assertEquals(initialCount - 1, after.size());
    }

    @Test
    @Order(11)
    void testAddIngredientAndSave() throws InterruptedException {
        driver.get("http://localhost:8081/dishes/edit/1");

        Thread.sleep(500);

        WebElement addBtn = driver.findElement(By.xpath("//button[text()='Add Another Ingredient']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", addBtn);

        Thread.sleep(500);

        addBtn.click();

        WebElement row = driver.findElement(By.cssSelector("#newIngredientsContainer .row"));
        Select select = new Select(row.findElement(By.tagName("select")));
        WebElement input = row.findElement(By.tagName("input"));

        select.selectByIndex(23);
        Thread.sleep(500);

        input.sendKeys("50");
        Thread.sleep(500);

        Thread.sleep(500);

        WebElement saveButton = driver.findElement(By.id("saveChangesBtn"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", saveButton);

        Thread.sleep(500);

        saveButton.click();

        wait.until(ExpectedConditions.urlContains("/dishes/all"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/dishes/all"));
    }
}