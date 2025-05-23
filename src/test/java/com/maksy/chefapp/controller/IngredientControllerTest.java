package com.maksy.chefapp.controller;

import com.maksy.chefapp.dto.IngredientDTO;
import com.maksy.chefapp.model.enums.IngredientCategory;
import com.maksy.chefapp.service.IngredientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IngredientController.class)
public class IngredientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IngredientService ingredientService;

    @Test
    void testGetAllIngredients() throws Exception {
        Page<IngredientDTO> mockPage = new PageImpl<>(new ArrayList<>());
        when(ingredientService.getAllingredientsFiltered(isNull(), isNull(), isNull(), isNull(), eq(PageRequest.of(0, 6))
        )).thenReturn(mockPage);

        mockMvc.perform(get("/ingredients"))
                .andExpect(status().isOk())
                .andExpect(view().name("ingredients"))
                .andExpect(model().attributeExists("ingredients"))
                .andExpect(model().attribute("selectedCategory", (Object) null))
                .andExpect(model().attribute("selectedSortOrder", (Object) null))
                .andExpect(model().attribute("caloriesFrom", (Object) null))
                .andExpect(model().attribute("caloriesTo", (Object) null));
    }

    @Test
    void testGetAllIngredients_WithCategory() throws Exception {
        Page<IngredientDTO> mockPage = new PageImpl<>(new ArrayList<>());
        IngredientCategory category = IngredientCategory.MEAT;

        when(ingredientService.getAllingredientsFiltered(eq(category), isNull(), isNull(), isNull(), eq(PageRequest.of(0, 6))))
                .thenReturn(mockPage);

        mockMvc.perform(get("/ingredients").param("ingredientCategory", category.name()))
                .andExpect(status().isOk())
                .andExpect(view().name("ingredients"))
                .andExpect(model().attributeExists("ingredients"))
                .andExpect(model().attribute("selectedCategory", category.name()));
    }

    @Test
    void testSelectedCategoryIsMappedToModel_WhenProvided() throws Exception {
        IngredientCategory category = IngredientCategory.SPICE;

        Page<IngredientDTO> emptyPage = new PageImpl<>(List.of());
        when(ingredientService.getAllingredientsFiltered(eq(category), isNull(), isNull(), isNull(), any()))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/ingredients")
                        .param("ingredientCategory", category.name()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("selectedCategory", category.name()));
    }

    @Test
    void testCreateIngredientForm() throws Exception {
        mockMvc.perform(get("/ingredients/create-ingredient"))
                .andExpect(status().isOk())
                .andExpect(view().name("ingredientForm"))
                .andExpect(model().attributeExists("ingredient"))
                .andExpect(model().attribute("formAction", "/ingredients/save"))
                .andExpect(model().attribute("actionTitle", "Create Ingredient"))
                .andExpect(model().attribute("submitButtonLabel", "Create"));
    }

    @Test
    void testSaveIngredientSuccess() throws Exception {
        mockMvc.perform(post("/ingredients/save")
                        .param("name", "Carrot")
                        .param("caloriesPer100g", "41")
                        .param("category", IngredientCategory.VEGETABLE.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ingredients"));
    }

    @Test
    void testSaveIngredientFailure() throws Exception {
        doThrow(new RuntimeException("DB error"))
                .when(ingredientService)
                .createIngredient(any());

        mockMvc.perform(post("/ingredients/save")
                        .param("name", "Broken")
                        .param("caloriesPer100g", "100")
                        .param("category", IngredientCategory.OTHER.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ingredients"))
                .andExpect(flash().attribute("status", "Failed to save ingredient."));
    }

    @Test
    void testGetIngredientDetails_Found() throws Exception {
        IngredientDTO dto = new IngredientDTO();
        when(ingredientService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/ingredients/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("ingredientDetails"))
                .andExpect(model().attributeExists("ingredient"));
    }

    @Test
    void testGetIngredientDetails_NotFound() throws Exception {
        when(ingredientService.findById(1L)).thenReturn(null);

        mockMvc.perform(get("/ingredients/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ingredients"))
                .andExpect(flash().attribute("status", "Ingredient not found!"));
    }

    @Test
    void testEditIngredient_Found() throws Exception {
        IngredientDTO dto = new IngredientDTO();
        when(ingredientService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/ingredients/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("ingredientForm"))
                .andExpect(model().attributeExists("ingredient"))
                .andExpect(model().attribute("formAction", "/ingredients/update/1"))
                .andExpect(model().attribute("actionTitle", "Edit Ingredient"))
                .andExpect(model().attribute("submitButtonLabel", "Update"));
    }

    @Test
    void testEditIngredient_NotFound() throws Exception {
        when(ingredientService.findById(1L)).thenReturn(null);

        mockMvc.perform(get("/ingredients/edit/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ingredients"))
                .andExpect(flash().attribute("status", "Ingredient not found!"));
    }

    @Test
    void testUpdateIngredient() throws Exception {
        mockMvc.perform(post("/ingredients/update/1")
                        .param("name", "Tomato")
                        .param("caloriesPer100g", "18")
                        .param("category", IngredientCategory.VEGETABLE.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ingredients"));
    }

    @Test
    void testDeleteIngredient_Success() throws Exception {
        mockMvc.perform(get("/ingredients/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ingredients"))
                .andExpect(flash().attribute("status", "Ingredient deleted successfully!"));
    }

    @Test
    void testDeleteIngredient_Failure() throws Exception {
        doThrow(new RuntimeException("In use")).when(ingredientService).deleteIngredient(1L);

        mockMvc.perform(get("/ingredients/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ingredients"))
                .andExpect(flash().attribute("status", "Failed to delete ingredient. It's used in dishes."));
    }
}