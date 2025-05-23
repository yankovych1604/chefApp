package com.maksy.chefapp.mapper;

import com.maksy.chefapp.dto.DishDTO;
import com.maksy.chefapp.dto.DishIngredientDTO;
import com.maksy.chefapp.model.Dish;
import com.maksy.chefapp.model.DishIngredient;
import com.maksy.chefapp.model.enums.DishType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DishMapperTest {
    private final DishMapperImpl mapper = new DishMapperImpl();
    private final DishIngredientMapper mockDishIngredientMapper = mock(DishIngredientMapper.class);

    @BeforeEach
    void init() throws Exception {
        Field field = DishMapperImpl.class.getDeclaredField("dishIngredientMapper");
        field.setAccessible(true);
        field.set(mapper, mockDishIngredientMapper);
    }

    @Test
    void testDishToDishDTO() {
        Dish dish = new Dish();
        dish.setId(1L);
        dish.setName("Pizza");
        dish.setType(DishType.MAIN);
        dish.setDescription("Tasty pizza");
        dish.setTotalWeight(400);
        dish.setTotalCalories(900);

        DishIngredient ingredient = new DishIngredient(1L, 2L, 100.0);
        ingredient.setId(10L);
        dish.setDishIngredients(List.of(ingredient));

        DishIngredientDTO mockDto = new DishIngredientDTO();
        mockDto.setId(10L);
        when(mockDishIngredientMapper.toDishIngredientDTO(ingredient)).thenReturn(mockDto);

        DishDTO dto = mapper.dishToDishDTO(dish);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Pizza", dto.getName());
        assertEquals(DishType.MAIN, dto.getType());
        assertEquals("Tasty pizza", dto.getDescription());
        assertEquals(400, dto.getTotalWeight());
        assertEquals(900, dto.getTotalCalories());
        assertNotNull(dto.getDishIngredients());
        assertEquals(1, dto.getDishIngredients().size());
        assertEquals(10L, dto.getDishIngredients().get(0).getId());
    }

    @Test
    void testDishDTOToDish() {
        DishIngredientDTO dtoIngredient = new DishIngredientDTO();
        dtoIngredient.setId(10L);
        dtoIngredient.setIngredientId(2L);
        dtoIngredient.setWeight(100.0);

        DishDTO dto = new DishDTO();
        dto.setId(2L);
        dto.setName("Soup");
        dto.setType(DishType.SOUP);
        dto.setDescription("Hot soup");
        dto.setTotalWeight(300);
        dto.setTotalCalories(400);
        dto.setDishIngredients(List.of(dtoIngredient));

        DishIngredient mockEntity = new DishIngredient();
        mockEntity.setId(10L);
        when(mockDishIngredientMapper.toDishIngredient(dtoIngredient)).thenReturn(mockEntity);

        Dish dish = mapper.dishDTOToDish(dto);

        assertNotNull(dish);
        assertEquals(2L, dish.getId());
        assertEquals("Soup", dish.getName());
        assertEquals(DishType.SOUP, dish.getType());
        assertEquals("Hot soup", dish.getDescription());
        assertEquals(300, dish.getTotalWeight());
        assertEquals(400, dish.getTotalCalories());
        assertNotNull(dish.getDishIngredients());
        assertEquals(1, dish.getDishIngredients().size());
        assertEquals(10L, dish.getDishIngredients().get(0).getId());
    }

    @Test
    void testDishesToDishDTOs_withOneItem() {
        Dish dish = new Dish();
        dish.setId(1L);
        dish.setName("Borscht");
        dish.setType(DishType.SOUP);
        dish.setDescription("Ukrainian soup");
        dish.setTotalCalories(250);
        dish.setTotalWeight(300);
        dish.setDishIngredients(Collections.emptyList());

        when(mockDishIngredientMapper.toDishIngredientDTO(any())).thenReturn(null);

        List<DishDTO> result = mapper.dishesToDishDTOs(List.of(dish));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Borscht", result.get(0).getName());
    }

    @Test
    void testDishDTOsToDishes_withOneItem() {
        DishIngredientDTO ing = new DishIngredientDTO();
        DishIngredient entity = new DishIngredient();
        when(mockDishIngredientMapper.toDishIngredient(ing)).thenReturn(entity);

        DishDTO dto = new DishDTO();
        dto.setId(2L);
        dto.setName("Varenyky");
        dto.setType(DishType.MAIN);
        dto.setDishIngredients(List.of(ing));

        List<Dish> result = mapper.dishDTOsToDishes(List.of(dto));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Varenyky", result.get(0).getName());
        assertEquals(DishType.MAIN, result.get(0).getType());
    }

    @Test
    void testDishToDishDTO_withNullInput() {
        assertNull(mapper.dishToDishDTO(null));
    }

    @Test
    void testDishDTOToDish_withNullInput() {
        assertNull(mapper.dishDTOToDish(null));
    }

    @Test
    void testDishesToDishDTOs_emptyList() {
        List<DishDTO> dtos = mapper.dishesToDishDTOs(Collections.emptyList());
        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    void testDishDTOsToDishes_emptyList() {
        List<Dish> dishes = mapper.dishDTOsToDishes(Collections.emptyList());
        assertNotNull(dishes);
        assertTrue(dishes.isEmpty());
    }

    @Test
    void testDishesToDishDTOs_withNull() {
        assertNull(mapper.dishesToDishDTOs(null));
    }

    @Test
    void testDishDTOsToDishes_withNull() {
        assertNull(mapper.dishDTOsToDishes(null));
    }

    @Test
    void testDishIngredientListToDishIngredientDTOList_withNull() {
        List<DishIngredientDTO> result = mapper.dishIngredientListToDishIngredientDTOList(null);
        assertNull(result);
    }

    @Test
    void testDishIngredientDTOListToDishIngredientList_withNull() {
        List<DishIngredient> result = mapper.dishIngredientDTOListToDishIngredientList(null);
        assertNull(result);
    }
}