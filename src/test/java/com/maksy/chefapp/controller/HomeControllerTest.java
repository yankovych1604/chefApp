package com.maksy.chefapp.controller;

import com.maksy.chefapp.dto.DishDTO;
import com.maksy.chefapp.service.DishService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(HomeController.class)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DishService dishService;

    @InjectMocks
    private HomeController homeController;

    @Test
    void testIndex() throws Exception {
        DishDTO mockDishDTO = new DishDTO();
        when(dishService.getAllDishes()).thenReturn(Collections.singletonList(mockDishDTO));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("dishes"));
    }
}