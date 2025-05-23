package com.maksy.chefapp.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DishIngredientTest {
    @Test
    void testNoArgsConstructorAndSetters() {
        DishIngredient ingredient = new DishIngredient();
        ingredient.setId(1L);
        ingredient.setDishId(2L);
        ingredient.setIngredientId(3L);
        ingredient.setWeight(150.5);

        assertThat(ingredient.getId()).isEqualTo(1L);
        assertThat(ingredient.getDishId()).isEqualTo(2L);
        assertThat(ingredient.getIngredientId()).isEqualTo(3L);
        assertThat(ingredient.getWeight()).isEqualTo(150.5);
    }

    @Test
    void testAllArgsConstructor() {
        DishIngredient ingredient = new DishIngredient(10L, 20L, 300.0);

        assertThat(ingredient.getDishId()).isEqualTo(10L);
        assertThat(ingredient.getIngredientId()).isEqualTo(20L);
        assertThat(ingredient.getWeight()).isEqualTo(300.0);
    }

    @Test
    void testToString() {
        DishIngredient ingredient = new DishIngredient(5L, 6L, 200.0);
        ingredient.setId(99L);

        String result = ingredient.toString();

        assertThat(result).contains("id=99");
        assertThat(result).contains("dishId=5");
        assertThat(result).contains("ingredientId=6");
        assertThat(result).contains("weight=200.0");
    }
}
