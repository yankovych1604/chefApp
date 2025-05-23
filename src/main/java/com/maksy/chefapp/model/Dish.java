package com.maksy.chefapp.model;

import com.maksy.chefapp.model.enums.DishType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Dish")
@Getter
@Setter
public class Dish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private DishType type;

    @Column(name = "description")
    private String description;

    @Column(name = "total_calories")
    private double totalCalories;

    @Column(name = "total_weight")
    private double totalWeight;

    @OneToMany(mappedBy = "dishId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DishIngredient> dishIngredients;

    public Dish() {

    }

    public Dish(String name, DishType type) {
        this.type = type;
        this.name = name;
    }

    public void removeIngredients(List<Long> ingredientIds) {
        if (dishIngredients == null) return;
        this.dishIngredients.removeIf(d -> ingredientIds.contains(d.getId()));
    }


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