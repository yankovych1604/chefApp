package com.maksy.chefapp.mapper;

import com.maksy.chefapp.dto.IngredientDTO;
import com.maksy.chefapp.model.Ingredient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IngredientMapper {
    // Method to map Ingredient to IngredientDTO
    IngredientDTO ingredientToIngredientDTO(Ingredient ingredient);

    // Method to map IngredientDTO to Ingredient
    Ingredient ingredientDTOToIngredient(IngredientDTO ingredientDTO);
}