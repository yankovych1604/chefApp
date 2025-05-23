package com.maksy.chefapp.dto;

import com.maksy.chefapp.model.enums.DishType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class DishDTO {
    private Long id;
    private String name;
    private DishType type;
    private String description;
    private List<DishIngredientDTO> dishIngredients;
    private double totalWeight;
    private double totalCalories;

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", name='" + Objects.toString(name, "null") + '\'' +
                ", type=" + Objects.toString(type, "null") +
                ", description='" + Objects.toString(description, "null") + '\'' +
                ", totalCalories=" + totalCalories +
                ", totalWeight=" + totalWeight +
                ", dishIngredients=" + (dishIngredients != null ? dishIngredients : "[]") +
                '}';
    }
}