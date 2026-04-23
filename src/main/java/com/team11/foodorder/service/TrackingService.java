package com.team11.foodorder.service;

import com.team11.foodorder.entity.Tracking;
import com.team11.foodorder.repository.TrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackingService {
    private final TrackingRepository trackingRepository;

    public List<Tracking> getByOrderId(Long orderId) {
        return trackingRepository.findByOrderIdOrderByUpdatedTimeAsc(orderId);
    }

    // 🔥 ADDED METHOD
    public List<Tracking> getTrackingForOrder(Long orderId) {
        return getByOrderId(orderId);
    }
}