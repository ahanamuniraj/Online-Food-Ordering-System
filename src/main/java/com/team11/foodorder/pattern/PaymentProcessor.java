package com.team11.foodorder.pattern;

public interface PaymentProcessor {
    String processPayment(Long orderId, double amount);
    String getMethodName();
}
