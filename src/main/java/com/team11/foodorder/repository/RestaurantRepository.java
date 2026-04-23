package com.team11.foodorder.repository;
import com.team11.foodorder.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByNameContainingIgnoreCase(String name);
    List<Restaurant> findByOpenTrue();
}
