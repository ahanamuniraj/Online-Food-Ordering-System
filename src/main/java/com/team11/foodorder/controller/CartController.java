package com.team11.foodorder.controller;

import com.team11.foodorder.entity.AppUser;
import com.team11.foodorder.entity.Cart;
import com.team11.foodorder.service.CartService;
import com.team11.foodorder.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * CART CONTROLLER — Ahana M (PES2UG23CS035)
 * Major use case: Add / view / remove items from cart.
 * Minor use case: Apply coupon code.
 */
@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    private AppUser getUser(UserDetails principal) {
        return userService.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/cart")
    public String viewCart(@AuthenticationPrincipal UserDetails principal, Model model) {
        Long uid = getUser(principal).getId();
        Cart cart = cartService.getCart(uid);
        model.addAttribute("cart", cart);
        model.addAttribute("subtotal", cartService.calculateSubtotal(cart));
        model.addAttribute("tax", cartService.calculateTax(cart));
        model.addAttribute("discount", cart.getDiscountAmount() != null ? cart.getDiscountAmount() : 0.0);
        model.addAttribute("total", cartService.calculateTotal(cart));
        return "cart";
    }

    @GetMapping("/cart/add")
    public String addToCart(@RequestParam Long menuItemId,
                            @AuthenticationPrincipal UserDetails principal,
                            RedirectAttributes ra) {
        cartService.addItem(getUser(principal).getId(), menuItemId);
        ra.addFlashAttribute("added", "Item added to cart!");
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove/{itemId}")
    public String removeItem(@PathVariable Long itemId,
                             @AuthenticationPrincipal UserDetails principal) {
        cartService.removeItem(itemId);
        return "redirect:/cart";
    }

    @PostMapping("/cart/coupon")
    public String applyCoupon(@RequestParam String code,
                              @AuthenticationPrincipal UserDetails principal,
                              RedirectAttributes ra) {
        String msg = cartService.applyCoupon(getUser(principal).getId(), code);
        ra.addFlashAttribute("couponMsg", msg);
        return "redirect:/cart";
    }

    @PostMapping("/cart/coupon/remove")
    public String removeCoupon(@AuthenticationPrincipal UserDetails principal) {
        cartService.removeCoupon(getUser(principal).getId());
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(@AuthenticationPrincipal UserDetails principal, Model model) {
        Long uid = getUser(principal).getId();
        Cart cart = cartService.getCart(uid);
        model.addAttribute("cart", cart);
        model.addAttribute("subtotal", cartService.calculateSubtotal(cart));
        model.addAttribute("tax", cartService.calculateTax(cart));
        model.addAttribute("discount", cart.getDiscountAmount() != null ? cart.getDiscountAmount() : 0.0);
        model.addAttribute("total", cartService.calculateTotal(cart));
        return "checkout";
    }
}
