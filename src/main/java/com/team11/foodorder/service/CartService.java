package com.team11.foodorder.service;

import com.team11.foodorder.entity.*;
import com.team11.foodorder.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

/**
 * CART SERVICE — Ahana M (PES2UG23CS035)
 * Single Responsibility: cart management and coupon validation only.
 * DRY: tax/total calculation consolidated here, not repeated in controllers.
 */
@Service
@RequiredArgsConstructor
public class CartService {

    private static final double TAX_RATE = 0.05;

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final CouponRepository couponRepository;

    /** Get or create cart for a customer. */
    public Cart getOrCreateCart(Long customerId) {
        return cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setCustomerId(customerId);
                    cart.setDiscountAmount(0.0);
                    return cartRepository.save(cart);
                });
    }

    public Cart getCart(Long customerId) {
        return getOrCreateCart(customerId);
    }

    /** Add a menu item to cart (increments quantity if already present). */
    public void addItem(Long customerId, Long menuItemId) {
        Cart cart = getOrCreateCart(customerId);
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("MenuItem not found: " + menuItemId));

        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> i.getMenuItemId().equals(menuItemId))
                .findFirst();

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + 1);
            cartItemRepository.save(item);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setMenuItemId(menuItemId);
            item.setMenuItemName(menuItem.getName());
            item.setPrice(menuItem.getPrice());
            item.setQuantity(1);
            cartItemRepository.save(item);
        }
    }

    /** Remove a single cart item by ID. */
    public void removeItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    /** Apply coupon — validates expiry, active status, and minimum order. */
    public String applyCoupon(Long customerId, String code) {
        Cart cart = getOrCreateCart(customerId);
        double subtotal = calculateSubtotal(cart);

        Optional<Coupon> couponOpt = couponRepository.findByCodeIgnoreCase(code);
        if (couponOpt.isEmpty()) return "Invalid coupon code.";

        Coupon coupon = couponOpt.get();
        if (!coupon.isActive()) return "Coupon is no longer active.";
        if (coupon.getExpiryDate().isBefore(LocalDate.now())) return "Coupon has expired.";
        if (subtotal < coupon.getMinOrderAmount())
            return "Minimum order amount ₹" + coupon.getMinOrderAmount() + " required.";

        double discount = subtotal * (coupon.getDiscountPercent() / 100.0);
        cart.setDiscountAmount(discount);
        cart.setAppliedCoupon(coupon.getCode());
        cartRepository.save(cart);
        return "Coupon applied! You save ₹" + String.format("%.2f", discount);
    }

    /** Remove applied coupon from cart. */
    public void removeCoupon(Long customerId) {
        Cart cart = getOrCreateCart(customerId);
        cart.setDiscountAmount(0.0);
        cart.setAppliedCoupon(null);
        cartRepository.save(cart);
    }

    // --- DRY calculation methods used by both CartController and OrderService ---

    public double calculateSubtotal(Cart cart) {
        return cart.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
    }

    public double calculateTax(Cart cart) {
        return calculateSubtotal(cart) * TAX_RATE;
    }

    public double calculateTotal(Cart cart) {
        double subtotal = calculateSubtotal(cart);
        double tax = subtotal * TAX_RATE;
        double discount = cart.getDiscountAmount() != null ? cart.getDiscountAmount() : 0.0;
        return subtotal + tax - discount;
    }

    public void clearCart(Long customerId) {
        Cart cart = getOrCreateCart(customerId);
        cart.getItems().clear();
        cart.setDiscountAmount(0.0);
        cart.setAppliedCoupon(null);
        cartRepository.save(cart);
    }
}
