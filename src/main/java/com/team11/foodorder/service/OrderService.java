package com.team11.foodorder.service;

import com.team11.foodorder.entity.FoodOrder;
import com.team11.foodorder.entity.OrderStatus;
import com.team11.foodorder.pattern.OrderStatusChangedEvent;
import com.team11.foodorder.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ORDER SERVICE — Ahana M (PES2UG23CS035)
 * Handles order queries and status updates.
 * Order placement is handled by OrderFacade (Facade pattern).
 * Status changes fire OrderStatusChangedEvent (Observer pattern).
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    public FoodOrder getById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }

    public List<FoodOrder> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerIdOrderByPlacedAtDesc(customerId);
    }

    public List<FoodOrder> getAllOrders() {
        return orderRepository.findAllByOrderByPlacedAtDesc();
    }

    public List<FoodOrder> filterByStatus(String status) {
        if (status == null || status.isBlank()) return getAllOrders();
        return orderRepository.findByStatus(status);
    }

    /** Update order status and fire Observer event. */
    public void updateStatus(Long orderId, String status) {
        FoodOrder order = getById(orderId);
        order.setStatus(status);
        orderRepository.save(order);
        // OBSERVER PATTERN: notify listeners of the status change
        eventPublisher.publishEvent(new OrderStatusChangedEvent(this, orderId, status));
    }
}
