package com.team11.foodorder.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("Driver")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Driver extends AppUser {

    private String licenseNumber;

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType = VehicleType.BIKE;

    private boolean isAvailable = true;

    private float currentRating = 5.0f;

    // current assigned order (null if idle)
    private Long currentOrderId;
}
