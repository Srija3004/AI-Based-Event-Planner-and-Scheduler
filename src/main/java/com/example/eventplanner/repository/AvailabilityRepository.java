package com.example.eventplanner.repository;

import com.example.eventplanner.model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByPlanner_Id(Long plannerId);
}
