package com.maksy.chefapp.mapper;

import com.maksy.chefapp.dto.DishDTO;
import com.maksy.chefapp.model.Dish;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {DishIngredientMapper.class})
public interface DishMapper {
    // Перетворює сутність Dish у DTO
    DishDTO dishToDishDTO(Dish dish);

    // Перетворює DTO у сутність Dish
    Dish dishDTOToDish(DishDTO dishDTO);

    // Перетворює список сутностей Dish у список DTO
    List<DishDTO> dishesToDishDTOs(List<Dish> dishes);

    // Перетворює список DTO у список сутностей Dish
    List<Dish> dishDTOsToDishes(List<DishDTO> dishDTOs);
}