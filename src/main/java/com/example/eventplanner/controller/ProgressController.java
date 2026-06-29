package com.example.eventplanner.controller;

import com.example.eventplanner.model.Task;
import com.example.eventplanner.repository.TaskRepository;
import com.example.eventplanner.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planner/progress")
@CrossOrigin(origins = {"http://localhost:63342","http://127.0.0.1:5500"})
public class ProgressController {

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/{eventId}")
    public List<Task> getTasks(@PathVariable Long eventId) {
        return taskRepository.findByEvent_Id(eventId);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addTask(@RequestBody Task task) {
        return ResponseEntity.ok(taskRepository.save(task));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return taskRepository.findById(id).map(task -> {
            task.setStatus(status);
            return ResponseEntity.ok(taskRepository.save(task));
        }).orElse(ResponseEntity.notFound().build());
    }
    @Autowired
    private PaymentService paymentService;

    @GetMapping("/generate/{eventId}")
    public ResponseEntity<?> generateTasks(@PathVariable Long eventId) {
        paymentService.generateDefaultTasks(eventId);
        return ResponseEntity.ok("Tasks successfully generated!");
    }

}
