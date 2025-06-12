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
    private DishMapper dishMapper;                                           // Мапер для конвертації між Dish і DishDTO

    @Autowired
    private DishRepository dishRepository;                                    // Репозиторій для роботи з таблицею страв

    @Autowired
    private EntityManager entityManager;                                            // Для ручного управління сутностями

    @Autowired
    private DishIngredientService dishIngredientService;                    // Сервіс для зв’язків "страва — інгредієнт"

    @Autowired
    private IngredientService ingredientService;                                              // Сервіс для інгредієнтів

    // Повертає всі страви без фільтрації
    public List<DishDTO> getAllDishes() {
        List<Dish> dishes = dishRepository.findAll();
        return dishMapper.dishesToDishDTOs(dishes);
    }

    // Повертає страву за ID. Якщо не знайдена — кидає виняток
    public DishDTO getDishById(Long id) {
        Dish dish = dishRepository.findById(id).orElse(null);
        if (dish == null) {
            throw new EntityNotFoundException(StatusCodes.ENTITY_NOT_FOUND.name(), "Dish does not exist");
        }
        return dishMapper.dishToDishDTO(dish);
    }

    // Повертає список страв з фільтрацією за типом страви та з пагінацією
    public Page<DishDTO> getAllDishesFiltered(DishType dishType, Pageable pageable) {
        Page<Dish> dishesPage = dishRepository.findFilteredDishes(dishType, pageable);

        List<DishDTO> dishesDTO = dishesPage.getContent().stream()
                .map(dishMapper::dishToDishDTO)
                .toList();

        return new PageImpl<>(dishesDTO, pageable, dishesPage.getTotalElements());
    }

    // Створює нову страву. Якщо є інгредієнти — додає їх і рахує загальні калорії та вагу
    public DishDTO createDish(DishDTO dishDTO) {
        Dish dish = dishMapper.dishDTOToDish(dishDTO);

        // Тимчасово забираємо інгредієнти для збереження страви
        List<DishIngredient> dishIngredients = dish.getDishIngredients();
        dish.setDishIngredients(null);

        // Зберігаємо страву
        Dish savedDish = dishRepository.save(dish);

        // Якщо є інгредієнти — оновлюємо dishId і додаємо їх до страви
        if (dishIngredients != null) {
            List<DishIngredient> newDishIngredients = new ArrayList<>();
            for (DishIngredient di : dishIngredients) {
                if (di.getId() == null) {
                    di.setDishId(savedDish.getId());
                }

                newDishIngredients.add(di);
            }

            savedDish.setDishIngredients(newDishIngredients);

            dishRepository.save(savedDish);                                       // Повторне збереження з інгредієнтами

            updateCaloriesWeight(savedDish);                                                // Підрахунок калорій і ваги
        }

        return dishMapper.dishToDishDTO(savedDish);
    }

    // Видаляє страву та всі її зв’язані інгредієнти
    @Transactional
    public void deleteDish(Long dishId) {
        Dish dish = entityManager.find(Dish.class, dishId);

        if (dish != null) {
            // Видалення зв’язаних інгредієнтів
            for (DishIngredient dishIngredient : dish.getDishIngredients()) {
                entityManager.remove(dishIngredient);
            }
            entityManager.remove(dish);                                                        // Видалення самої страви
        } else {
            throw new EntityNotFoundException(StatusCodes.ENTITY_NOT_FOUND.name(), "Dish with id " + dishId + " and its ingredients were not found.");
        }
    }

    // Оновлює страву та її інгредієнти, включно з видаленням зазначених інгредієнтів
    @Transactional
    public DishDTO updateDish(Long id, DishDTO dishDTO, List<Long> deleteIngredientIds) {
        Dish dish = dishMapper.dishDTOToDish(dishDTO);
        dish.setId(id);

        // Видалення зазначених інгредієнтів
        if (deleteIngredientIds != null) {
            dish.removeIngredients(deleteIngredientIds);
        }

        // Обробка інгредієнтів (нових і вже існуючих)
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

        dishRepository.save(dish);                                                         // Зберігаємо оновлену страву
        updateCaloriesWeight(dish);                                                        // Перерахунок калорій і ваги

        return dishMapper.dishToDishDTO(dish);
    }

    // Оновлює калорійність і вагу страви на основі її інгредієнтів
    public void updateCaloriesWeight(Dish dish) {
        // Отримуємо всі інгредієнти страви
        List<DishIngredientDTO> dishIngredients = dishIngredientService.findAllByDishId(dish.getId());

        // Завантажуємо інформацію про самі інгредієнти
        List<IngredientDTO> ingredientDTOS = ingredientService.findAllById(dishIngredients.stream()
                .map(DishIngredientDTO::getIngredientId)
                .collect(Collectors.toList())
        );

        // Створюємо map для швидкого доступу по ID
        Map<Long, IngredientDTO> ingredientMap = ingredientDTOS.stream()
                .collect(Collectors.toMap(IngredientDTO::getId, Function.identity()));

        double dishCalories = 0;
        double dishWeight = 0;

        // Підрахунок загальної калорійності та ваги
        for (DishIngredientDTO dishIngredient : dishIngredients) {
            IngredientDTO ingredientDTO = ingredientMap.get(dishIngredient.getIngredientId());
            if(ingredientDTO != null){
                double weight = dishIngredient.getWeight();
                double caloriesPer100g = ingredientDTO.getCaloriesPer100g();

                dishWeight += weight;
                dishCalories += (caloriesPer100g / 100) * weight;
            }
        }

        // Оновлюємо та зберігаємо
        dish.setTotalWeight(dishWeight);
        dish.setTotalCalories(dishCalories);
        dishRepository.save(dish);

    }

    // Повертає останні 3 створені страви
    public List<DishDTO> get3Dishes() {
        List<Dish> dishes = dishRepository.findTop3ByOrderByIdDesc();
        return dishes.stream()
                .map(dish -> dishMapper.dishToDishDTO(dish))
                .collect(Collectors.toList());
    }
}