package com.team11.foodorder.repository;

import com.team11.foodorder.entity.DeliveryOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryOtpRepository extends JpaRepository<DeliveryOtp, Long> {

    Optional<DeliveryOtp> findByOrderId(Long orderId);

    void deleteByOrderId(Long orderId);
}
