package com.team11.foodorder.service;

import com.team11.foodorder.entity.FoodOrder;
import com.team11.foodorder.entity.Payment;
import com.team11.foodorder.repository.OrderRepository;
import com.team11.foodorder.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PAYMENT SERVICE
 *
 * Key change from original:
 *   ❌ REMOVED: insertTrackingSteps() calls after payment confirmation.
 *   ✅ REASON:  Tracking must only start after OTP verification (handled by OtpService).
 *              Payment confirms the order financially; delivery begins only when the
 *              delivery agent presents the OTP to the customer.
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 1: Create Payment record on method selection
    // ─────────────────────────────────────────────────────────────────────────

    public Payment processPayment(Long orderId, String method) {

        if (method == null || method.isBlank()) {
            throw new RuntimeException("Payment method missing");
        }

        FoodOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setPaymentMethod(method.toUpperCase());
        payment.setAmount(order.getTotal());
        payment.setPaymentTime(LocalDateTime.now());

        if (method.equalsIgnoreCase("COD")) {
            // COD is immediately "paid" — mark order CONFIRMED, but do NOT start tracking
            payment.setStatus("SUCCESS");
            order.setStatus("CONFIRMED");
            orderRepository.save(order);
        } else {
            payment.setStatus("PENDING");
        }

        return paymentRepository.save(payment);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 2: Confirm online payment (UPI / CARD)
    // ─────────────────────────────────────────────────────────────────────────

    public void confirmPayment(Long orderId) {

        List<Payment> payments = paymentRepository.findAll()
                .stream()
                .filter(p -> p.getOrderId().equals(orderId))
                .toList();

        if (payments.isEmpty()) {
            System.out.println("❌ Payment NOT found for order: " + orderId);
            return;
        }

        Payment payment = payments.get(payments.size() - 1);

        if ("SUCCESS".equals(payment.getStatus())) {
            System.out.println("⚠️ Already confirmed for order: " + orderId);
            return;
        }

        payment.setStatus("SUCCESS");
        paymentRepository.save(payment);

        FoodOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        // Mark as CONFIRMED — delivery will NOT start until OTP is verified
        order.setStatus("CONFIRMED");
        orderRepository.save(order);

        System.out.println("✅ Payment confirmed for order #" + orderId +
                           " — awaiting OTP verification to start delivery.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET PAYMENT
    // ─────────────────────────────────────────────────────────────────────────

    public Payment getLatestPayment(Long orderId) {
        List<Payment> payments = paymentRepository.findAll()
                .stream()
                .filter(p -> p.getOrderId().equals(orderId))
                .toList();

        if (payments.isEmpty()) return null;
        return payments.get(payments.size() - 1);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CARD VALIDATION
    // ─────────────────────────────────────────────────────────────────────────

    public boolean isValidCard(String cardNumber) {
        return cardNumber != null && cardNumber.matches("\\d{16}");
    }
}