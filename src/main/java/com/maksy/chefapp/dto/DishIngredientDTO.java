package com.maksy.chefapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DishIngredientDTO {
    private Long id;
    private Long dishId;
    private Long ingredientId;
    private String ingredientName;
    private double weight;

    @Override
    public String toString() {
        return "DishIngredientDTO{" +
                "id=" + id +
                ", dishId=" + dishId +
                ", ingredientId=" + ingredientId +
                ", ingredientName='" + ingredientName + '\'' +
                ", weight=" + weight +
                '}';
    }
}