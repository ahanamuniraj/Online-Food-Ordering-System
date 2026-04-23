package com.team11.foodorder.repository;

import com.team11.foodorder.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Customer-specific repository — Aarna M (PES2UG23CS009)
 * Provides typed access to Customer entities.
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
}
