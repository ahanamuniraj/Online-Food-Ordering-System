package com.team11.foodorder.pattern;

public class CodPayment implements PaymentProcessor {
    @Override
    public String processPayment(Long orderId, double amount) {
        return "SUCCESS"; // COD always succeeds at order time
    }
    @Override
    public String getMethodName() { return "COD"; }
}
