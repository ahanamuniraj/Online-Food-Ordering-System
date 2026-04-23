package com.team11.foodorder.controller;

import com.team11.foodorder.entity.Admin;
import com.team11.foodorder.entity.Coupon;
import com.team11.foodorder.entity.Driver;
import com.team11.foodorder.entity.Restaurant;
import com.team11.foodorder.entity.VehicleType;
import com.team11.foodorder.repository.CouponRepository;
import com.team11.foodorder.repository.UserRepository;
import com.team11.foodorder.service.DriverService;
import com.team11.foodorder.service.OrderService;
import com.team11.foodorder.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final RestaurantService restaurantService;
    private final OrderService orderService;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final PasswordEncoder passwordEncoder;
    private final DriverService driverService;

    // ── Dashboard ──────────────────────────────────────────

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        long totalUsers       = userRepository.findAll().stream().filter(u -> "ROLE_CUSTOMER".equals(u.getRole())).count();
        long totalOrders      = orderService.getAllOrders().size();
        long totalRestaurants = restaurantService.getAllRestaurants().size();
        long pendingOrders    = orderService.filterByStatus("PENDING").size();
        long deliveredOrders  = orderService.filterByStatus("DELIVERED").size();
        long totalCoupons     = couponRepository.findAll().size();

        model.addAttribute("totalUsers",       totalUsers);
        model.addAttribute("totalOrders",      totalOrders);
        model.addAttribute("totalRestaurants", totalRestaurants);
        model.addAttribute("pendingOrders",    pendingOrders);
        model.addAttribute("deliveredOrders",  deliveredOrders);
        model.addAttribute("totalCoupons",     totalCoupons);
        model.addAttribute("recentOrders",     orderService.getAllOrders().stream().limit(5).toList());
        return "admin-dashboard";
    }

    // ── Restaurants ─────────────────────────────────────────

    @GetMapping("/restaurants")
    public String adminRestaurants(Model model) {
        model.addAttribute("restaurants",   restaurantService.getAllRestaurants());
        model.addAttribute("newRestaurant", new Restaurant());
        return "admin-restaurants";
    }

    @PostMapping("/restaurants/add")
    public String addRestaurant(@RequestParam String name,
                                @RequestParam String cuisine,
                                @RequestParam(defaultValue = "0.0") double rating,
                                @RequestParam(required = false) String imageUrl,
                                @RequestParam(defaultValue = "false") boolean open) {
        Restaurant r = new Restaurant();
        r.setName(name);
        r.setCuisine(cuisine);
        r.setRating(rating);
        r.setImageUrl(imageUrl);
        r.setOpen(open);
        restaurantService.save(r);
        return "redirect:/admin/restaurants?success=added";
    }

    @GetMapping("/restaurants/edit/{id}")
    public String editRestaurantForm(@PathVariable Long id, Model model) {
        model.addAttribute("restaurant", restaurantService.getById(id));
        return "admin-restaurant-edit";
    }

    @PostMapping("/restaurants/edit/{id}")
    public String editRestaurant(@PathVariable Long id,
                                 @RequestParam String name,
                                 @RequestParam String cuisine,
                                 @RequestParam(defaultValue = "0.0") double rating,
                                 @RequestParam(required = false) String imageUrl,
                                 @RequestParam(defaultValue = "false") boolean open) {
        Restaurant r = restaurantService.getById(id);
        r.setName(name);
        r.setCuisine(cuisine);
        r.setRating(rating);
        r.setImageUrl(imageUrl);
        r.setOpen(open);
        restaurantService.save(r);
        return "redirect:/admin/restaurants?success=updated";
    }

    @PostMapping("/restaurants/delete/{id}")
    public String deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
        return "redirect:/admin/restaurants?success=deleted";
    }

    // ── Menu Items ─────────────────────────────────────────

    @GetMapping("/menu-items")
    public String adminMenuItems(Model model) {
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        model.addAttribute("menuItems",   restaurantService.getAllMenuItems());
        return "admin-menu-items";
    }

    @PostMapping("/menu-items/add")
    public String addMenuItem(@RequestParam Long restaurantId,
                              @RequestParam String name,
                              @RequestParam String category,
                              @RequestParam double price) {
        restaurantService.addMenuItem(restaurantId, name, category, price, true);
        return "redirect:/admin/menu-items?success=added";
    }

    @GetMapping("/menu-items/edit/{id}")
    public String editMenuItemForm(@PathVariable Long id, Model model) {
        model.addAttribute("item",        restaurantService.getMenuItemById(id));
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        return "admin-menuitem-edit";
    }

    @PostMapping("/menu-items/edit/{id}")
    public String editMenuItem(@PathVariable Long id,
                               @RequestParam String name,
                               @RequestParam String category,
                               @RequestParam double price,
                               @RequestParam(defaultValue = "false") boolean available) {
        restaurantService.updateMenuItem(id, name, category, price, available);
        return "redirect:/admin/menu-items?success=updated";
    }

    @PostMapping("/menu-items/delete/{id}")
    public String deleteMenuItem(@PathVariable Long id) {
        restaurantService.deleteMenuItem(id);
        return "redirect:/admin/menu-items?success=deleted";
    }

    @PostMapping("/menu-items/toggle/{id}")
    public String toggleMenuItem(@PathVariable Long id) {
        restaurantService.toggleMenuItemAvailability(id);
        return "redirect:/admin/menu-items";
    }

    // ── Orders ─────────────────────────────────────────────

    @GetMapping("/orders")
    public String adminOrders(@RequestParam(required = false) String status, Model model) {
        model.addAttribute("orders",         orderService.filterByStatus(status));
        model.addAttribute("selectedStatus", status);
        return "admin-orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        orderService.updateStatus(id, status);
        return "redirect:/admin/orders?success=updated";
    }

    // ── Users ──────────────────────────────────────────────

    @GetMapping("/users")
    public String adminUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin-users";
    }

    @PostMapping("/users/add")
    public String addAdminUser(@RequestParam String name,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String phone) {
        if (userRepository.findByEmail(email).isPresent()) {
            return "redirect:/admin/users?error=exists";
        }
        Admin admin = new Admin();
        admin.setName(name);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setPhone(phone);
        admin.setRole("ROLE_ADMIN");
        userRepository.save(admin);
        return "redirect:/admin/users?success=added";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users?success=deleted";
    }

    // ── Coupons ────────────────────────────────────────────

    @GetMapping("/coupons")
    public String adminCoupons(Model model) {
        model.addAttribute("coupons", couponRepository.findAll());
        return "admin-coupons";
    }

    @PostMapping("/coupons/add")
    public String addCoupon(@RequestParam String code,
                            @RequestParam double discountPercent,
                            @RequestParam double minOrderAmount,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate) {
        if (couponRepository.findByCodeIgnoreCase(code).isPresent()) {
            return "redirect:/admin/coupons?error=exists";
        }
        Coupon c = new Coupon();
        c.setCode(code.toUpperCase());
        c.setDiscountPercent(discountPercent);
        c.setMinOrderAmount(minOrderAmount);
        c.setExpiryDate(expiryDate);
        c.setActive(true);
        couponRepository.save(c);
        return "redirect:/admin/coupons?success=added";
    }

    @GetMapping("/coupons/edit/{id}")
    public String editCouponForm(@PathVariable Long id, Model model) {
        model.addAttribute("coupon", couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found")));
        return "admin-coupon-edit";
    }

    @PostMapping("/coupons/edit/{id}")
    public String editCoupon(@PathVariable Long id,
                             @RequestParam double discountPercent,
                             @RequestParam double minOrderAmount,
                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate,
                             @RequestParam(defaultValue = "false") boolean active) {
        couponRepository.findById(id).ifPresent(c -> {
            c.setDiscountPercent(discountPercent);
            c.setMinOrderAmount(minOrderAmount);
            c.setExpiryDate(expiryDate);
            c.setActive(active);
            couponRepository.save(c);
        });
        return "redirect:/admin/coupons?success=updated";
    }

    @PostMapping("/coupons/toggle/{id}")
    public String toggleCoupon(@PathVariable Long id) {
        couponRepository.findById(id).ifPresent(c -> {
            c.setActive(!c.isActive());
            couponRepository.save(c);
        });
        return "redirect:/admin/coupons";
    }

    @PostMapping("/coupons/delete/{id}")
    public String deleteCoupon(@PathVariable Long id) {
        couponRepository.deleteById(id);
        return "redirect:/admin/coupons?success=deleted";
    }

    // ── Drivers ────────────────────────────────────────────

    @GetMapping("/drivers")
    public String adminDrivers(Model model) {
        model.addAttribute("drivers", driverService.getAllDrivers());
        model.addAttribute("vehicleTypes", VehicleType.values());
        return "admin-drivers";
    }

    @PostMapping("/drivers/add")
    public String addDriver(@RequestParam String name,
                            @RequestParam String email,
                            @RequestParam String password,
                            @RequestParam String phone,
                            @RequestParam(required = false) String licenseNumber,
                            @RequestParam VehicleType vehicleType) {
        if (userRepository.findByEmail(email).isPresent()) {
            return "redirect:/admin/drivers?error=exists";
        }
        Driver d = new Driver();
        d.setName(name);
        d.setEmail(email);
        d.setPassword(passwordEncoder.encode(password));
        d.setPhone(phone);
        d.setRole("ROLE_DRIVER");
        d.setLicenseNumber(licenseNumber);
        d.setVehicleType(vehicleType);
        d.setAvailable(true);
        d.setCurrentRating(5.0f);
        driverService.save(d);
        return "redirect:/admin/drivers?success=added";
    }

    @PostMapping("/drivers/toggle/{id}")
    public String toggleDriver(@PathVariable Long id) {
        driverService.toggleAvailability(id);
        return "redirect:/admin/drivers";
    }

    @PostMapping("/drivers/delete/{id}")
    public String deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return "redirect:/admin/drivers?success=deleted";
    }
}
