package com.team11.foodorder.controller;

import com.team11.foodorder.entity.FoodOrder;
import com.team11.foodorder.service.OrderService;
import com.team11.foodorder.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingService trackingService;
    private final OrderService orderService;

    @GetMapping("/orders/{id}/track")
    public String track(@PathVariable Long id, Model model) {

        var updates = trackingService.getTrackingForOrder(id);
        FoodOrder order = orderService.getById(id);

        boolean deliveryStarted = "OUT_FOR_DELIVERY".equals(order.getStatus())
                               || "DELIVERED".equals(order.getStatus());

        model.addAttribute("orderId", id);
        model.addAttribute("updates", updates);
        model.addAttribute("deliveryStarted", deliveryStarted);

        return "tracking";
    }
}