package com.maksy.chefapp.repository;

import com.maksy.chefapp.model.Dish;
import com.maksy.chefapp.model.enums.DishType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DishRepository extends JpaRepository<Dish, Integer> {
    // Повертає всі страви з таблиці
    List<Dish> findAll();

    // Пошук страви за її ID
    Optional<Dish> findById(Long id);

    // Запит для пошуку страв з фільтрацією за типом (DishType), якщо тип не вказано (null), повертаються всі страви
    @Query("SELECT d FROM Dish d WHERE (:dishtype IS NULL OR d.type = :dishtype) ")
    Page<Dish> findFilteredDishes(@Param("dishtype") DishType dishType, Pageable pageable);

    // Повертає 3 останні страви, відсортовані за спаданням ID
    List<Dish> findTop3ByOrderByIdDesc();
}