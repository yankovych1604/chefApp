package com.maksy.chefapp.service;

import com.maksy.chefapp.dto.IngredientDTO;
import com.maksy.chefapp.exception.EntityNotFoundException;
import com.maksy.chefapp.exception.StatusCodes;
import com.maksy.chefapp.mapper.IngredientMapper;
import com.maksy.chefapp.model.Ingredient;
import com.maksy.chefapp.model.enums.IngredientCategory;
import com.maksy.chefapp.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class IngredientService {

    @Autowired
    private IngredientMapper ingredientMapper;

    @Autowired
    private IngredientRepository ingredientRepository;

    public List<IngredientDTO> getAllIngredients() {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        return ingredients.stream()
                .map(ingredientMapper::ingredientToIngredientDTO)
                .collect(toList());
    }

    public IngredientDTO findById(Long ingredientId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId).orElseThrow(()->new EntityNotFoundException(StatusCodes.ENTITY_NOT_FOUND.name(), "Ingredient not found"));
        return ingredientMapper.ingredientToIngredientDTO(ingredient);
    }

    public List<IngredientDTO> findAllById(List<Long>  ingredientIds) {
        List<Ingredient> ingredients = ingredientRepository.findAllById(ingredientIds);
        return ingredients.stream()
                .map(ingredientMapper::ingredientToIngredientDTO)
                .toList();
    }

    public Page<IngredientDTO> getAllingredientsByCatagory(IngredientCategory ingredientCategory, Pageable pageable) {
        Page<Ingredient> ingredientsPage = ingredientRepository.findAllByCategory(ingredientCategory, pageable);
        List<IngredientDTO> ingredientsDTO = ingredientsPage.getContent().stream()
                .map(ingredientMapper::ingredientToIngredientDTO)
                .toList();

        return new PageImpl<>(ingredientsDTO, pageable, ingredientsPage.getTotalElements());
    }

    public IngredientDTO createIngredient(IngredientDTO ingredientDTO) {
        Ingredient ingredient = ingredientMapper.ingredientDTOToIngredient(ingredientDTO);

        ingredientRepository.save(ingredient);

        return ingredientMapper.ingredientToIngredientDTO(ingredient);
    }

    public IngredientDTO updateIngredient(long id, IngredientDTO ingredientDTO) {
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(()->
                new EntityNotFoundException(StatusCodes.ENTITY_NOT_FOUND.name(), "Ingredient not found"));

        ingredient.setName(ingredientDTO.getName());
        ingredient.setCaloriesPer100g(ingredientDTO.getCaloriesPer100g());
        ingredient.setCategory(ingredientDTO.getCategory());

        ingredientRepository.save(ingredient);
        return ingredientMapper.ingredientToIngredientDTO(ingredient);
    }

    public void deleteIngredient(Long id) {
        ingredientRepository.deleteById(id);
    }

    public Page<IngredientDTO> getAllingredientsFiltered(IngredientCategory ingredientCategory, Double caloriesFrom, Double caloriesTo, String sortOrder, Pageable pageable) {
        Sort sort = Sort.unsorted();
        if ("highToLow".equals(sortOrder)) {
            sort = Sort.by(Sort.Direction.DESC, "caloriesPer100g");
        } else if ("lowToHigh".equals(sortOrder)) {
            sort = Sort.by(Sort.Direction.ASC, "caloriesPer100g");
        }

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Ingredient> ingredientPage = ingredientRepository.findAllFiltered(
                ingredientCategory, caloriesFrom, caloriesTo, sortedPageable
        );

        List<IngredientDTO> ingredientDTOS = ingredientPage.getContent().stream()
                .map(ingredientMapper::ingredientToIngredientDTO)
                .toList();

        return new PageImpl<>(ingredientDTOS, pageable, ingredientPage.getTotalElements());
    }
}