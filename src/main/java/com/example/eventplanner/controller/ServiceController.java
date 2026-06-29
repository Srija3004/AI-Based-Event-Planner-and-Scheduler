package com.example.eventplanner.controller;

import com.example.eventplanner.model.Planner;
import com.example.eventplanner.model.Service;
import com.example.eventplanner.repository.PlannerRepository;
import com.example.eventplanner.repository.ServiceRepository;
import com.example.eventplanner.service.PlannerServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = {"http://localhost:63342", "http://127.0.0.1:5500"})
public class ServiceController {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private PlannerRepository plannerRepository;
    @Autowired
    private PlannerServiceManager manager;

    @PostMapping("/add/{plannerId}")
    public ResponseEntity<?> addService(@PathVariable Long plannerId, @RequestBody Service service) {

        Planner planner = plannerRepository.findById(plannerId).orElse(null);
        if (planner == null)
            return ResponseEntity.badRequest().body("Planner not found");

        service.setPlanner(planner);
        return ResponseEntity.ok(serviceRepository.save(service));
    }

    @GetMapping("/{plannerId}")
    public List<Service> getPlannerServices(@PathVariable Long plannerId) {
        return serviceRepository.findByPlanner_Id(plannerId);
    }

    @PutMapping("/update/{serviceId}")
    public ResponseEntity<?> updateService(
            @PathVariable Long serviceId,
            @RequestBody Service updated
    ) {
        return serviceRepository.findById(serviceId).map(existing -> {
            existing.setName(updated.getName());
            existing.setCategory(updated.getCategory());
            existing.setPrice(updated.getPrice());
            existing.setDescription(updated.getDescription());
            existing.setAvailability(updated.getAvailability());
            return ResponseEntity.ok(serviceRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{serviceId}")
    public ResponseEntity<?> deleteService(@PathVariable Long serviceId) {
        if (!serviceRepository.existsById(serviceId))
            return ResponseEntity.notFound().build();

        serviceRepository.deleteById(serviceId);
        return ResponseEntity.ok("Service Deleted");
    }
}
