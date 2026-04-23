package com.team11.foodorder.repository;
import com.team11.foodorder.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByOrderId(Long orderId);
}
