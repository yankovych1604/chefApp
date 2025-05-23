package com.maksy.chefapp.service;

import com.maksy.chefapp.dto.DishDTO;
import com.maksy.chefapp.dto.DishIngredientDTO;
import com.maksy.chefapp.dto.IngredientDTO;
import com.maksy.chefapp.exception.EntityNotFoundException;
import com.maksy.chefapp.mapper.DishMapper;
import com.maksy.chefapp.model.Dish;
import com.maksy.chefapp.model.DishIngredient;
import com.maksy.chefapp.model.enums.DishType;
import com.maksy.chefapp.repository.DishRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DishServiceTest {

    @InjectMocks
    private DishService dishService;

    @Mock
    private DishRepository dishRepository;

    @Mock
    private DishMapper dishMapper;

    @Mock
    private DishIngredientService dishIngredientService;

    @Mock
    private IngredientService ingredientService;

    @Mock
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllDishes_ShouldReturnDishDTOList() {
        List<Dish> dishes = List.of(new Dish("Caesar Salad", DishType.SALAD));
        List<DishDTO> dishDTOs = List.of(new DishDTO());
        when(dishRepository.findAll()).thenReturn(dishes);
        when(dishMapper.dishesToDishDTOs(dishes)).thenReturn(dishDTOs);

        List<DishDTO> result = dishService.getAllDishes();

        assertEquals(dishDTOs, result);
        verify(dishRepository).findAll();
        verify(dishMapper).dishesToDishDTOs(dishes);
    }

    @Test
    void getDishById_WhenExists_ShouldReturnDishDTO() {
        Dish dish = new Dish("Borscht", DishType.SOUP);
        when(dishRepository.findById(1L)).thenReturn(Optional.of(dish));
        DishDTO dishDTO = new DishDTO();
        when(dishMapper.dishToDishDTO(dish)).thenReturn(dishDTO);

        DishDTO result = dishService.getDishById(1L);

        assertEquals(dishDTO, result);
    }

    @Test
    void getDishById_WhenNotFound_ShouldThrowEntityNotFoundException() {
        when(dishRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> dishService.getDishById(1L));
    }

    @Test
    void getAllDishesFiltered_ShouldReturnFilteredPage() {
        DishType dishType = DishType.MAIN;
        Pageable pageable = PageRequest.of(0, 10);
        List<Dish> dishes = List.of(new Dish("Steak", DishType.MAIN));
        Page<Dish> dishesPage = new PageImpl<>(dishes);

        when(dishRepository.findFilteredDishes(dishType, pageable)).thenReturn(dishesPage);

        List<DishDTO> dishDTOs = List.of(new DishDTO());
        when(dishMapper.dishToDishDTO(any())).thenReturn(dishDTOs.get(0));

        Page<DishDTO> result = dishService.getAllDishesFiltered(dishType, pageable);

        assertEquals(1, result.getContent().size());
    }

    @Test
    void createDish_ShouldSaveAndReturnDishDTO() {
        DishDTO dishDTO = new DishDTO();
        Dish dish = new Dish("Cake", DishType.DESSERT);

        when(dishMapper.dishDTOToDish(dishDTO)).thenReturn(dish);
        when(dishRepository.save(dish)).thenReturn(dish);
        when(dishMapper.dishToDishDTO(dish)).thenReturn(dishDTO);

        DishDTO result = dishService.createDish(dishDTO);

        assertEquals(dishDTO, result);
        verify(dishRepository).save(dish);
    }

    @Test
    void createDish_WithIngredients_ShouldSaveAndAssignDishId() {
        DishDTO dishDTO = new DishDTO();
        Dish dish = new Dish("Cake", DishType.DESSERT);
        dish.setDishIngredients(List.of(new DishIngredient(null, 1L, 100)));

        Dish savedDish = new Dish("Cake", DishType.DESSERT);
        savedDish.setId(1L);

        when(dishMapper.dishDTOToDish(dishDTO)).thenReturn(dish);
        when(dishRepository.save(any(Dish.class))).thenReturn(savedDish);
        when(dishMapper.dishToDishDTO(any(Dish.class))).thenReturn(dishDTO);

        DishDTO result = dishService.createDish(dishDTO);

        assertEquals(dishDTO, result);
        verify(dishRepository, times(3)).save(any(Dish.class));
    }

    @Test
    void createDish_WithDishIngredients_ShouldAssignDishIdOnlyIfIdIsNull() {
        DishDTO dishDTO = new DishDTO();
        Dish dish = new Dish("Salad", DishType.SALAD);
        Dish savedDish = new Dish("Salad", DishType.SALAD);
        savedDish.setId(123L);

        DishIngredient ing1 = new DishIngredient();
        ing1.setId(null);
        ing1.setWeight(100);

        DishIngredient ing2 = new DishIngredient();
        ing2.setId(456L);
        ing2.setWeight(200);

        dish.setDishIngredients(List.of(ing1, ing2));
        dishDTO.setDishIngredients(List.of());

        when(dishMapper.dishDTOToDish(dishDTO)).thenReturn(dish);
        when(dishRepository.save(any(Dish.class))).thenReturn(savedDish);
        when(dishMapper.dishToDishDTO(any(Dish.class))).thenReturn(dishDTO);

        dishService.createDish(dishDTO);

        assertThat(ing1.getDishId()).isEqualTo(savedDish.getId());
        assertThat(ing2.getDishId()).isNull();
    }

    @Test
    void deleteDish_WhenDishExists_ShouldDeleteDishAndIngredients() {
        DishIngredient ingredient = new DishIngredient();
        Dish dish = new Dish();
        dish.setDishIngredients(List.of(ingredient));

        when(entityManager.find(Dish.class, 1L)).thenReturn(dish);

        dishService.deleteDish(1L);

        verify(entityManager).remove(ingredient);
        verify(entityManager).remove(dish);
    }

    @Test
    void deleteDish_WhenDishNotFound_ShouldThrowEntityNotFoundException() {
        when(entityManager.find(Dish.class, 1L)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> dishService.deleteDish(1L));
    }

    @Test
    void updateDish_ShouldUpdateDishAndRecalculateCalories() {
        Long dishId = 1L;
        DishDTO dishDTO = new DishDTO();
        dishDTO.setId(dishId);
        Dish dish = new Dish();
        dish.setId(dishId);

        DishIngredient newIngredient = new DishIngredient(1L, 2L, 150);
        dish.setDishIngredients(List.of(newIngredient));

        when(dishMapper.dishDTOToDish(dishDTO)).thenReturn(dish);
        when(dishRepository.save(any(Dish.class))).thenReturn(dish);
        when(dishMapper.dishToDishDTO(dish)).thenReturn(dishDTO);

        DishIngredientDTO dishIngredientDTO = new DishIngredientDTO();
        dishIngredientDTO.setIngredientId(2L);
        dishIngredientDTO.setWeight(150);
        List<DishIngredientDTO> dishIngredientDTOList = List.of(dishIngredientDTO);

        when(dishIngredientService.findAllByDishId(dishId)).thenReturn(dishIngredientDTOList);

        IngredientDTO ingredientDTO = new IngredientDTO();
        ingredientDTO.setId(2L);
        ingredientDTO.setCaloriesPer100g(200.0);
        when(ingredientService.findAllById(List.of(2L))).thenReturn(List.of(ingredientDTO));

        DishDTO result = dishService.updateDish(dishId, dishDTO, null);

        assertEquals(dishDTO, result);
        verify(dishRepository, atLeastOnce()).save(dish);
        verify(dishIngredientService).findAllByDishId(dishId);
        verify(ingredientService).findAllById(List.of(2L));

        assertEquals(150, dish.getTotalWeight());
        assertEquals(300, dish.getTotalCalories());
    }

    @Test
    void updateDish_ShouldRemoveIngredients_WhenDeleteIdsProvided() {
        Long dishId = 1L;
        DishDTO dishDTO = new DishDTO();
        dishDTO.setId(dishId);
        Dish dish = new Dish();
        dish.setId(dishId);

        DishIngredient ing1 = new DishIngredient(dishId, 10L, 50);
        ing1.setId(1L);
        DishIngredient ing2 = new DishIngredient(dishId, 11L, 100);
        ing2.setId(2L);
        dish.setDishIngredients(new ArrayList<>(List.of(ing1, ing2)));

        when(dishMapper.dishDTOToDish(dishDTO)).thenReturn(dish);
        when(dishRepository.save(any(Dish.class))).thenReturn(dish);
        when(dishMapper.dishToDishDTO(dish)).thenReturn(dishDTO);

        when(dishIngredientService.findAllByDishId(dishId)).thenReturn(List.of());
        when(ingredientService.findAllById(List.of())).thenReturn(List.of());

        DishDTO result = dishService.updateDish(dishId, dishDTO, List.of(1L));

        assertEquals(dishDTO, result);
        assertEquals(1, dish.getDishIngredients().size());
        assertEquals(2L, dish.getDishIngredients().get(0).getId());
    }

    @Test
    void updateCaloriesWeight_ShouldCalculateCorrectly_WhenIngredientExists() {
        Long dishId = 5L;
        Dish dish = new Dish();
        dish.setId(dishId);

        DishIngredientDTO dishIngredientDTO = new DishIngredientDTO();
        dishIngredientDTO.setIngredientId(10L);
        dishIngredientDTO.setWeight(200);

        IngredientDTO ingredientDTO = new IngredientDTO();
        ingredientDTO.setId(10L);
        ingredientDTO.setCaloriesPer100g(150.0);

        when(dishIngredientService.findAllByDishId(dishId)).thenReturn(List.of(dishIngredientDTO));
        when(ingredientService.findAllById(List.of(10L))).thenReturn(List.of(ingredientDTO));
        when(dishRepository.save(any(Dish.class))).thenReturn(dish);

        dishService.updateCaloriesWeight(dish);

        assertEquals(200.0, dish.getTotalWeight());
        assertEquals(300.0, dish.getTotalCalories());

        verify(dishRepository).save(dish);
        verify(ingredientService).findAllById(List.of(10L));
    }

    @Test
    void updateCaloriesWeight_ShouldSkipWhenIngredientIsMissing() {
        Long dishId = 6L;
        Dish dish = new Dish();
        dish.setId(dishId);

        DishIngredientDTO dishIngredientDTO = new DishIngredientDTO();
        dishIngredientDTO.setIngredientId(100L);
        dishIngredientDTO.setWeight(250);

        when(dishIngredientService.findAllByDishId(dishId)).thenReturn(List.of(dishIngredientDTO));
        when(ingredientService.findAllById(List.of(100L))).thenReturn(List.of());

        when(dishRepository.save(any(Dish.class))).thenReturn(dish);

        dishService.updateCaloriesWeight(dish);

        assertEquals(0.0, dish.getTotalWeight());
        assertEquals(0.0, dish.getTotalCalories());

        verify(dishRepository).save(dish);
    }

    @Test
    void get3Dishes_ShouldReturnMappedList() {
        Dish dish1 = new Dish("Cake", DishType.DESSERT);
        Dish dish2 = new Dish("Soup", DishType.SOUP);
        Dish dish3 = new Dish("Salad", DishType.SALAD);

        DishDTO dto1 = new DishDTO();
        DishDTO dto2 = new DishDTO();
        DishDTO dto3 = new DishDTO();

        when(dishRepository.findTop3ByOrderByIdDesc()).thenReturn(List.of(dish1, dish2, dish3));
        when(dishMapper.dishToDishDTO(dish1)).thenReturn(dto1);
        when(dishMapper.dishToDishDTO(dish2)).thenReturn(dto2);
        when(dishMapper.dishToDishDTO(dish3)).thenReturn(dto3);

        List<DishDTO> result = dishService.get3Dishes();

        assertThat(result).containsExactly(dto1, dto2, dto3);
    }

    @Test
    void get3Dishes_ShouldReturnEmptyList_WhenNoDishes() {
        when(dishRepository.findTop3ByOrderByIdDesc()).thenReturn(emptyList());

        List<DishDTO> result = dishService.get3Dishes();

        assertThat(result).isEmpty();
    }
}
