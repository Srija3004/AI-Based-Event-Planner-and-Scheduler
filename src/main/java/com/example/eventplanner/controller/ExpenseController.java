package com.example.eventplanner.controller;

import com.example.eventplanner.model.Expense;
import com.example.eventplanner.repository.ExpenseRepository;
import com.example.eventplanner.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = {"http://localhost:63342","http://127.0.0.1:5500"})
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private EventRepository eventRepository;

    @PostMapping("/add")
    public ResponseEntity<?> addExpense(@RequestBody Expense expense) {
        if (expense.getEvent() == null || expense.getEvent().getId() == null) {
            return ResponseEntity.badRequest().body("Event ID required");
        }
        return ResponseEntity.ok(expenseRepository.save(expense));
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateExpense(@PathVariable Long id, @RequestBody Expense updatedExpense) {
        return expenseRepository.findById(id).map(existing -> {
            existing.setCategory(updatedExpense.getCategory());
            existing.setDescription(updatedExpense.getDescription());
            existing.setAmount(updatedExpense.getAmount());
            existing.setStatus(updatedExpense.getStatus());
            return ResponseEntity.ok(expenseRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id) {
        if (!expenseRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        expenseRepository.deleteById(id);
        return ResponseEntity.ok("Deleted successfully");
    }

}
