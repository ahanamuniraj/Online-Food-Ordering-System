package com.team11.foodorder.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("Admin")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Admin extends AppUser {
}
