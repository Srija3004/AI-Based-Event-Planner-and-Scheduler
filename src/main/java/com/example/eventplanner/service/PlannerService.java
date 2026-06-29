package com.example.eventplanner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.eventplanner.model.Planner;
import com.example.eventplanner.repository.PlannerRepository;
import java.util.List;

@Service
public class PlannerService {

    @Autowired
    private PlannerRepository plannerRepository;

    public List<Planner> getAllPlanners() {
        return plannerRepository.findAll();
    }

    public Planner getPlannerByUserId(Long userId) {
        return plannerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Planner not found"));
    }
}
