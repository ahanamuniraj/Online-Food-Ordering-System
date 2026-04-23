package com.team11.foodorder.pattern;

import com.team11.foodorder.entity.Cart;
import com.team11.foodorder.entity.FoodOrder;
import com.team11.foodorder.entity.OrderItem;
import com.team11.foodorder.entity.OrderStatus;
import com.team11.foodorder.pattern.OrderStatusChangedEvent;
import com.team11.foodorder.repository.OrderRepository;
import com.team11.foodorder.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FACADE PATTERN (Structural) — Ahana M (PES2UG23CS035)
 *
 * OrderFacade hides the complexity of the entire order-placement pipeline.
 * The controller calls ONE method instead of orchestrating five subsystems:
 *
 *   1. CartService  — validate cart is non-empty, read items
 *   2. CartService  — calculate subtotal, tax, coupon discount
 *   3. OrderRepository — persist the FoodOrder and OrderItems
 *   4. ApplicationEventPublisher — fire OrderStatusChangedEvent (Observer pattern)
 *   5. CartService  — clear the cart after order is placed
 *
 * Without this facade, all five steps would live in the controller,
 * violating Single Responsibility and making the controller hard to test.
 */
@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Single entry point for the full order placement pipeline.
     *
     * @param customerId      the logged-in customer's ID
     * @param customerName    the customer's display name
     * @param deliveryAddress the delivery address from checkout form
     * @return the saved FoodOrder with status PENDING
     */
    public FoodOrder placeOrder(Long customerId, String customerName, String deliveryAddress) {

        // Step 1 — validate cart
        Cart cart = cartService.getCart(customerId);
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot place order: cart is empty.");
        }

        // Step 2 — calculate totals (DRY: all math lives in CartService)
        double subtotal = cartService.calculateSubtotal(cart);
        double tax      = cartService.calculateTax(cart);
        double discount = cart.getDiscountAmount() != null ? cart.getDiscountAmount() : 0.0;
        double total    = subtotal + tax - discount;

        // Step 3 — build and persist the order
        FoodOrder order = new FoodOrder();
        order.setCustomerId(customerId);
        order.setCustomerName(customerName);
        order.setDeliveryAddress(deliveryAddress);
        order.setStatus(OrderStatus.PENDING.name());
        order.setSubtotal(subtotal);
        order.setTax(tax);
        order.setDiscount(discount);
        order.setTotal(total);
        order.setPlacedAt(LocalDateTime.now());

        List<OrderItem> items = cart.getItems().stream().map(ci -> {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setItemName(ci.getMenuItemName());
            oi.setPrice(ci.getPrice());
            oi.setQuantity(ci.getQuantity());
            return oi;
        }).collect(Collectors.toList());
        order.setItems(items);

        FoodOrder saved = orderRepository.save(order);

        // Step 4 — fire Observer event (decoupled status notification)
        eventPublisher.publishEvent(new OrderStatusChangedEvent(this, saved.getId(), OrderStatus.PENDING.name()));

        // Step 5 — clear the cart
        cartService.clearCart(customerId);

        return saved;
    }
}
