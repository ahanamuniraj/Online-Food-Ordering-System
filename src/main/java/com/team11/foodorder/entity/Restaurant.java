package com.team11.foodorder.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String cuisine;
    private double rating;
    private boolean open;
    private String imageUrl;

    @OneToOne(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Menu menu;
}
