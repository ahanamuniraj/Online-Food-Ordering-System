package com.team11.foodorder.repository;
import com.team11.foodorder.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CartItemRepository extends JpaRepository<CartItem, Long> {}
