package com.team11.foodorder.controller;

import com.team11.foodorder.entity.Restaurant;
import com.team11.foodorder.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RESTAURANT CONTROLLER — Adarsh L (PES2UG23CS025)
 * Major use case: browse restaurants and view menus.
 * Minor use case: search restaurants by name.
 */
@Controller
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping("/restaurants")
    public String listRestaurants(@RequestParam(required = false) String search, Model model) {
        List<Restaurant> restaurants = (search != null && !search.isBlank())
                ? restaurantService.searchByName(search)
                : restaurantService.getAllRestaurants();
        model.addAttribute("restaurants", restaurants);
        model.addAttribute("search", search);
        return "restaurants";
    }

    @GetMapping("/restaurants/{id}")
    public String restaurantDetail(@PathVariable Long id, Model model) {
        model.addAttribute("restaurant", restaurantService.getById(id));
        model.addAttribute("menu", restaurantService.getMenuByRestaurant(id));
        return "restaurant-detail";
    }
}
