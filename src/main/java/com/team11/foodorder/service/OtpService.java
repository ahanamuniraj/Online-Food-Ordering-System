package com.team11.foodorder.service;

import com.team11.foodorder.entity.DeliveryOtp;
import com.team11.foodorder.entity.FoodOrder;
import com.team11.foodorder.entity.Tracking;
import com.team11.foodorder.repository.DeliveryOtpRepository;
import com.team11.foodorder.repository.OrderRepository;
import com.team11.foodorder.repository.TrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpService {

    private static final int OTP_VALIDITY_MINUTES = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final DeliveryOtpRepository otpRepository;
    private final OrderRepository orderRepository;
    private final TrackingRepository trackingRepository;

    @Transactional
    public DeliveryOtp generateOtp(Long orderId) {
        FoodOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (!isOtpAllowedForStatus(order.getStatus())) {
            throw new IllegalStateException(
                "OTP can only be generated for CONFIRMED or PREPARING orders. " +
                "Current status: " + order.getStatus()
            );
        }

        otpRepository.deleteByOrderId(orderId);

        String code = generateSixDigitCode();
        LocalDateTime now = LocalDateTime.now();

        DeliveryOtp otp = DeliveryOtp.builder()
                .orderId(orderId)
                .otpCode(code)
                .generatedAt(now)
                .expiresAt(now.plusMinutes(OTP_VALIDITY_MINUTES))
                .used(false)
                .build();

        DeliveryOtp saved = otpRepository.save(otp);

        System.out.printf("OTP generated for order #%d  ->  %s  (expires: %s)%n",
                orderId, code, saved.getExpiresAt());

        return saved;
    }

    @Transactional
    public OtpVerificationResult verifyOtp(Long orderId, String submittedCode) {

        FoodOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if ("OUT_FOR_DELIVERY".equals(order.getStatus()) || "DELIVERED".equals(order.getStatus())) {
            return OtpVerificationResult.failure("Delivery has already started for this order.");
        }

        DeliveryOtp stored = otpRepository.findByOrderId(orderId).orElse(null);

        if (stored == null) {
            return OtpVerificationResult.failure("No OTP has been generated for this order yet. Please ask the admin.");
        }

        if (stored.isUsed()) {
            return OtpVerificationResult.failure("This OTP has already been used.");
        }

        if (LocalDateTime.now().isAfter(stored.getExpiresAt())) {
            return OtpVerificationResult.failure("OTP has expired. Please ask the admin to regenerate.");
        }

        if (!stored.getOtpCode().equals(submittedCode)) {
            return OtpVerificationResult.failure("Incorrect OTP. Please try again.");
        }

        stored.setUsed(true);
        otpRepository.save(stored);

        order.setStatus("OUT_FOR_DELIVERY");
        orderRepository.save(order);

        insertInitialTrackingSteps(orderId);

        System.out.printf("OTP verified for order #%d - delivery started%n", orderId);
        return OtpVerificationResult.verified();
    }

    public DeliveryOtp getActiveOtp(Long orderId) {
        return otpRepository.findByOrderId(orderId)
                .filter(DeliveryOtp::isValid)
                .orElse(null);
    }

    private String generateSixDigitCode() {
        int code = 100_000 + RANDOM.nextInt(900_000);
        return String.valueOf(code);
    }

    private boolean isOtpAllowedForStatus(String status) {
        return "CONFIRMED".equals(status) || "PREPARING".equals(status);
    }

    private void insertInitialTrackingSteps(Long orderId) {
        addTracking(orderId, "OUT_FOR_DELIVERY", "Picked up from restaurant - on the way!");
    }

    private void addTracking(Long orderId, String status, String location) {
        Tracking t = new Tracking();
        t.setOrderId(orderId);
        t.setStatus(status);
        t.setLocation(location);
        t.setUpdatedTime(LocalDateTime.now());
        trackingRepository.save(t);
    }

    // Fix: record accessor methods must be public
    public record OtpVerificationResult(boolean success, String message) {
        public static OtpVerificationResult verified() {
            return new OtpVerificationResult(true, "OTP verified. Delivery has started!");
        }
        public static OtpVerificationResult failure(String reason) {
            return new OtpVerificationResult(false, reason);
        }
    }
}
