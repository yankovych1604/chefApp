package com.maksy.chefapp.service;

import com.maksy.chefapp.dto.DishIngredientDTO;
import com.maksy.chefapp.mapper.DishIngredientMapper;
import com.maksy.chefapp.model.DishIngredient;
import com.maksy.chefapp.repository.DishIngredientRepository;
import com.maksy.chefapp.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishIngredientService {
    @Autowired
    private DishIngredientRepository dishIngredientRepository;

    @Autowired
    private DishIngredientMapper dishIngredientMapper;
    @Autowired
    private IngredientRepository ingredientRepository;

    public List<DishIngredientDTO> findAllByDishId(Long dishId){
        List<DishIngredient> dishIngredients = dishIngredientRepository.findAllByDishId(dishId);
        List<DishIngredientDTO> dishIngredientDTOS = new ArrayList<>();

        for(DishIngredient dishIngredient : dishIngredients){
           DishIngredientDTO dishIngredientDTO = dishIngredientMapper.toDishIngredientDTO(dishIngredient);

            ingredientRepository.findById(dishIngredient.getIngredientId()).ifPresent(ingredient ->
                    dishIngredientDTO.setIngredientName(ingredient.getName()));
            dishIngredientDTOS.add(dishIngredientDTO);
        }

        return dishIngredientDTOS;
    }

    public  List<DishIngredientDTO> findAll(){
        return dishIngredientRepository.findAll().stream()
                .map(dishIngredientMapper::toDishIngredientDTO)
                .toList();
    }
}