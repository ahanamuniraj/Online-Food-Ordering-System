package com.team11.foodorder.controller;

import com.team11.foodorder.entity.DeliveryOtp;
import com.team11.foodorder.service.OtpService;
import com.team11.foodorder.service.OtpService.OtpVerificationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * OTP CONTROLLER
 *
 * Customer-facing endpoints:
 *   GET  /orders/{id}/verify-otp        – show OTP entry form
 *   POST /orders/{id}/verify-otp        – submit OTP; on success → tracking page
 *
 * Admin-facing endpoints  (secured by ROLE_ADMIN in SecurityConfig):
 *   POST /admin/orders/{id}/generate-otp – generate / regenerate OTP for an order
 */
@Controller
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    // ─────────────────────────────────────────────────────────────────────────
    // CUSTOMER: Show OTP entry form
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/orders/{id}/verify-otp")
    public String showOtpForm(@PathVariable Long id, Model model) {
        model.addAttribute("orderId", id);
        return "verify-otp";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CUSTOMER: Submit OTP
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/orders/{id}/verify-otp")
    public String verifyOtp(@PathVariable Long id,
                            @RequestParam String otpCode,
                            Model model,
                            RedirectAttributes redirectAttributes) {

        OtpVerificationResult result = otpService.verifyOtp(id, otpCode);

        if (result.success()) {
            // OTP verified → delivery started → go to live tracking
            redirectAttributes.addFlashAttribute("successMessage", result.message());
            return "redirect:/orders/" + id + "/track";
        }

        // OTP failed → stay on the form, show error
        model.addAttribute("orderId", id);
        model.addAttribute("errorMessage", result.message());
        return "verify-otp";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ADMIN: Generate / regenerate OTP
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/admin/orders/{id}/generate-otp")
    public String generateOtp(@PathVariable Long id,
                              RedirectAttributes redirectAttributes) {
        try {
            DeliveryOtp otp = otpService.generateOtp(id);
            redirectAttributes.addFlashAttribute("otpGenerated",
                    "OTP for order #" + id + " is: " + otp.getOtpCode() +
                    "  (expires: " + otp.getExpiresAt() + ")");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("otpError", e.getMessage());
        }
        return "redirect:/admin/orders";
    }
}