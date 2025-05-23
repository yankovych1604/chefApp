package com.maksy.chefapp.mapper;

import com.maksy.chefapp.dto.DishIngredientDTO;
import com.maksy.chefapp.model.DishIngredient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DishIngredientMapper {
    @Mapping(target = "ingredientName", ignore = true)
    DishIngredientDTO toDishIngredientDTO(DishIngredient dishIngredient);

    DishIngredient toDishIngredient(DishIngredientDTO dishIngredientDTO);
}