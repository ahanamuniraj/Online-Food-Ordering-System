package com.team11.foodorder.repository;
import com.team11.foodorder.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByCustomerId(Long customerId);
}
