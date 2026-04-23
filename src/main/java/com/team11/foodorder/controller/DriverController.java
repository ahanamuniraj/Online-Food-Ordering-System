package com.team11.foodorder.controller;

import com.team11.foodorder.entity.Driver;
import com.team11.foodorder.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * DRIVER CONTROLLER
 * Handles the driver dashboard and all driver actions:
 *   GET  /driver/dashboard        – show dashboard
 *   POST /driver/accept/{orderId} – accept an available order
 *   POST /driver/update-location  – push a location update
 *   POST /driver/delivered        – mark current order as delivered
 */
@Controller
@RequestMapping("/driver")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    // ── Dashboard ──────────────────────────────────────────────────────────

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Driver driver = driverService.findByEmail(userDetails.getUsername());
        model.addAttribute("driver", driver);
        model.addAttribute("assignedOrder", driverService.getAssignedOrder(driver));
        model.addAttribute("availableOrders", driverService.getAvailableOrders());
        return "driver-dashboard";
    }

    // ── Accept order ───────────────────────────────────────────────────────

    @PostMapping("/accept/{orderId}")
    public String acceptOrder(@PathVariable Long orderId,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes ra) {
        Driver driver = driverService.findByEmail(userDetails.getUsername());
        try {
            driverService.acceptOrder(driver.getId(), orderId);
            ra.addFlashAttribute("success", "Order #" + orderId + " accepted! Head to the restaurant.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/driver/dashboard";
    }

    // ── Update location ────────────────────────────────────────────────────

    @PostMapping("/update-location")
    public String updateLocation(@RequestParam String location,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes ra) {
        Driver driver = driverService.findByEmail(userDetails.getUsername());
        try {
            driverService.updateLocation(driver.getId(), location);
            ra.addFlashAttribute("success", "Location updated: " + location);
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/driver/dashboard";
    }

    // ── Mark delivered ─────────────────────────────────────────────────────

    @PostMapping("/delivered")
    public String markDelivered(@AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes ra) {
        Driver driver = driverService.findByEmail(userDetails.getUsername());
        try {
            driverService.markDelivered(driver.getId());
            ra.addFlashAttribute("success", "Order marked as delivered. Great work! 🎉");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/driver/dashboard";
    }
}
