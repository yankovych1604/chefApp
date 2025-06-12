package com.maksy.chefapp.mapper;

import com.maksy.chefapp.dto.IngredientDTO;
import com.maksy.chefapp.model.Ingredient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IngredientMapper {
    // Перетворює сутність Ingredient у DTO
    IngredientDTO ingredientToIngredientDTO(Ingredient ingredient);

    // Перетворює DTO у сутність Ingredient
    Ingredient ingredientDTOToIngredient(IngredientDTO ingredientDTO);
}