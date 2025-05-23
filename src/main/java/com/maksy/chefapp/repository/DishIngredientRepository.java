package com.maksy.chefapp.repository;

import com.maksy.chefapp.model.DishIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishIngredientRepository extends JpaRepository<DishIngredient, Long> {
    List<DishIngredient> findAllByDishId(Long dishId);

    List<DishIngredient> findAll();
}