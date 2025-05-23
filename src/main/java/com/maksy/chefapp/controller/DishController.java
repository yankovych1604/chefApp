package com.maksy.chefapp.controller;

import com.maksy.chefapp.dto.DishDTO;
import com.maksy.chefapp.dto.DishIngredientDTO;
import com.maksy.chefapp.dto.IngredientDTO;
import com.maksy.chefapp.model.Dish;
import com.maksy.chefapp.model.enums.DishType;
import com.maksy.chefapp.service.DishIngredientService;
import com.maksy.chefapp.service.DishService;
import com.maksy.chefapp.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dishes")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishIngredientService dishIngredientService;
    @Autowired
    private IngredientService ingredientService;


    @GetMapping("/all")
    public String showAllDishes(@RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(required = false) DishType dishType,
                                Model model) {
        Page<DishDTO> dishesPage = dishService.getAllDishesFiltered(dishType, PageRequest.of(page, 6));

        model.addAttribute("dishType", dishType != null ? dishType.name() : null);
        model.addAttribute("dishes", dishesPage);

        return "dishes";
    }

    @GetMapping("/{id}")
    public String showDishById(@PathVariable("id") long id,
                               @RequestParam(required = false) Double minCalories,
                               @RequestParam(required = false) Double maxCalories,
                               Model model) {
        DishDTO dishDTO = dishService.getDishById(id);
        if (dishDTO != null) {
            List<DishIngredientDTO> dishIngredients = dishIngredientService.findAllByDishId(dishDTO.getId());

            Map<Long, Double> caloriesPer100gMap = ingredientService
                    .findAllById(dishIngredients.stream().map(DishIngredientDTO::getIngredientId).toList())
                    .stream()
                    .collect(Collectors.toMap(IngredientDTO::getId, IngredientDTO::getCaloriesPer100g));

            Map<Long, Double> ingredientCalories = new HashMap<>();
            List<Long> highlightedIngredientIds = new ArrayList<>();

            boolean applyHighlighting = (minCalories != null || maxCalories != null);

            for (DishIngredientDTO di : dishIngredients) {
                Double caloriesPer100g = caloriesPer100gMap.get(di.getIngredientId());
                if (caloriesPer100g != null) {
                    double total = caloriesPer100g * di.getWeight() / 100;
                    ingredientCalories.put(di.getIngredientId(), total);

                    if (applyHighlighting && (minCalories == null || total >= minCalories) && (maxCalories == null || total <= maxCalories)) {
                        highlightedIngredientIds.add(di.getIngredientId());
                    }
                }
            }

            model.addAttribute("dish", dishDTO);
            model.addAttribute("dishIngredients", dishIngredients);
            model.addAttribute("ingredientCalories", ingredientCalories);
            model.addAttribute("highlightedIngredientIds", highlightedIngredientIds);
            model.addAttribute("minCalories", minCalories);
            model.addAttribute("maxCalories", maxCalories);

            return "dishDetails";
        } else {
            model.addAttribute("error", "Dish not found");
            return "error";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteDishById(@PathVariable("id") long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            dishService.deleteDish(id);
            redirectAttributes.addFlashAttribute("status", "Dish deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("status", "Failed to delete dish. Please try again.");
        }
        return "redirect:/dishes/all";
    }

    @GetMapping("/edit/{id}")
    public String editDishById(@PathVariable("id") long id, Model model, RedirectAttributes redirectAttributes) {
        DishDTO dishDTO = dishService.getDishById(id);

        if (dishDTO!=null) {
            List<DishIngredientDTO>dishIngredientDTOS =  dishIngredientService.findAllByDishId(id);
            List<IngredientDTO> allIngredients = ingredientService.getAllIngredients();
            model.addAttribute("dish", dishDTO);
            model.addAttribute("dishIngredients", dishIngredientDTOS);
            model.addAttribute("allIngredients", allIngredients);
            model.addAttribute("actionTitle", "Edit dish");
            model.addAttribute("formAction", "/dishes/update/" + dishDTO.getId());
            model.addAttribute("formTitle", "Edit Dish");
            return "editDish";
        } else {
            redirectAttributes.addFlashAttribute("status", "Dish not found!");
            return "redirect:/dishes/all";
        }
    }

    @PostMapping("/update/{id}")
    public String updateDish(@PathVariable("id") long id,
                             @ModelAttribute("dishDTO") DishDTO dishDTO,
                             @RequestParam(value = "deleteIngredientIds", required = false) List<Long> deleteIngredientIds,
                             RedirectAttributes redirectAttributes) {

        dishService.updateDish(id, dishDTO, deleteIngredientIds);
        redirectAttributes.addFlashAttribute("status", "Dish updated successfully!");
        return "redirect:/dishes/all";
    }

    @PostMapping("/save")
    public String saveDish(@ModelAttribute("dishDTO") DishDTO dishDTO, RedirectAttributes redirectAttributes) {
        dishService.createDish(dishDTO);
        redirectAttributes.addFlashAttribute("status", "Dish saved successfully!");
        return "redirect:/dishes/all";
    }


    @GetMapping("/create-dish")
    public String addDish(Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("dish", new Dish());
            model.addAttribute("dishIngredients", new ArrayList<>());
            model.addAttribute("allIngredients", ingredientService.getAllIngredients());
            model.addAttribute("actionTitle", "Create dish");
            model.addAttribute("formAction", "/dishes/save");
            model.addAttribute("formTitle", "Create Dish");

            return "editDish";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("status", "Failed to add dish. Please try again.");

            return "redirect:/dishes/create-dish";
        }
    }
}