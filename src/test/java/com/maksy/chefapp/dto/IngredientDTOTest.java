package com.maksy.chefapp.dto;

import com.maksy.chefapp.model.enums.IngredientCategory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IngredientDTOTest {
    @Test
    void testNoArgsConstructorAndSetters() {
        IngredientDTO dto = new IngredientDTO();
        dto.setId(1L);
        dto.setName("Carrot");
        dto.setCaloriesPer100g(41.0);
        dto.setCategory(IngredientCategory.VEGETABLE);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Carrot");
        assertThat(dto.getCaloriesPer100g()).isEqualTo(41.0);
        assertThat(dto.getCategory()).isEqualTo(IngredientCategory.VEGETABLE);
    }

    @Test
    void testAllArgsConstructor() {
        IngredientDTO dto = new IngredientDTO(2L, "Salt", 0.0, IngredientCategory.SPICE);

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getName()).isEqualTo("Salt");
        assertThat(dto.getCaloriesPer100g()).isEqualTo(0.0);
        assertThat(dto.getCategory()).isEqualTo(IngredientCategory.SPICE);
    }

    @Test
    void testToString() {
        IngredientDTO dto = new IngredientDTO(3L, "Milk", 60.0, IngredientCategory.DAIRY);
        String expected = "IngredientDTO{id=3, name='Milk', caloriesPer100g=60.0, category=DAIRY}";
        assertThat(dto.toString()).isEqualTo(expected);
    }
}