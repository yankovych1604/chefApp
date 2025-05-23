package com.maksy.chefapp.model;

import com.maksy.chefapp.model.enums.DishType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DishTest {
    @Test
    void testSettersAndGetters() {
        Dish dish = new Dish();
        dish.setId(1L);
        dish.setName("Olivier");
        dish.setType(DishType.SALAD);
        dish.setDescription("Classic salad");
        dish.setTotalCalories(200);
        dish.setTotalWeight(300);

        assertThat(dish.getId()).isEqualTo(1L);
        assertThat(dish.getName()).isEqualTo("Olivier");
        assertThat(dish.getType()).isEqualTo(DishType.SALAD);
        assertThat(dish.getDescription()).isEqualTo("Classic salad");
        assertThat(dish.getTotalCalories()).isEqualTo(200);
        assertThat(dish.getTotalWeight()).isEqualTo(300);
    }

    @Test
    void testToStringNotNull() {
        Dish dish = new Dish("Soup", DishType.SOUP);
        dish.setDescription("Warm and tasty");
        String str = dish.toString();

        assertThat(str).contains("Soup");
        assertThat(str).contains("SOUP");
        assertThat(str).contains("Warm and tasty");
    }

    @Test
    void testRemoveIngredients() {
        DishIngredient ing1 = new DishIngredient();
        ing1.setId(10L);
        DishIngredient ing2 = new DishIngredient();
        ing2.setId(20L);
        DishIngredient ing3 = new DishIngredient();
        ing3.setId(30L);

        List<DishIngredient> ingredientList = new ArrayList<>();
        ingredientList.add(ing1);
        ingredientList.add(ing2);
        ingredientList.add(ing3);

        Dish dish = new Dish();
        dish.setDishIngredients(ingredientList);

        dish.removeIngredients(List.of(10L, 30L));

        assertThat(dish.getDishIngredients())
                .extracting(DishIngredient::getId)
                .containsExactly(20L);
    }

    @Test
    void testRemoveIngredients_NullSafe() {
        Dish dish = new Dish();
        dish.setDishIngredients(null);
        dish.removeIngredients(List.of(1L, 2L));
    }

    @Test
    void testToString_WhenDishIngredientsIsNull() {
        Dish dish = new Dish();
        dish.setName("Stew");
        dish.setType(DishType.MAIN);
        dish.setDescription("Hearty stew");
        dish.setDishIngredients(null);

        String str = dish.toString();

        assertThat(str).contains("dishIngredients=[]");
        assertThat(str).contains("Stew");
        assertThat(str).contains("MAIN");
        assertThat(str).contains("Hearty stew");
    }

    @Test
    void testToString_WhenDishIngredientsIsEmpty() {
        Dish dish = new Dish();
        dish.setName("Pizza");
        dish.setType(DishType.MAIN);
        dish.setDishIngredients(new ArrayList<>());

        String str = dish.toString();

        assertThat(str).contains("dishIngredients=[]");
        assertThat(str).contains("Pizza");
    }
}
