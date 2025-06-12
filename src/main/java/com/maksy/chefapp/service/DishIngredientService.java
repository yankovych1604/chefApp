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
    private DishIngredientRepository dishIngredientRepository;     // Репозиторій для доступу до таблиці dish_ingredient

    @Autowired
    private DishIngredientMapper dishIngredientMapper;  // Мапер для перетворення між DishIngredient ↔ DishIngredientDTO

    @Autowired
    private IngredientRepository ingredientRepository;       // Репозиторій інгредієнтів, щоб отримати назву інгредієнта

    // Повертає список інгредієнтів певної страви у вигляді DTO та до кожного DTO додається назва інгредієнта (не тільки його ID)
    public List<DishIngredientDTO> findAllByDishId(Long dishId){
        // Отримуємо всі зв'язки інгредієнтів зі стравою
        List<DishIngredient> dishIngredients = dishIngredientRepository.findAllByDishId(dishId);
        List<DishIngredientDTO> dishIngredientDTOS = new ArrayList<>();

        for(DishIngredient dishIngredient : dishIngredients){
            // Конвертація сутності у DTO
           DishIngredientDTO dishIngredientDTO = dishIngredientMapper.toDishIngredientDTO(dishIngredient);

            // Знаходимо інгредієнт по ID і встановлюємо назву в DTO
            ingredientRepository.findById(dishIngredient.getIngredientId()).ifPresent(ingredient ->
                    dishIngredientDTO.setIngredientName(ingredient.getName()));

            // Додаємо DTO до списку
            dishIngredientDTOS.add(dishIngredientDTO);
        }

        return dishIngredientDTOS;
    }

    // Повертає всі зв’язки між стравами та інгредієнтами у вигляді DTO
    public  List<DishIngredientDTO> findAll(){
        // Отримуємо всі зв'язки та одразу перетворюємо у DTO
        return dishIngredientRepository.findAll().stream()
                .map(dishIngredientMapper::toDishIngredientDTO)
                .toList();
    }
}