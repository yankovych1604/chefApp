package com.maksy.chefapp.service;

import com.maksy.chefapp.dto.DishIngredientDTO;
import com.maksy.chefapp.mapper.DishIngredientMapper;
import com.maksy.chefapp.model.DishIngredient;
import com.maksy.chefapp.model.Ingredient;
import com.maksy.chefapp.repository.DishIngredientRepository;
import com.maksy.chefapp.repository.IngredientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DishIngredientServiceTest {

    @InjectMocks
    private DishIngredientService dishIngredientService;

    @Mock
    private DishIngredientRepository dishIngredientRepository;

    @Mock
    private DishIngredientMapper dishIngredientMapper;

    @Mock
    private IngredientRepository ingredientRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllByDishId() {
        Long dishId = 1L;

        DishIngredient dishIngredient = new DishIngredient();
        dishIngredient.setIngredientId(10L);
        dishIngredient.setDishId(dishId);

        DishIngredientDTO dto = new DishIngredientDTO();
        dto.setIngredientId(10L);

        Ingredient ingredient = new Ingredient();
        ingredient.setId(10L);
        ingredient.setName("Tomato");

        when(dishIngredientRepository.findAllByDishId(dishId)).thenReturn(List.of(dishIngredient));
        when(dishIngredientMapper.toDishIngredientDTO(dishIngredient)).thenReturn(dto);
        when(ingredientRepository.findById(10L)).thenReturn(Optional.of(ingredient));

        List<DishIngredientDTO> result = dishIngredientService.findAllByDishId(dishId);

        assertEquals(1, result.size());
        assertEquals("Tomato", result.get(0).getIngredientName());
        verify(dishIngredientRepository).findAllByDishId(dishId);
    }

    @Test
    void testFindAllByDishId_IngredientMissing() {
        Long dishId = 2L;

        DishIngredient dishIngredient = new DishIngredient();
        dishIngredient.setIngredientId(99L);

        DishIngredientDTO dto = new DishIngredientDTO();
        dto.setIngredientId(99L);

        when(dishIngredientRepository.findAllByDishId(dishId)).thenReturn(List.of(dishIngredient));
        when(dishIngredientMapper.toDishIngredientDTO(dishIngredient)).thenReturn(dto);
        when(ingredientRepository.findById(99L)).thenReturn(Optional.empty());

        List<DishIngredientDTO> result = dishIngredientService.findAllByDishId(dishId);

        assertEquals(1, result.size());
        assertNull(result.get(0).getIngredientName());
    }

    @Test
    void testFindAll() {
        DishIngredient dishIngredient = new DishIngredient();
        DishIngredientDTO dto = new DishIngredientDTO();

        when(dishIngredientRepository.findAll()).thenReturn(List.of(dishIngredient));
        when(dishIngredientMapper.toDishIngredientDTO(dishIngredient)).thenReturn(dto);

        List<DishIngredientDTO> result = dishIngredientService.findAll();

        assertEquals(1, result.size());
        verify(dishIngredientRepository).findAll();
    }
}
