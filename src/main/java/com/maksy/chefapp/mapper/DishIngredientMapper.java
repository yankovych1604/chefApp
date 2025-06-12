package com.maksy.chefapp.mapper;

import com.maksy.chefapp.dto.DishIngredientDTO;
import com.maksy.chefapp.model.DishIngredient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DishIngredientMapper {
    // Перетворює сутність DishIngredient у DTO, ігноруючи поле ingredientName (воно не зберігається в БД)
    @Mapping(target = "ingredientName", ignore = true)
    DishIngredientDTO toDishIngredientDTO(DishIngredient dishIngredient);

    // Перетворює DTO назад у сутність DishIngredient (поле ingredientName буде автоматично проігнороване)
    DishIngredient toDishIngredient(DishIngredientDTO dishIngredientDTO);
}