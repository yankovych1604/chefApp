package com.maksy.chefapp.service;

import com.maksy.chefapp.dto.IngredientDTO;
import com.maksy.chefapp.exception.EntityNotFoundException;
import com.maksy.chefapp.exception.StatusCodes;
import com.maksy.chefapp.mapper.IngredientMapper;
import com.maksy.chefapp.model.Ingredient;
import com.maksy.chefapp.model.enums.IngredientCategory;
import com.maksy.chefapp.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class IngredientService {

    @Autowired
    private IngredientMapper ingredientMapper;                                                     // Мапер DTO ↔ Entity

    @Autowired
    private IngredientRepository ingredientRepository;                                  // Репозиторій для доступу до БД

    // Повертає всі інгредієнти як список DTO
    public List<IngredientDTO> getAllIngredients() {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        return ingredients.stream()
                .map(ingredientMapper::ingredientToIngredientDTO)
                .collect(toList());
    }

    // Повертає інгредієнт за ID або кидає помилку, якщо не знайдено
    public IngredientDTO findById(Long ingredientId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId).orElseThrow(()->new EntityNotFoundException(StatusCodes.ENTITY_NOT_FOUND.name(), "Ingredient not found"));
        return ingredientMapper.ingredientToIngredientDTO(ingredient);
    }

    // Повертає список DTO для кількох інгредієнтів за ID
    public List<IngredientDTO> findAllById(List<Long>  ingredientIds) {
        List<Ingredient> ingredients = ingredientRepository.findAllById(ingredientIds);
        return ingredients.stream()
                .map(ingredientMapper::ingredientToIngredientDTO)
                .toList();
    }

    // Фільтрація за категорією з пагінацією
    public Page<IngredientDTO> getAllingredientsByCatagory(IngredientCategory ingredientCategory, Pageable pageable) {
        Page<Ingredient> ingredientsPage = ingredientRepository.findAllByCategory(ingredientCategory, pageable);
        List<IngredientDTO> ingredientsDTO = ingredientsPage.getContent().stream()
                .map(ingredientMapper::ingredientToIngredientDTO)
                .toList();

        return new PageImpl<>(ingredientsDTO, pageable, ingredientsPage.getTotalElements());
    }

    // Створення нового інгредієнта
    public IngredientDTO createIngredient(IngredientDTO ingredientDTO) {
        Ingredient ingredient = ingredientMapper.ingredientDTOToIngredient(ingredientDTO);

        ingredientRepository.save(ingredient);

        return ingredientMapper.ingredientToIngredientDTO(ingredient);
    }

    // Оновлення існуючого інгредієнта. Кидає помилку, якщо не знайдено
    public IngredientDTO updateIngredient(long id, IngredientDTO ingredientDTO) {
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(()->
                new EntityNotFoundException(StatusCodes.ENTITY_NOT_FOUND.name(), "Ingredient not found"));

        // Оновлення полів
        ingredient.setName(ingredientDTO.getName());
        ingredient.setCaloriesPer100g(ingredientDTO.getCaloriesPer100g());
        ingredient.setCategory(ingredientDTO.getCategory());

        ingredientRepository.save(ingredient);
        return ingredientMapper.ingredientToIngredientDTO(ingredient);
    }

    // Видалення інгредієнта за ID
    public void deleteIngredient(Long id) {
        ingredientRepository.deleteById(id);
    }

    // Повертає фільтрований список інгредієнтів (за категорією, за діапазоном калорій, з сортуванням по калорійності)
    public Page<IngredientDTO> getAllingredientsFiltered(IngredientCategory ingredientCategory, Double caloriesFrom, Double caloriesTo, String sortOrder, Pageable pageable) {
        Sort sort = Sort.unsorted();
        if ("highToLow".equals(sortOrder)) {
            sort = Sort.by(Sort.Direction.DESC, "caloriesPer100g");
        } else if ("lowToHigh".equals(sortOrder)) {
            sort = Sort.by(Sort.Direction.ASC, "caloriesPer100g");
        }

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        // Запит з урахуванням фільтрів
        Page<Ingredient> ingredientPage = ingredientRepository.findAllFiltered(
                ingredientCategory, caloriesFrom, caloriesTo, sortedPageable
        );

        // Перетворюємо результат на DTO
        List<IngredientDTO> ingredientDTOS = ingredientPage.getContent().stream()
                .map(ingredientMapper::ingredientToIngredientDTO)
                .toList();

        return new PageImpl<>(ingredientDTOS, pageable, ingredientPage.getTotalElements());
    }
}