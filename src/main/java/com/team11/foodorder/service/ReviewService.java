package com.team11.foodorder.service;

import com.team11.foodorder.entity.Review;
import com.team11.foodorder.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public Review save(Long orderId, String customerName, int rating, String comment) {
        Review r = new Review();
        r.setOrderId(orderId);
        r.setCustomerName(customerName);
        r.setRating(rating);
        r.setComment(comment);
        r.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(r);
    }

    public List<Review> getByOrderId(Long orderId) {
        return reviewRepository.findByOrderId(orderId);
    }

    public List<Review> getAll() {
        return reviewRepository.findAll();
    }

    // 🔥 ADDED METHOD
    public Review submitReview(Long orderId, Long userId, String customerName, int rating, String comment) {
        return save(orderId, customerName, rating, comment);
    }
}