package com.team11.foodorder.repository;
import com.team11.foodorder.entity.Tracking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TrackingRepository extends JpaRepository<Tracking, Long> {
    List<Tracking> findByOrderIdOrderByUpdatedTimeAsc(Long orderId);
}
