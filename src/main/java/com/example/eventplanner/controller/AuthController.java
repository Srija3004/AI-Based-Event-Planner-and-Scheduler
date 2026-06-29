package com.example.eventplanner.controller;

import com.example.eventplanner.model.Planner;
import com.example.eventplanner.repository.PlannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.example.eventplanner.model.User;
import com.example.eventplanner.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {
        "http://localhost:63342",
        "http://localhost:5500",
        "http://127.0.0.1:5500"
})
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PlannerRepository plannerRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String,String> payload) {
        User user = new User();
        user.setName(payload.get("name"));
        user.setEmail(payload.get("email"));
        user.setPassword(payload.get("password"));
        user.setRole(payload.get("role").toUpperCase());
        return ResponseEntity.ok(userService.signup(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> payload) {

        String email = payload.get("email");
        String password = payload.get("password");

        User user = userService.login(email, password);

        Long plannerId = null;
        if ("PLANNER".equalsIgnoreCase(user.getRole())) {
            plannerId = plannerRepository.findByUserId(user.getId())
                    .map(Planner::getId)
                    .orElse(null);
        }

        // FIX: use HashMap instead of Map.of()
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        response.put("token", "dummy-token");
        response.put("plannerId", plannerId);  // now safe to be null

        return ResponseEntity.ok(response);
    }
}
