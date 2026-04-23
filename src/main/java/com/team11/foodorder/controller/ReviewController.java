package com.team11.foodorder.controller;

import com.team11.foodorder.entity.AppUser;
import com.team11.foodorder.service.ReviewService;
import com.team11.foodorder.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * REVIEW CONTROLLER — Akshaya Lakshmi Narasimhan (PES2UG23CS046)
 * Minor use case: submit a review for a delivered order.
 */
@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    @GetMapping("/orders/{id}/review")
    public String reviewForm(@PathVariable Long id, Model model) {
        model.addAttribute("orderId", id);
        return "review";
    }

    @PostMapping("/orders/{id}/review")
    public String submitReview(@PathVariable Long id,
                               @RequestParam int rating,
                               @RequestParam String comment,
                               @AuthenticationPrincipal UserDetails principal) {
        AppUser user = userService.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        reviewService.submitReview(id, user.getId(), user.getName(), rating, comment);
        return "redirect:/orders/" + id;
    }
}
