package com.team11.foodorder.repository;
import com.team11.foodorder.entity.FoodOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface OrderRepository extends JpaRepository<FoodOrder, Long> {
    List<FoodOrder> findByCustomerIdOrderByPlacedAtDesc(Long customerId);
    List<FoodOrder> findAllByOrderByPlacedAtDesc();
    List<FoodOrder> findByStatus(String status);
}
