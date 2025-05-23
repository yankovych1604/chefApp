package com.maksy.chefapp.mapper;

import com.maksy.chefapp.dto.DishIngredientDTO;
import com.maksy.chefapp.model.DishIngredient;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class DishIngredientMapperTest {

    private final DishIngredientMapper mapper = Mappers.getMapper(DishIngredientMapper.class);

    @Test
    void testToDishIngredientDTO() {
        DishIngredient entity = new DishIngredient(1L, 2L, 123.45);
        entity.setId(10L);

        DishIngredientDTO dto = mapper.toDishIngredientDTO(entity);

        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals(1L, dto.getDishId());
        assertEquals(2L, dto.getIngredientId());
        assertEquals(123.45, dto.getWeight());
        assertNull(dto.getIngredientName(), "ingredientName має бути null, бо його ігноруємо");
    }

    @Test
    void testToDishIngredient() {
        DishIngredientDTO dto = new DishIngredientDTO();
        dto.setId(5L);
        dto.setDishId(3L);
        dto.setIngredientId(7L);
        dto.setWeight(77.7);
        dto.setIngredientName("Tomato");

        DishIngredient entity = mapper.toDishIngredient(dto);

        assertNotNull(entity);
        assertEquals(5L, entity.getId());
        assertEquals(3L, entity.getDishId());
        assertEquals(7L, entity.getIngredientId());
        assertEquals(77.7, entity.getWeight());
    }

    @Test
    void testToDishIngredientDTO_withNullInput() {
        DishIngredientDTO dto = mapper.toDishIngredientDTO(null);
        assertNull(dto, "Якщо на вхід подати null, то й результат має бути null");
    }

    @Test
    void testToDishIngredient_withNullInput() {
        DishIngredient entity = mapper.toDishIngredient(null);
        assertNull(entity, "Якщо на вхід подати null, то й результат має бути null");
    }
}