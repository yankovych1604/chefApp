package com.maksy.chefapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "DISHINGREDIENT")
public class DishIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dish_id", nullable = false)
    private Long dishId;

    @Column(name = "ingredient_id", nullable = false)
    private Long ingredientId;

    private double weight;

    public DishIngredient(Long dishId, Long ingredientId, double weight) {
        this.weight = weight;
        this.ingredientId = ingredientId;
        this.dishId = dishId;
    }

    public DishIngredient() {

    }

    @Override
    public String toString() {
        return "DishIngredient{" +
                "id=" + id +
                ", dishId=" + dishId +
                ", ingredientId=" + ingredientId +
                ", weight=" + weight +
                '}';
    }
}