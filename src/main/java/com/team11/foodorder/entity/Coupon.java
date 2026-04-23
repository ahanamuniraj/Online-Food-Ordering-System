package com.team11.foodorder.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    private double discountPercent;   // e.g. 10 = 10%
    private double minOrderAmount;
    private LocalDate expiryDate;
    private boolean active;
}
