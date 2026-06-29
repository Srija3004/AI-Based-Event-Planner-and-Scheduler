package com.example.eventplanner.service;

import com.example.eventplanner.model.Availability;
import com.example.eventplanner.repository.AvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvailabilityService {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    public List<Availability> getByPlanner(Long plannerId) {
        return availabilityRepository.findByPlanner_Id(plannerId);
    }

    public Availability save(Availability availability) {
        return availabilityRepository.save(availability);
    }

    public void delete(Long id) {
        availabilityRepository.deleteById(id);
    }
}
