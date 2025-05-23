package com.maksy.chefapp.controller;

import com.maksy.chefapp.dto.DishDTO;
import com.maksy.chefapp.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private DishService dishService;

    @GetMapping("/")
    public String index(Model model) {
        List<DishDTO> dishes = dishService.get3Dishes();
        model.addAttribute("dishes", dishes);
        return "index";
    }
}