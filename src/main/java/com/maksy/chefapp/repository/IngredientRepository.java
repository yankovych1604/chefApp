package com.maksy.chefapp.repository;

import com.maksy.chefapp.model.Ingredient;
import com.maksy.chefapp.model.enums.IngredientCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    // Пошук інгредієнта за його ID
    Optional<Ingredient> findById(Long id);

    // Повертає сторінку інгредієнтів, відфільтрованих за категорією, якщо категорія не вказана (null), повертаються всі інгредієнти
    @Query("SELECT i FROM Ingredient i WHERE (:ingredientCategory IS NULL OR i.category = :ingredientCategory) ")
    Page<Ingredient> findAllByCategory(@Param("ingredientCategory") IngredientCategory ingredientCategory, Pageable pageable);

    // Повертає сторінку інгредієнтів з фільтрацією за категорією/мінімальною та максимальною калорійністю
    // Якщо будь-який з параметрів є null, відповідна умова ігнорується
    @Query("SELECT i FROM Ingredient i WHERE (:ingredientCategory IS NULL OR i.category = :ingredientCategory) AND" +
            "(:caloriesFrom IS NULL OR i.caloriesPer100g >= :caloriesFrom) AND" +
            "(:caloriesTo IS NULL OR i.caloriesPer100g <= :caloriesTo)")
    Page<Ingredient> findAllFiltered(@Param("ingredientCategory") IngredientCategory ingredientCategory, Double caloriesFrom, Double caloriesTo, Pageable pageable);
}