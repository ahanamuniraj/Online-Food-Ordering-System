package com.team11.foodorder.config;

import com.team11.foodorder.entity.*;
import com.team11.foodorder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DataInitializer — runs at startup and creates default users.
 * Uses BCryptPasswordEncoder so passwords always match.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        // Admin
        Admin admin = new Admin();
        admin.setName("Admin User");
        admin.setEmail("admin@demo.com");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setPhone("9000000000");
        admin.setRole("ROLE_ADMIN");
        userRepository.save(admin);

        // Customers
        Customer alice = new Customer();
        alice.setName("Alice Kumar");
        alice.setEmail("alice@demo.com");
        alice.setPassword(passwordEncoder.encode("pass123"));
        alice.setPhone("9876543210");
        alice.setRole("ROLE_CUSTOMER");
        userRepository.save(alice);

        Customer bob = new Customer();
        bob.setName("Bob Sharma");
        bob.setEmail("bob@demo.com");
        bob.setPassword(passwordEncoder.encode("pass123"));
        bob.setPhone("9123456789");
        bob.setRole("ROLE_CUSTOMER");
        userRepository.save(bob);

        // Drivers
        Driver ravi = new Driver();
        ravi.setName("Ravi Driver");
        ravi.setEmail("ravi@demo.com");
        ravi.setPassword(passwordEncoder.encode("pass123"));
        ravi.setPhone("9111111111");
        ravi.setRole("ROLE_DRIVER");
        ravi.setLicenseNumber("KA01AB1234");
        ravi.setVehicleType(VehicleType.BIKE);
        ravi.setAvailable(true);
        ravi.setCurrentRating(4.8f);
        userRepository.save(ravi);

        Driver priya = new Driver();
        priya.setName("Priya Rider");
        priya.setEmail("priya@demo.com");
        priya.setPassword(passwordEncoder.encode("pass123"));
        priya.setPhone("9222222222");
        priya.setRole("ROLE_DRIVER");
        priya.setLicenseNumber("KA02CD5678");
        priya.setVehicleType(VehicleType.CAR);
        priya.setAvailable(true);
        priya.setCurrentRating(4.5f);
        userRepository.save(priya);

        System.out.println("✅ DataInitializer: default users created.");
    }
}
