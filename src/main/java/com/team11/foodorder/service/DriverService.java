package com.team11.foodorder.service;

import com.team11.foodorder.entity.Driver;
import com.team11.foodorder.entity.FoodOrder;
import com.team11.foodorder.entity.Tracking;
import com.team11.foodorder.repository.DriverRepository;
import com.team11.foodorder.repository.OrderRepository;
import com.team11.foodorder.repository.TrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DRIVER SERVICE
 * Handles driver-specific operations: accepting orders, updating location,
 * marking deliveries complete, and driver management by admin.
 */
@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final OrderRepository orderRepository;
    private final TrackingRepository trackingRepository;
    private final OrderService orderService;

    // ── Admin: get all drivers ─────────────────────────────────────────────

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Driver getDriverById(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found: " + id));
    }

    public Driver save(Driver driver) {
        return driverRepository.save(driver);
    }

    public void deleteDriver(Long id) {
        driverRepository.deleteById(id);
    }

    public void toggleAvailability(Long driverId) {
        Driver d = getDriverById(driverId);
        d.setAvailable(!d.isAvailable());
        driverRepository.save(d);
    }

    // ── Driver: get their current + available orders ───────────────────────

    public List<FoodOrder> getAvailableOrders() {
        return orderRepository.findByStatus("CONFIRMED");
    }

    public FoodOrder getAssignedOrder(Driver driver) {
        if (driver.getCurrentOrderId() == null) return null;
        return orderRepository.findById(driver.getCurrentOrderId()).orElse(null);
    }

    // ── Driver: accept an order ────────────────────────────────────────────

    public void acceptOrder(Long driverId, Long orderId) {
        Driver driver = getDriverById(driverId);
        FoodOrder order = orderService.getById(orderId);

        if (driver.getCurrentOrderId() != null) {
            throw new IllegalStateException("You already have an active order.");
        }
        if (!"CONFIRMED".equals(order.getStatus())) {
            throw new IllegalStateException("Order is not available for pickup.");
        }

        driver.setCurrentOrderId(orderId);
        driver.setAvailable(false);
        driverRepository.save(driver);

        orderService.updateStatus(orderId, "OUT_FOR_DELIVERY");
        addTracking(orderId, "OUT_FOR_DELIVERY", "Driver " + driver.getName() + " accepted the order");
    }

    // ── Driver: update location ────────────────────────────────────────────

    public void updateLocation(Long driverId, String location) {
        Driver driver = getDriverById(driverId);
        if (driver.getCurrentOrderId() == null) {
            throw new IllegalStateException("No active order.");
        }
        addTracking(driver.getCurrentOrderId(), "OUT_FOR_DELIVERY", location);
    }

    // ── Driver: mark delivered ─────────────────────────────────────────────

    public void markDelivered(Long driverId) {
        Driver driver = getDriverById(driverId);
        if (driver.getCurrentOrderId() == null) {
            throw new IllegalStateException("No active order to mark delivered.");
        }
        Long orderId = driver.getCurrentOrderId();
        orderService.updateStatus(orderId, "DELIVERED");
        addTracking(orderId, "DELIVERED", "Order delivered successfully");

        driver.setCurrentOrderId(null);
        driver.setAvailable(true);
        driverRepository.save(driver);
    }

    // ── Helper ─────────────────────────────────────────────────────────────

    private void addTracking(Long orderId, String status, String location) {
        Tracking t = Tracking.builder()
                .orderId(orderId)
                .status(status)
                .location(location)
                .updatedTime(LocalDateTime.now())
                .build();
        trackingRepository.save(t);
    }

    public Driver findByEmail(String email) {
        return driverRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Driver not found: " + email));
    }
}
