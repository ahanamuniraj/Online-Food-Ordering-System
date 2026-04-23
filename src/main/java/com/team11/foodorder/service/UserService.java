package com.team11.foodorder.service;

import com.team11.foodorder.entity.AppUser;
import com.team11.foodorder.entity.Customer;
import com.team11.foodorder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * USER SERVICE — Aarna M (PES2UG23CS009)
 * Single Responsibility: handles only user/auth business logic.
 * Uses BCrypt password encoding for secure storage.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<AppUser> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public AppUser findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    /**
     * Register a new customer with BCrypt-encoded password.
     * Throws if email already exists.
     */
    public Customer register(String name, String email, String password, String phone) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered.");
        }
        Customer c = new Customer();
        c.setName(name);
        c.setEmail(email);
        c.setPassword(passwordEncoder.encode(password));
        c.setPhone(phone);
        c.setRole("ROLE_CUSTOMER");
        return (Customer) userRepository.save(c);
    }

    /** Update name and phone for an existing user. */
    public void updateProfile(Long userId, String name, String phone) {
        AppUser user = findById(userId);
        user.setName(name);
        user.setPhone(phone);
        userRepository.save(user);
    }
}
