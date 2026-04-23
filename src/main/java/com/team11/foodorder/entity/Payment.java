package com.team11.foodorder.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private String paymentMethod; // UPI, CARD, COD
    private String status;        // SUCCESS, FAILED, PENDING
    private double amount;
    private LocalDateTime paymentTime;
}
