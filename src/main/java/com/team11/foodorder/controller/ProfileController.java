package com.team11.foodorder.controller;

import com.team11.foodorder.entity.AppUser;
import com.team11.foodorder.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * PROFILE CONTROLLER — Aarna M (PES2UG23CS009)
 * Minor use case: view and edit customer profile (/profile, /profile/edit).
 * Uses Spring Security @AuthenticationPrincipal — no manual session handling needed.
 */
@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails principal, Model model) {
        AppUser user = userService.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile/edit")
    public String editProfile(@RequestParam String name,
                              @RequestParam String phone,
                              @AuthenticationPrincipal UserDetails principal,
                              RedirectAttributes ra) {
        AppUser user = userService.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        userService.updateProfile(user.getId(), name, phone);
        ra.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/profile";
    }
}
