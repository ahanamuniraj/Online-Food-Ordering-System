package com.team11.foodorder.pattern;

public class CardPayment implements PaymentProcessor {
    @Override
    public String processPayment(Long orderId, double amount) {
        return Math.random() > 0.05 ? "SUCCESS" : "FAILED";
    }
    @Override
    public String getMethodName() { return "CARD"; }
}
