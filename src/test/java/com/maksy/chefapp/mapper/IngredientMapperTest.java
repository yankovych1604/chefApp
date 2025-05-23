package com.maksy.chefapp.mapper;

import com.maksy.chefapp.dto.IngredientDTO;
import com.maksy.chefapp.model.Ingredient;
import com.maksy.chefapp.model.enums.IngredientCategory;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class IngredientMapperTest {
    private final IngredientMapper mapper = Mappers.getMapper(IngredientMapper.class);

    @Test
    void testIngredientToIngredientDTO() {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setName("Butter");
        ingredient.setCaloriesPer100g(717.0);
        ingredient.setCategory(IngredientCategory.DAIRY);

        IngredientDTO dto = mapper.ingredientToIngredientDTO(ingredient);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Butter", dto.getName());
        assertEquals(717.0, dto.getCaloriesPer100g());
        assertEquals(IngredientCategory.DAIRY, dto.getCategory());
    }

    @Test
    void testIngredientDTOToIngredient() {
        IngredientDTO dto = new IngredientDTO();
        dto.setId(2L);
        dto.setName("Oregano");
        dto.setCaloriesPer100g(265.0);
        dto.setCategory(IngredientCategory.SPICE);

        Ingredient ingredient = mapper.ingredientDTOToIngredient(dto);

        assertNotNull(ingredient);
        assertEquals(2L, ingredient.getId());
        assertEquals("Oregano", ingredient.getName());
        assertEquals(265.0, ingredient.getCaloriesPer100g());
        assertEquals(IngredientCategory.SPICE, ingredient.getCategory());
    }

    @Test
    void testIngredientToIngredientDTO_withNullInput() {
        IngredientDTO dto = mapper.ingredientToIngredientDTO(null);
        assertNull(dto, "Якщо вхідний Ingredient null — результат має бути null");
    }

    @Test
    void testIngredientDTOToIngredient_withNullInput() {
        Ingredient ingredient = mapper.ingredientDTOToIngredient(null);
        assertNull(ingredient, "Якщо вхідний IngredientDTO null — результат має бути null");
    }
}
