package com.maksy.chefapp.controller;

import com.maksy.chefapp.dto.IngredientDTO;
import com.maksy.chefapp.model.Ingredient;
import com.maksy.chefapp.model.enums.IngredientCategory;
import com.maksy.chefapp.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ingredients")
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    @GetMapping
    public String getAllIngredients(@RequestParam(value = "page", defaultValue = "0") int page,
                                    @RequestParam(required = false) IngredientCategory ingredientCategory,
                                    @RequestParam(required = false) Double caloriesFrom,
                                    @RequestParam(required = false) Double caloriesTo,
                                    @RequestParam(required = false) String sortOrder,
                                    Model model) {
        Page<IngredientDTO> ingredientsPage = ingredientService.getAllingredientsFiltered(ingredientCategory, caloriesFrom, caloriesTo, sortOrder, PageRequest.of(page, 6));

        model.addAttribute("selectedCategory", ingredientCategory != null ? ingredientCategory.name() : null);
        model.addAttribute("selectedSortOrder", sortOrder);
        model.addAttribute("caloriesFrom", caloriesFrom);
        model.addAttribute("caloriesTo", caloriesTo);
        model.addAttribute("ingredients", ingredientsPage);

        return "ingredients";
    }

    @GetMapping("/create-ingredient")
    public String createIngredientForm(Model model) {
        model.addAttribute("ingredient", new Ingredient());
        model.addAttribute("formAction", "/ingredients/save");
        model.addAttribute("actionTitle", "Create Ingredient");
        model.addAttribute("submitButtonLabel", "Create");
        return "ingredientForm";
    }

    @PostMapping("/save")
    public String saveIngredient(@ModelAttribute("ingredient") IngredientDTO ingredientDTO,
                                 RedirectAttributes redirectAttributes) {
        try {
            ingredientService.createIngredient(ingredientDTO);
            redirectAttributes.addFlashAttribute("status", "Ingredient saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("status", "Failed to save ingredient.");
        }
        return "redirect:/ingredients";
    }

    @GetMapping("/{id}")
    public String getIngredientDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        IngredientDTO ingredientDTO = ingredientService.findById(id);

        if (ingredientDTO != null) {
            model.addAttribute("ingredient", ingredientDTO);
            return "ingredientDetails";
        } else {
            redirectAttributes.addFlashAttribute("status", "Ingredient not found!");
            return "redirect:/ingredients";
        }
    }

    @GetMapping("/edit/{id}")
    public String editIngredient(@PathVariable Long id, Model model , RedirectAttributes redirectAttributes) {
        IngredientDTO ingredientDTO = ingredientService.findById(id);
        if (ingredientDTO != null) {
            model.addAttribute("ingredient", ingredientDTO);
            model.addAttribute("formAction", "/ingredients/update/" + id);
            model.addAttribute("actionTitle", "Edit Ingredient");
            model.addAttribute("submitButtonLabel", "Update");
            return "ingredientForm";
        }else {
            redirectAttributes.addFlashAttribute("status", "Ingredient not found!");
            return "redirect:/ingredients";
        }

    }

    @PostMapping("/update/{id}")
    public String updateDish(@PathVariable("id") long id,
                             @ModelAttribute("ingredient") IngredientDTO ingredientDTO,
                             RedirectAttributes redirectAttributes) {

        ingredientService.updateIngredient(id, ingredientDTO);
        redirectAttributes.addFlashAttribute("status", "Ingredient updated successfully!");
        return "redirect:/ingredients";
    }

    @GetMapping("/delete/{id}")
    public String deleteIngredient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ingredientService.deleteIngredient(id);
            redirectAttributes.addFlashAttribute("status", "Ingredient deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("status", "Failed to delete ingredient. It's used in dishes.");
        }
        return "redirect:/ingredients";
    }
}