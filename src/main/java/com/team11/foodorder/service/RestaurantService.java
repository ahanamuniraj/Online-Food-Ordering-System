package com.team11.foodorder.service;

import com.team11.foodorder.entity.Menu;
import com.team11.foodorder.entity.MenuItem;
import com.team11.foodorder.entity.Restaurant;
import com.team11.foodorder.repository.MenuItemRepository;
import com.team11.foodorder.repository.MenuRepository;
import com.team11.foodorder.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final MenuRepository menuRepository;

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public Restaurant getById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
    }

    public void addRestaurant(String name, String location, String cuisine) {
        Restaurant r = new Restaurant();
        r.setName(name);
        r.setCuisine(cuisine);
        r.setOpen(true);
        restaurantRepository.save(r);
    }

    public void deleteRestaurant(Long id) {
        restaurantRepository.deleteById(id);
    }

    public void addMenuItem(Long restaurantId, String name, String category, double price, boolean available) {
        Restaurant restaurant = getById(restaurantId);

        Menu menu = restaurant.getMenu();
        if (menu == null) {
            menu = new Menu();
            menu.setRestaurant(restaurant);
            menu = menuRepository.save(menu);
            restaurant.setMenu(menu);
            restaurantRepository.save(restaurant);
        }

        MenuItem item = new MenuItem();
        item.setName(name);
        item.setCategory(category);
        item.setPrice(price);
        item.setAvailable(available);
        item.setMenu(menu);

        menuItemRepository.save(item);
    }

    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }

    public void toggleMenuItemAvailability(Long id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        item.setAvailable(!item.isAvailable());
        menuItemRepository.save(item);
    }

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    public List<MenuItem> getMenuByRestaurant(Long restaurantId) {
        return menuItemRepository.findAll()
                .stream()
                .filter(item -> item.getMenu().getRestaurant().getId().equals(restaurantId))
                .toList();
    }
    public List<Restaurant> searchByName(String keyword) {
    return restaurantRepository.findAll()
            .stream()
            .filter(r -> r.getName().toLowerCase().contains(keyword.toLowerCase()))
            .toList();
}

    public Restaurant save(Restaurant r) {
        return restaurantRepository.save(r);
    }

    public com.team11.foodorder.entity.MenuItem getMenuItemById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MenuItem not found: " + id));
    }

    public void updateMenuItem(Long id, String name, String category, double price, boolean available) {
        com.team11.foodorder.entity.MenuItem item = getMenuItemById(id);
        item.setName(name);
        item.setCategory(category);
        item.setPrice(price);
        item.setAvailable(available);
        menuItemRepository.save(item);
    }
}
