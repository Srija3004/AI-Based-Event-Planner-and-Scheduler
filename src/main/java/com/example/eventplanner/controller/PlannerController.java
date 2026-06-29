package com.example.eventplanner.controller;

import com.example.eventplanner.dto.BudgetResponse;
import com.example.eventplanner.model.Event;
import com.example.eventplanner.model.Payment;
import com.example.eventplanner.repository.EventRepository;
import com.example.eventplanner.repository.PaymentRepository;
import com.example.eventplanner.repository.PlannerRepository;
import com.example.eventplanner.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.eventplanner.model.Planner;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/planner")
@CrossOrigin(origins = {"http://localhost:63342", "http://127.0.0.1:5500"})
public class PlannerController {

    @Autowired
    private PlannerRepository plannerRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired(required = false)
    private ExpenseRepository expenseRepository; // optional until expenses exist

    // Load all planners for USER browse
    @GetMapping("/all")
    public List<?> getAllPlanners() {
        return plannerRepository.findAll();
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getPlannerByUserId(@PathVariable Long userId) {
        return plannerRepository.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profile/{userId}")
    public ResponseEntity<?> updatePlannerProfile(
            @PathVariable Long userId, @RequestBody Planner updatedPlanner) {

        return plannerRepository.findByUserId(userId).map(planner -> {
            planner.setName(updatedPlanner.getName());
            planner.setEmail(updatedPlanner.getEmail());
            planner.setPhone(updatedPlanner.getPhone());
            planner.setLocation(updatedPlanner.getLocation());
            planner.setAbout(updatedPlanner.getAbout());
            planner.setExperienceYears(updatedPlanner.getExperienceYears());
            planner.setSpecialties(updatedPlanner.getSpecialties());
            plannerRepository.save(planner);
            return ResponseEntity.ok(planner);
        }).orElse(ResponseEntity.notFound().build());
    }

    // Earnings API
    @GetMapping("/earnings/{plannerId}")
    public ResponseEntity<?> getPlannerEarnings(@PathVariable Long plannerId) {

        List<Payment> payments = paymentRepository.findByPlanner_IdAndStatus(plannerId, "PAID");

        double total = payments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();

        LocalDate now = LocalDate.now();
        double thisMonth = payments.stream()
                .filter(p -> p.getPaymentDate().toLocalDate().getMonth() == now.getMonth()
                        && p.getPaymentDate().toLocalDate().getYear() == now.getYear())
                .mapToDouble(Payment::getAmount)
                .sum();

        Map<String, Object> response = new HashMap<>();
        response.put("totalEarnings", total);
        response.put("monthlyEarnings", thisMonth);
        response.put("pendingAmount", 0);
        response.put("transactions", payments);

        return ResponseEntity.ok(response);
    }

    // ------------------ NEW: Budget API ------------------
    @GetMapping("/budget/{userId}")
    public ResponseEntity<?> getBudget(@PathVariable Long userId) {

        // Only confirmed events should count
        List<Event> confirmedEvents = eventRepository.findByUserId(userId)
                .stream()
                .filter(event -> "confirmed".equalsIgnoreCase(event.getStatus()))
                .toList();

        double totalBudget = confirmedEvents.stream()
                .mapToDouble(e -> e.getBudget() != null ? e.getBudget() : 0)
                .sum();

        List<?> expenses = expenseRepository != null
                ? expenseRepository.findByEvent_User_Id(userId)
                : List.of();

        double spent = expenseRepository != null
                ? expenseRepository.findByEvent_User_Id(userId)
                .stream()
                .mapToDouble(e -> e.getAmount() != null ? e.getAmount() : 0)
                .sum()
                : 0;

        double remaining = totalBudget - spent;

        return ResponseEntity.ok(
                new BudgetResponse(totalBudget, spent, remaining, expenses)
        );
    }
}
