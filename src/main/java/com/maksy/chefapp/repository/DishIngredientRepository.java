package com.maksy.chefapp.repository;

import com.maksy.chefapp.model.DishIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishIngredientRepository extends JpaRepository<DishIngredient, Long> {
    // Повертає всі зв’язки між стравою та її інгредієнтами за ID страви
    List<DishIngredient> findAllByDishId(Long dishId);

    // Повертає всі записи з таблиці dish_ingredient
    List<DishIngredient> findAll();
}