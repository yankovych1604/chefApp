package com.maksy.chefapp.service;

import com.maksy.chefapp.dto.DishDTO;
import com.maksy.chefapp.dto.DishIngredientDTO;
import com.maksy.chefapp.dto.IngredientDTO;
import com.maksy.chefapp.exception.EntityNotFoundException;
import com.maksy.chefapp.exception.StatusCodes;
import com.maksy.chefapp.mapper.DishMapper;
import com.maksy.chefapp.model.Dish;
import com.maksy.chefapp.model.DishIngredient;
import com.maksy.chefapp.model.enums.DishType;
import com.maksy.chefapp.repository.DishRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DishIngredientService dishIngredientService;
    @Autowired
    private IngredientService ingredientService;


    public List<DishDTO> getAllDishes() {
        List<Dish> dishes = dishRepository.findAll();
        return dishMapper.dishesToDishDTOs(dishes);
    }

    public DishDTO getDishById(Long id) {
        Dish dish = dishRepository.findById(id).orElse(null);
        if (dish == null) {
            throw new EntityNotFoundException(StatusCodes.ENTITY_NOT_FOUND.name(), "Dish does not exist");
        }
        return dishMapper.dishToDishDTO(dish);
    }

    public Page<DishDTO> getAllDishesFiltered(DishType dishType, Pageable pageable) {
        Page<Dish> dishesPage = dishRepository.findFilteredDishes(dishType, pageable);

        List<DishDTO> dishesDTO = dishesPage.getContent().stream()
                .map(dishMapper::dishToDishDTO)
                .toList();

        return new PageImpl<>(dishesDTO, pageable, dishesPage.getTotalElements());
    }

    public DishDTO createDish(DishDTO dishDTO) {
        Dish dish = dishMapper.dishDTOToDish(dishDTO);

        List<DishIngredient> dishIngredients = dish.getDishIngredients();
        dish.setDishIngredients(null);

        Dish savedDish = dishRepository.save(dish);

        if (dishIngredients != null) {
            List<DishIngredient> newDishIngredients = new ArrayList<>();
            for (DishIngredient di : dishIngredients) {
                if (di.getId() == null) {
                    di.setDishId(savedDish.getId());
                }
                newDishIngredients.add(di);
            }

            savedDish.setDishIngredients(newDishIngredients);

            dishRepository.save(savedDish);

            updateCaloriesWeight(savedDish);
        }

        return dishMapper.dishToDishDTO(savedDish);
    }


    @Transactional
    public void deleteDish(Long dishId) {
        Dish dish = entityManager.find(Dish.class, dishId);

        if (dish != null) {
            for (DishIngredient dishIngredient : dish.getDishIngredients()) {
                entityManager.remove(dishIngredient);
            }
            entityManager.remove(dish);
        } else {
            throw new EntityNotFoundException(StatusCodes.ENTITY_NOT_FOUND.name(), "Dish with id " + dishId + " and its ingredients were not found.");
        }
    }

    @Transactional
    public DishDTO updateDish(Long id, DishDTO dishDTO, List<Long> deleteIngredientIds) {
        Dish dish = dishMapper.dishDTOToDish(dishDTO);
        dish.setId(id);

        if (deleteIngredientIds != null) {
            dish.removeIngredients(deleteIngredientIds);
        }

        List<DishIngredient> dishIngredients = dish.getDishIngredients();
        if (dishIngredients != null) {
            List<DishIngredient> newDishIngredients = new ArrayList<>();
            for (DishIngredient di : dish.getDishIngredients()) {
                if(di.getId() == null){
                    newDishIngredients.add(new DishIngredient(dish.getId(), di.getIngredientId(), di.getWeight()));
                }else {
                    newDishIngredients.add(di);
                }
            }
            dish.setDishIngredients(newDishIngredients);
        }

        dishRepository.save(dish);
        updateCaloriesWeight(dish);

        return dishMapper.dishToDishDTO(dish);
    }

    public void updateCaloriesWeight(Dish dish) {
        List<DishIngredientDTO> dishIngredients = dishIngredientService.findAllByDishId(dish.getId());
        List<IngredientDTO> ingredientDTOS = ingredientService.findAllById(dishIngredients.stream()
                .map(DishIngredientDTO::getIngredientId)
                .collect(Collectors.toList())
        );

        Map<Long, IngredientDTO> ingredientMap = ingredientDTOS.stream()
                .collect(Collectors.toMap(IngredientDTO::getId, Function.identity()));

        double dishCalories = 0;
        double dishWeight = 0;

        for (DishIngredientDTO dishIngredient : dishIngredients) {
            IngredientDTO ingredientDTO = ingredientMap.get(dishIngredient.getIngredientId());
            if(ingredientDTO != null){
                double weight = dishIngredient.getWeight();
                double caloriesPer100g = ingredientDTO.getCaloriesPer100g();

                dishWeight += weight;
                dishCalories += (caloriesPer100g / 100) * weight;
            }
        }

        dish.setTotalWeight(dishWeight);
        dish.setTotalCalories(dishCalories);
        dishRepository.save(dish);

    }

    public List<DishDTO> get3Dishes() {
        List<Dish> dishes = dishRepository.findTop3ByOrderByIdDesc();
        return dishes.stream()
                .map(dish -> dishMapper.dishToDishDTO(dish))
                .collect(Collectors.toList());
    }
}