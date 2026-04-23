package com.team11.foodorder.pattern;

public class UpiPayment implements PaymentProcessor {
    @Override
    public String processPayment(Long orderId, double amount) {
        // Simulate UPI payment processing
        return Math.random() > 0.1 ? "SUCCESS" : "FAILED";
    }
    @Override
    public String getMethodName() { return "UPI"; }
}
