package com.maksy.chefapp.mapper;

import com.maksy.chefapp.dto.DishDTO;
import com.maksy.chefapp.model.Dish;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {DishIngredientMapper.class})
public interface DishMapper {

    DishDTO dishToDishDTO(Dish dish);

    Dish dishDTOToDish(DishDTO dishDTO);

    List<DishDTO> dishesToDishDTOs(List<Dish> dishes);

    List<Dish> dishDTOsToDishes(List<DishDTO> dishDTOs);
}