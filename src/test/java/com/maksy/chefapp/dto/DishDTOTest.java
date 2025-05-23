package com.maksy.chefapp.dto;

import com.maksy.chefapp.model.enums.DishType;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DishDTOTest {
    @Test
    void testDishDTO_SettersAndGetters() {
        DishDTO dish = new DishDTO();
        dish.setId(1L);
        dish.setName("Borscht");
        dish.setType(DishType.SOUP);
        dish.setDescription("Traditional beet soup");
        dish.setTotalCalories(123.4);
        dish.setTotalWeight(456.7);
        dish.setDishIngredients(Collections.emptyList());

        assertThat(dish.getId()).isEqualTo(1L);
        assertThat(dish.getName()).isEqualTo("Borscht");
        assertThat(dish.getType()).isEqualTo(DishType.SOUP);
        assertThat(dish.getDescription()).isEqualTo("Traditional beet soup");
        assertThat(dish.getTotalCalories()).isEqualTo(123.4);
        assertThat(dish.getTotalWeight()).isEqualTo(456.7);
        assertThat(dish.getDishIngredients()).isEmpty();
    }

    @Test
    void testToString_NotNull() {
        DishDTO dish = new DishDTO();
        dish.setName("Pizza");
        dish.setType(DishType.MAIN);
        dish.setDescription("Delicious");

        String toString = dish.toString();

        assertThat(toString).contains("Pizza");
        assertThat(toString).contains("MAIN");
        assertThat(toString).contains("Delicious");
    }

    @Test
    void testToString_Full() {
        DishIngredientDTO ingredientDTO = new DishIngredientDTO();
        ingredientDTO.setId(10L);
        ingredientDTO.setDishId(1L);
        ingredientDTO.setIngredientId(100L);
        ingredientDTO.setIngredientName("Tomato");
        ingredientDTO.setWeight(50.0);

        DishDTO dish = new DishDTO();
        dish.setId(5L);
        dish.setName("Pasta");
        dish.setType(DishType.MAIN);
        dish.setDescription("With tomato sauce");
        dish.setTotalCalories(600.0);
        dish.setTotalWeight(300.0);
        dish.setDishIngredients(List.of(ingredientDTO));

        String toString = dish.toString();

        assertThat(toString).contains("id=5");
        assertThat(toString).contains("name='Pasta'");
        assertThat(toString).contains("MAIN");
        assertThat(toString).contains("With tomato sauce");
        assertThat(toString).contains("600.0");
        assertThat(toString).contains("300.0");
        assertThat(toString).contains("ingredientName='Tomato'");
    }
}