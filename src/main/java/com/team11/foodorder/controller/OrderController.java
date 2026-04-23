package com.team11.foodorder.controller;

import com.team11.foodorder.entity.AppUser;
import com.team11.foodorder.entity.FoodOrder;
import com.team11.foodorder.pattern.OrderFacade;
import com.team11.foodorder.service.OrderService;
import com.team11.foodorder.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * ORDER CONTROLLER — Ahana M (PES2UG23CS035)
 * Uses OrderFacade for order placement (single method call).
 */
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderFacade orderFacade;
    private final OrderService orderService;
    private final UserService userService;

    private AppUser getUser(UserDetails principal) {
        return userService.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping("/order/place")
    public String placeOrder(@RequestParam String deliveryAddress,
                             @AuthenticationPrincipal UserDetails principal) {
        AppUser user = getUser(principal);
        // FACADE: single call encapsulates the entire order pipeline
        FoodOrder order = orderFacade.placeOrder(user.getId(), user.getName(), deliveryAddress);
        return "redirect:/orders/" + order.getId() + "/confirm";
    }

    @GetMapping("/orders/{id}/confirm")
    public String confirm(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.getById(id));
        return "order-confirmation";
    }

    @GetMapping("/orders")
    public String myOrders(@AuthenticationPrincipal UserDetails principal, Model model) {
        AppUser user = getUser(principal);
        model.addAttribute("orders", orderService.getOrdersByCustomer(user.getId()));
        return "orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.getById(id));
        return "order-detail";
    }
}
