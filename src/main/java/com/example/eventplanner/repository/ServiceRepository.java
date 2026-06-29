package com.example.eventplanner.repository;

import com.example.eventplanner.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByPlanner_Id(Long plannerId);
}
