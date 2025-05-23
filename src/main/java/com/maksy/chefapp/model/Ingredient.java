package com.maksy.chefapp.model;

import com.maksy.chefapp.model.enums.IngredientCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Ingredient")
@Getter
@Setter
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "calories_per_100g")
    private Double caloriesPer100g;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private IngredientCategory category;

    public Ingredient(String name, double caloriesPer100g, IngredientCategory category) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        if (caloriesPer100g <= 0) {
            throw new IllegalArgumentException("Calories per 100g must be greater than zero.");
        }
        this.name = name;
        this.caloriesPer100g = caloriesPer100g;
        this.category = category;
    }

    public Ingredient() {}

    public double getCalories(double weight) {
        return caloriesPer100g * weight / 100;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", caloriesPer100g=" + caloriesPer100g +
                ", category=" + category +
                '}';
    }
}