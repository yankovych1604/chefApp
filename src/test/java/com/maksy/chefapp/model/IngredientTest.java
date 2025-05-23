package com.maksy.chefapp.model;

import com.maksy.chefapp.model.enums.IngredientCategory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class IngredientTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setName("Tomato");
        ingredient.setCaloriesPer100g(20.0);
        ingredient.setCategory(IngredientCategory.VEGETABLE);

        assertThat(ingredient.getId()).isEqualTo(1L);
        assertThat(ingredient.getName()).isEqualTo("Tomato");
        assertThat(ingredient.getCaloriesPer100g()).isEqualTo(20.0);
        assertThat(ingredient.getCategory()).isEqualTo(IngredientCategory.VEGETABLE);
    }

    @Test
    void testAllArgsConstructorValid() {
        Ingredient ingredient = new Ingredient("Carrot", 40.0, IngredientCategory.VEGETABLE);

        assertThat(ingredient.getName()).isEqualTo("Carrot");
        assertThat(ingredient.getCaloriesPer100g()).isEqualTo(40.0);
        assertThat(ingredient.getCategory()).isEqualTo(IngredientCategory.VEGETABLE);
    }

    @Test
    void testAllArgsConstructorThrowsOnEmptyName() {
        assertThatThrownBy(() -> new Ingredient("", 30.0, IngredientCategory.SPICE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name cannot be null or empty.");
    }

    @Test
    void testAllArgsConstructorThrowsOnZeroCalories() {
        assertThatThrownBy(() -> new Ingredient("Salt", 0.0, IngredientCategory.SPICE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Calories per 100g must be greater than zero.");
    }

    @Test
    void testGetCaloriesCalculation() {
        Ingredient ingredient = new Ingredient("Rice", 130.0, IngredientCategory.GRAIN);
        double calories = ingredient.getCalories(200);

        assertThat(calories).isEqualTo(260.0);
    }

    @Test
    void testToStringOutput() {
        Ingredient ingredient = new Ingredient("Milk", 50.0, IngredientCategory.DAIRY);
        ingredient.setId(5L);

        String result = ingredient.toString();

        assertThat(result).contains("id=5");
        assertThat(result).contains("name='Milk'");
        assertThat(result).contains("caloriesPer100g=50.0");
        assertThat(result).contains("category=DAIRY");
    }
}
