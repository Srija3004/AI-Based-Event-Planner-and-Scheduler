package com.example.eventplanner.repository;

import com.example.eventplanner.model.ConnectionRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConnectionRequestRepository extends JpaRepository<ConnectionRequest, Long> {
    List<ConnectionRequest> findByPlannerId(Long plannerId);

    List<ConnectionRequest> findByPlannerIdAndStatus(Long plannerId, String status);

    // 🔥 NEW: used to prevent duplicate pending requests
    Optional<ConnectionRequest> findByUserIdAndPlannerIdAndStatus(Long userId,
                                                                  Long plannerId,
                                                                  String status);

    List<ConnectionRequest> findByUserIdAndStatus(Long userId, String status);

}
