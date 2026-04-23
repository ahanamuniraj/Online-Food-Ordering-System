package com.team11.foodorder.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("Customer")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Customer extends AppUser {
    private String address;
}
