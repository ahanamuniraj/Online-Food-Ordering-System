package com.team11.foodorder.pattern;

import org.springframework.context.ApplicationEvent;

/**
 * OBSERVER PATTERN (Behavioral) — Shared (Ahana + Akshaya)
 * Fired whenever an order's status changes.
 */
public class OrderStatusChangedEvent extends ApplicationEvent {
    private final Long orderId;
    private final String newStatus;

    public OrderStatusChangedEvent(Object source, Long orderId, String newStatus) {
        super(source);
        this.orderId = orderId;
        this.newStatus = newStatus;
    }

    public Long getOrderId() { return orderId; }
    public String getNewStatus() { return newStatus; }
}
