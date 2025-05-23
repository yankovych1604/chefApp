package com.maksy.chefapp.dto;

import com.maksy.chefapp.model.enums.IngredientCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IngredientDTO {
    private Long id;
    private String name;
    private Double caloriesPer100g;
    private IngredientCategory category;

    @Override
    public String toString() {
        return "IngredientDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", caloriesPer100g=" + caloriesPer100g +
                ", category=" + category +
                '}';
    }
}