package com.team11.foodorder.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "food_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private String customerName;
    private String deliveryAddress;
    private String status; // PENDING, CONFIRMED, OUT_FOR_DELIVERY, DELIVERED, CANCELLED

    private double subtotal;
    private double tax;
    private double discount;
    private double total;

    private LocalDateTime placedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> items;
}
