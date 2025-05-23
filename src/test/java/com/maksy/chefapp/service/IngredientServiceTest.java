package com.maksy.chefapp.service;

import com.maksy.chefapp.dto.IngredientDTO;
import com.maksy.chefapp.exception.EntityNotFoundException;
import com.maksy.chefapp.mapper.IngredientMapper;
import com.maksy.chefapp.model.Ingredient;
import com.maksy.chefapp.model.enums.IngredientCategory;
import com.maksy.chefapp.repository.IngredientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IngredientServiceTest {

    @InjectMocks
    private IngredientService ingredientService;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private IngredientMapper ingredientMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllIngredients() {
        List<Ingredient> ingredients = List.of(new Ingredient(), new Ingredient());
        when(ingredientRepository.findAll()).thenReturn(ingredients);
        when(ingredientMapper.ingredientToIngredientDTO(any())).thenReturn(new IngredientDTO());

        List<IngredientDTO> result = ingredientService.getAllIngredients();

        assertEquals(2, result.size());
        verify(ingredientRepository, times(1)).findAll();
    }

    @Test
    void testFindById_Found() {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(1L);
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredient));
        when(ingredientMapper.ingredientToIngredientDTO(any())).thenReturn(new IngredientDTO());

        IngredientDTO result = ingredientService.findById(1L);

        assertNotNull(result);
        verify(ingredientRepository).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> ingredientService.findById(1L));
    }

    @Test
    void testFindAllById() {
        List<Ingredient> ingredients = List.of(new Ingredient(), new Ingredient());
        when(ingredientRepository.findAllById(anyList())).thenReturn(ingredients);
        when(ingredientMapper.ingredientToIngredientDTO(any())).thenReturn(new IngredientDTO());

        List<IngredientDTO> result = ingredientService.findAllById(List.of(1L, 2L));

        assertEquals(2, result.size());
        verify(ingredientRepository).findAllById(anyList());
    }

    @Test
    void testGetAllIngredientsByCategory() {
        IngredientCategory category = IngredientCategory.VEGETABLE;
        Ingredient ingredient = new Ingredient();
        Page<Ingredient> page = new PageImpl<>(List.of(ingredient));

        when(ingredientRepository.findAllByCategory(eq(category), any(Pageable.class)))
                .thenReturn(page);
        when(ingredientMapper.ingredientToIngredientDTO(any()))
                .thenReturn(new IngredientDTO());

        Pageable pageable = PageRequest.of(0, 10);
        Page<IngredientDTO> result = ingredientService.getAllingredientsByCatagory(category, pageable);

        assertEquals(1, result.getContent().size());
        verify(ingredientRepository).findAllByCategory(eq(category), eq(pageable));
    }

    @Test
    void testGetAllIngredientsFiltered_HighToLow() {
        Ingredient ingredient = new Ingredient("Salt", 10.0, IngredientCategory.SPICE);
        ingredient.setId(1L);

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "caloriesPer100g"));
        Page<Ingredient> ingredientPage = new PageImpl<>(List.of(ingredient), pageable, 1);

        when(ingredientRepository.findAllFiltered(null, null, null, pageable)).thenReturn(ingredientPage);
        when(ingredientMapper.ingredientToIngredientDTO(any())).thenReturn(new IngredientDTO());

        Page<IngredientDTO> result = ingredientService.getAllingredientsFiltered(null, null, null, "highToLow", PageRequest.of(0, 5));

        assertEquals(1, result.getTotalElements());
        verify(ingredientRepository).findAllFiltered(null, null, null, pageable);
    }

    @Test
    void testGetAllIngredientsFiltered_LowToHigh() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "caloriesPer100g"));
        when(ingredientRepository.findAllFiltered(null, null, null, pageable))
                .thenReturn(new PageImpl<>(List.of()));
        when(ingredientMapper.ingredientToIngredientDTO(any())).thenReturn(new IngredientDTO());

        ingredientService.getAllingredientsFiltered(null, null, null, "lowToHigh", PageRequest.of(0, 5));

        verify(ingredientRepository).findAllFiltered(null, null, null, pageable);
    }

    @Test
    void testGetAllIngredientsFiltered_NoSort() {
        Pageable pageable = PageRequest.of(0, 5, Sort.unsorted());
        when(ingredientRepository.findAllFiltered(null, null, null, pageable))
                .thenReturn(new PageImpl<>(List.of()));
        when(ingredientMapper.ingredientToIngredientDTO(any())).thenReturn(new IngredientDTO());

        ingredientService.getAllingredientsFiltered(null, null, null, null, PageRequest.of(0, 5));

        verify(ingredientRepository).findAllFiltered(null, null, null, pageable);
    }

    @Test
    void testGetAllIngredientsFiltered_HighToLow_DirectCall() {
        Ingredient ingredient = new Ingredient("Salt", 10.0, IngredientCategory.SPICE);
        ingredient.setId(1L);

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "caloriesPer100g"));
        Page<Ingredient> ingredientPage = new PageImpl<>(List.of(ingredient), pageable, 1);

        when(ingredientRepository.findAllFiltered(null, null, null, pageable)).thenReturn(ingredientPage);
        when(ingredientMapper.ingredientToIngredientDTO(any())).thenReturn(new IngredientDTO());

        Page<IngredientDTO> result = ingredientService.getAllingredientsFiltered(null, null, null, "highToLow", pageable);

        assertEquals(1, result.getTotalElements());
        verify(ingredientRepository).findAllFiltered(null, null, null, pageable);
    }

    @Test
    void testCreateIngredient() {
        IngredientDTO ingredientDTO = new IngredientDTO();
        Ingredient ingredient = new Ingredient();

        when(ingredientMapper.ingredientDTOToIngredient(ingredientDTO)).thenReturn(ingredient);
        when(ingredientMapper.ingredientToIngredientDTO(ingredient)).thenReturn(ingredientDTO);

        IngredientDTO result = ingredientService.createIngredient(ingredientDTO);

        verify(ingredientRepository).save(ingredient);
        assertEquals(ingredientDTO, result);
    }

    @Test
    void testUpdateIngredient_Success() {
        long id = 1L;
        Ingredient ingredient = new Ingredient();
        ingredient.setId(id);
        ingredient.setName("Old Name");
        ingredient.setCaloriesPer100g(100.0);
        ingredient.setCategory(IngredientCategory.VEGETABLE);

        IngredientDTO updatedDTO = new IngredientDTO();
        updatedDTO.setName("New Name");
        updatedDTO.setCaloriesPer100g(50.0);
        updatedDTO.setCategory(IngredientCategory.MEAT);

        when(ingredientRepository.findById(id)).thenReturn(Optional.of(ingredient));
        when(ingredientMapper.ingredientToIngredientDTO(any())).thenReturn(updatedDTO);

        IngredientDTO result = ingredientService.updateIngredient(id, updatedDTO);

        assertEquals("New Name", ingredient.getName());
        assertEquals(50, ingredient.getCaloriesPer100g());
        assertEquals(IngredientCategory.MEAT, ingredient.getCategory());

        verify(ingredientRepository).save(ingredient);
        assertEquals(updatedDTO, result);
    }

    @Test
    void testUpdateIngredient_NotFound() {
        long id = 1L;
        IngredientDTO dto = new IngredientDTO();

        when(ingredientRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> ingredientService.updateIngredient(id, dto));
        verify(ingredientRepository, never()).save(any());
    }

    @Test
    void testDeleteIngredient() {
        long id = 1L;

        ingredientService.deleteIngredient(id);

        verify(ingredientRepository).deleteById(id);
    }
}
