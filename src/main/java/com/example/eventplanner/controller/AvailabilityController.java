package com.example.eventplanner.controller;

import com.example.eventplanner.model.Availability;
import com.example.eventplanner.model.Planner;
import com.example.eventplanner.repository.PlannerRepository;
import com.example.eventplanner.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planner/availability")
@CrossOrigin(origins = {"http://localhost:63342", "http://127.0.0.1:5500"})
public class AvailabilityController {

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private PlannerRepository plannerRepository;

    @GetMapping("/{plannerId}")
    public List<Availability> getAvailability(@PathVariable Long plannerId) {
        return availabilityService.getByPlanner(plannerId);
    }

    @PostMapping("/{plannerId}")
    public Availability addAvailability(@PathVariable Long plannerId, @RequestBody Availability request) {
        Planner planner = plannerRepository.findById(plannerId).orElse(null);
        request.setPlanner(planner);
        return availabilityService.save(request);
    }

    @DeleteMapping("/{id}")
    public String deleteAvailability(@PathVariable Long id) {
        availabilityService.delete(id);
        return "Deleted";
    }
}
