package com.example.eventplanner.repository;

import com.example.eventplanner.model.Planner;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PlannerRepository extends JpaRepository<Planner, Long> {
    Optional<Planner> findByUserId(Long userId);
}
