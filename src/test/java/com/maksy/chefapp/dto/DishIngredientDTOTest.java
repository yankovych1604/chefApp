package com.maksy.chefapp.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DishIngredientDTOTest {
    @Test
    void testToString_ReturnsExpectedFormat() {
        DishIngredientDTO dto = new DishIngredientDTO();
        dto.setId(1L);
        dto.setDishId(10L);
        dto.setIngredientId(100L);
        dto.setIngredientName("Tomato");
        dto.setWeight(200.0);

        String toString = dto.toString();

        assertThat(toString).contains("id=1");
        assertThat(toString).contains("dishId=10");
        assertThat(toString).contains("ingredientId=100");
        assertThat(toString).contains("ingredientName='Tomato'");
        assertThat(toString).contains("weight=200.0");
    }

    @Test
    void testToString_WithNullValues() {
        DishIngredientDTO dto = new DishIngredientDTO();

        String toString = dto.toString();

        assertThat(toString).contains("id=null");
        assertThat(toString).contains("dishId=null");
        assertThat(toString).contains("ingredientId=null");
        assertThat(toString).contains("ingredientName='null'");
        assertThat(toString).contains("weight=0.0");
    }

    @Test
    void testIdAndDishIdSettersAndGetters() {
        DishIngredientDTO dto = new DishIngredientDTO();
        dto.setId(5L);
        dto.setDishId(42L);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getDishId()).isEqualTo(42L);
    }
}
