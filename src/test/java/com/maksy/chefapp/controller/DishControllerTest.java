package com.maksy.chefapp.controller;

import com.maksy.chefapp.dto.DishDTO;
import com.maksy.chefapp.dto.DishIngredientDTO;
import com.maksy.chefapp.dto.IngredientDTO;
import com.maksy.chefapp.model.enums.DishType;
import com.maksy.chefapp.service.DishIngredientService;
import com.maksy.chefapp.service.DishService;
import com.maksy.chefapp.service.IngredientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DishControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DishService dishService;

    @InjectMocks
    private DishController dishController;

    @Mock
    private DishIngredientService dishIngredientService;

    @Mock
    private IngredientService ingredientService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dishController).build();
    }

    @Test
    void testShowAllDishes() throws Exception {
        mockMvc.perform(get("/dishes/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("dishes"));
    }

    @Test
    void testShowAllDishes_WithDishType() throws Exception {
        DishDTO dish = new DishDTO();
        Page<DishDTO> mockPage = new PageImpl<>(List.of(dish));

        Mockito.when(dishService.getAllDishesFiltered(eq(DishType.SALAD), any())).thenReturn(mockPage);

        mockMvc.perform(get("/dishes/all")
                        .param("dishType", "SALAD"))
                .andExpect(status().isOk())
                .andExpect(view().name("dishes"))
                .andExpect(model().attribute("dishes", mockPage))
                .andExpect(model().attribute("dishType", "SALAD"));
    }

    @Test
    void testShowAllDishes_WithoutDishType() throws Exception {
        Page<DishDTO> mockPage = new PageImpl<>(List.of());

        Mockito.when(dishService.getAllDishesFiltered(isNull(), any())).thenReturn(mockPage);

        mockMvc.perform(get("/dishes/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("dishes"))
                .andExpect(model().attribute("dishes", mockPage))
                .andExpect(model().attribute("dishType", (Object) null));
    }

    @Test
    void testShowDishById_withCalorieRange_shouldHighlightCorrectIngredients() throws Exception {
        Long dishId = 1L;

        DishDTO dishDTO = new DishDTO();
        dishDTO.setId(dishId);
        dishDTO.setName("Greek Salad");

        DishIngredientDTO di1 = new DishIngredientDTO();
        di1.setIngredientId(10L);
        di1.setWeight(100.0);
        di1.setIngredientName("Salt");

        DishIngredientDTO di2 = new DishIngredientDTO();
        di2.setIngredientId(11L);
        di2.setWeight(50.0);
        di2.setIngredientName("Olive Oil");

        List<DishIngredientDTO> dishIngredients = List.of(di1, di2);

        IngredientDTO ingr1 = new IngredientDTO(10L, "Salt", 10.0, null);
        IngredientDTO ingr2 = new IngredientDTO(11L, "Olive Oil", 898.0, null);

        Mockito.when(dishService.getDishById(dishId)).thenReturn(dishDTO);
        Mockito.when(dishIngredientService.findAllByDishId(dishId)).thenReturn(dishIngredients);
        Mockito.when(ingredientService.findAllById(List.of(10L, 11L))).thenReturn(List.of(ingr1, ingr2));

        mockMvc.perform(get("/dishes/1")
                        .param("minCalories", "5")
                        .param("maxCalories", "100"))
                .andExpect(status().isOk())
                .andExpect(view().name("dishDetails"))
                .andExpect(model().attribute("dish", dishDTO))
                .andExpect(model().attribute("dishIngredients", dishIngredients))
                .andExpect(model().attributeExists("ingredientCalories"))
                .andExpect(model().attributeExists("minCalories"))
                .andExpect(model().attributeExists("maxCalories"));
    }

    @Test
    void testShowDishById_withoutCalorieFilter_shouldNotHighlightAny() throws Exception {
        Long dishId = 1L;

        DishDTO dishDTO = new DishDTO();
        dishDTO.setId(dishId);

        DishIngredientDTO di = new DishIngredientDTO();
        di.setIngredientId(100L);
        di.setWeight(100.0);

        IngredientDTO ingr = new IngredientDTO(100L, "Test", 50.0, null);

        Mockito.when(dishService.getDishById(dishId)).thenReturn(dishDTO);
        Mockito.when(dishIngredientService.findAllByDishId(dishId)).thenReturn(List.of(di));
        Mockito.when(ingredientService.findAllById(List.of(100L))).thenReturn(List.of(ingr));

        mockMvc.perform(get("/dishes/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("dishDetails"))
                .andExpect(model().attribute("dish", dishDTO))
                .andExpect(model().attributeExists("ingredientCalories"))
                .andExpect(model().attribute("minCalories", (Object) null))
                .andExpect(model().attribute("maxCalories", (Object) null));
    }

    @Test
    void testShowDishById_withOnlyMinCalories_shouldHighlightAboveMin() throws Exception {
        Long dishId = 2L;

        DishDTO dishDTO = new DishDTO();
        dishDTO.setId(dishId);

        DishIngredientDTO di = new DishIngredientDTO();
        di.setIngredientId(200L);
        di.setWeight(200.0);

        IngredientDTO ingr = new IngredientDTO(200L, "Test", 50.0, null);

        Mockito.when(dishService.getDishById(dishId)).thenReturn(dishDTO);
        Mockito.when(dishIngredientService.findAllByDishId(dishId)).thenReturn(List.of(di));
        Mockito.when(ingredientService.findAllById(List.of(200L))).thenReturn(List.of(ingr));

        mockMvc.perform(get("/dishes/2")
                        .param("minCalories", "80"))
                .andExpect(status().isOk())
                .andExpect(view().name("dishDetails"))
                .andExpect(model().attribute("dish", dishDTO))
                .andExpect(model().attribute("minCalories", 80.0))
                .andExpect(model().attribute("maxCalories", (Object) null))
                .andExpect(model().attributeExists("ingredientCalories"));
    }

    @Test
    void testShowDishById_withOnlyMaxCalories_shouldHighlightBelowMax() throws Exception {
        Long dishId = 3L;

        DishDTO dishDTO = new DishDTO();
        dishDTO.setId(dishId);

        DishIngredientDTO di = new DishIngredientDTO();
        di.setIngredientId(300L);
        di.setWeight(50.0);

        IngredientDTO ingr = new IngredientDTO(300L, "Test", 50.0, null);

        Mockito.when(dishService.getDishById(dishId)).thenReturn(dishDTO);
        Mockito.when(dishIngredientService.findAllByDishId(dishId)).thenReturn(List.of(di));
        Mockito.when(ingredientService.findAllById(List.of(300L))).thenReturn(List.of(ingr));

        mockMvc.perform(get("/dishes/3")
                        .param("maxCalories", "30"))
                .andExpect(status().isOk())
                .andExpect(view().name("dishDetails"))
                .andExpect(model().attribute("dish", dishDTO))
                .andExpect(model().attribute("minCalories", (Object) null))
                .andExpect(model().attribute("maxCalories", 30.0))
                .andExpect(model().attributeExists("ingredientCalories"));
    }

    @Test
    void testShowDishById_caloriesPer100gNull_shouldSkipCalculation() throws Exception {
        Long dishId = 10L;

        DishDTO dishDTO = new DishDTO();
        dishDTO.setId(dishId);

        DishIngredientDTO di = new DishIngredientDTO();
        di.setIngredientId(999L);
        di.setWeight(100.0);

        Mockito.when(dishService.getDishById(dishId)).thenReturn(dishDTO);
        Mockito.when(dishIngredientService.findAllByDishId(dishId)).thenReturn(List.of(di));
        Mockito.when(ingredientService.findAllById(List.of(999L))).thenReturn(List.of());

        mockMvc.perform(get("/dishes/10"))
                .andExpect(status().isOk())
                .andExpect(view().name("dishDetails"))
                .andExpect(model().attribute("dish", dishDTO))
                .andExpect(model().attribute("ingredientCalories", Map.of()))
                .andExpect(model().attribute("minCalories", (Object) null))
                .andExpect(model().attribute("maxCalories", (Object) null));
    }

    @Test
    void testShowDishById_caloriesOutsideRange_shouldNotHighlight() throws Exception {
        Long dishId = 11L;

        DishDTO dishDTO = new DishDTO();
        dishDTO.setId(dishId);

        DishIngredientDTO di = new DishIngredientDTO();
        di.setIngredientId(111L);
        di.setWeight(50.0);

        IngredientDTO ingr = new IngredientDTO(111L, "OutsideRange", 50.0, null);

        Mockito.when(dishService.getDishById(dishId)).thenReturn(dishDTO);
        Mockito.when(dishIngredientService.findAllByDishId(dishId)).thenReturn(List.of(di));
        Mockito.when(ingredientService.findAllById(List.of(111L))).thenReturn(List.of(ingr));

        mockMvc.perform(get("/dishes/11")
                        .param("minCalories", "30")
                        .param("maxCalories", "40"))
                .andExpect(status().isOk())
                .andExpect(view().name("dishDetails"))
                .andExpect(model().attribute("dish", dishDTO))
                .andExpect(model().attribute("minCalories", 30.0))
                .andExpect(model().attribute("maxCalories", 40.0))
                .andExpect(model().attribute("ingredientCalories", Map.of(111L, 25.0)));
    }

    @Test
    void testShowDishById_NotFound() throws Exception {
        // Simulate dish not found
        Mockito.when(dishService.getDishById(999L)).thenReturn(null);

        mockMvc.perform(get("/dishes/999"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("error", "Dish not found"));
    }

    @Test
    void testDeleteDishById() throws Exception {
        // Simulate successful delete
        Mockito.doNothing().when(dishService).deleteDish(1L);

        mockMvc.perform(get("/dishes/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dishes/all"))
                .andExpect(flash().attribute("status", "Dish deleted successfully!"));
    }

    @Test
    void testDeleteDishById_Fail() throws Exception {
        // Simulate error while deleting
        Mockito.doThrow(new RuntimeException("Failed to delete")).when(dishService).deleteDish(999L);

        mockMvc.perform(get("/dishes/delete/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dishes/all"))
                .andExpect(flash().attribute("status", "Failed to delete dish. Please try again."));
    }

    @Test
    void testEditDishById() throws Exception {
        // Simulate dish found
        DishDTO dishDTO = new DishDTO();
        dishDTO.setId(1L);
        dishDTO.setName("Salad");

        Mockito.when(dishService.getDishById(1L)).thenReturn(dishDTO);

        mockMvc.perform(get("/dishes/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("editDish"))
                .andExpect(model().attribute("dish", dishDTO))
                .andExpect(model().attribute("actionTitle", "Edit dish"))
                .andExpect(model().attribute("formAction", "/dishes/update/1"));
    }

    @Test
    void testEditDishById_NotFound() throws Exception {
        // Simulate dish not found
        Mockito.when(dishService.getDishById(999L)).thenReturn(null);

        mockMvc.perform(get("/dishes/edit/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dishes/all"))
                .andExpect(flash().attribute("status", "Dish not found!"));
    }

    @Test
    void testUpdateDish_CallsServiceAndRedirects() throws Exception {
        mockMvc.perform(post("/dishes/update/1")
                        .param("name", "Updated Dish")
                        .param("calories", "250")
                        .param("category", "MAIN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dishes/all"))
                .andExpect(flash().attribute("status", "Dish updated successfully!"));

        Mockito.verify(dishService).updateDish(eq(1L), any(DishDTO.class), isNull());
    }

    @Test
    void testSaveDish() throws Exception {
        DishDTO dishDTO = new DishDTO();
        dishDTO.setName("New Dish");

        Mockito.when(dishService.createDish(any(DishDTO.class))).thenReturn(dishDTO);

        mockMvc.perform(post("/dishes/save")
                        .param("name", "New Dish"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dishes/all"))
                .andExpect(flash().attribute("status", "Dish saved successfully!"));
    }

    @Test
    void testAddDish() throws Exception {
        mockMvc.perform(get("/dishes/create-dish"))
                .andExpect(status().isOk())
                .andExpect(view().name("editDish"));
    }

    @Test
    void testAddDish_WhenIngredientServiceFails_ShouldRedirectWithError() throws Exception {
        Mockito.when(ingredientService.getAllIngredients())
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/dishes/create-dish"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dishes/create-dish"))
                .andExpect(flash().attribute("status", "Failed to add dish. Please try again."));
    }
}