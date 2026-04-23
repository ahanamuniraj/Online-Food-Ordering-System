package com.team11.foodorder.pattern;

/**
 * FACTORY PATTERN (Creational) — Akshaya Lakshmi Narasimhan
 * Creates the appropriate PaymentProcessor based on selected payment method.
 * Open/Closed: new methods (e.g. WalletPayment) can be added without modifying this factory's clients.
 */
public class PaymentFactory {
    public static PaymentProcessor getProcessor(String method) {
        return switch (method.toUpperCase()) {
            case "UPI"  -> new UpiPayment();
            case "CARD" -> new CardPayment();
            case "COD"  -> new CodPayment();
            default     -> throw new IllegalArgumentException("Unknown payment method: " + method);
        };
    }
}
