package com.team11.foodorder.controller;

import com.team11.foodorder.entity.Payment;
import com.team11.foodorder.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * PAYMENT CONTROLLER
 *
 * Key change from original:
 *   ❌ OLD: after payment, redirect → /orders/{id}/track   (starts delivery immediately)
 *   ✅ NEW: after payment, redirect → /orders/{id}/verify-otp  (gate before delivery)
 */
@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // STEP 1: Show payment method selection page
    @GetMapping("/orders/{id}/pay")
    public String payPage(@PathVariable Long id, Model model) {
        model.addAttribute("orderId", id);
        return "payment";
    }

    // STEP 2: Process method selection
    @PostMapping("/orders/{id}/pay")
    public String processPayment(@PathVariable Long id,
                                 @RequestParam String method,
                                 Model model) {

        Payment payment = paymentService.processPayment(id, method);

        // COD is confirmed immediately → go to OTP verification page
        if ("SUCCESS".equals(payment.getStatus())) {
            return "redirect:/orders/" + id + "/verify-otp";
        }

        if (method.equalsIgnoreCase("UPI")) {
            model.addAttribute("orderId", id);
            return "payment-qr";
        }

        if (method.equalsIgnoreCase("CARD")) {
            model.addAttribute("orderId", id);
            return "card-payment";
        }

        return "redirect:/orders/" + id + "/pay";
    }

    // STEP 3: Confirm online payment (UPI / CARD "I have paid" button)
    @PostMapping("/orders/{id}/pay/confirm")
    public String confirmPayment(@PathVariable Long id) {
        paymentService.confirmPayment(id);
        // ✅ Goes to OTP verification, NOT tracking
        return "redirect:/orders/" + id + "/verify-otp";
    }

    // SUCCESS PAGE
    @GetMapping("/orders/{id}/pay/success")
    public String success(@PathVariable Long id, Model model) {
        model.addAttribute("orderId", id);
        Payment payment = paymentService.getLatestPayment(id);
        if (payment != null) {
            model.addAttribute("payment", payment);
        }
        return "payment-success";
    }

    // FAILURE PAGE
    @GetMapping("/orders/{id}/pay/failure")
    public String failure(@PathVariable Long id, Model model) {
        model.addAttribute("orderId", id);
        return "payment-failure";
    }
}