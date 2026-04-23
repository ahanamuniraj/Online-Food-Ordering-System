package com.team11.foodorder.pattern;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * OBSERVER PATTERN (Behavioral) — listens for order status change events.
 * In a real app this would send SMS/email notifications.
 */
@Component
public class OrderStatusListener {
    @EventListener
    public void onOrderStatusChanged(OrderStatusChangedEvent event) {
        System.out.printf("[ORDER EVENT] Order #%d status changed to: %s%n",
                event.getOrderId(), event.getNewStatus());
    }
}
