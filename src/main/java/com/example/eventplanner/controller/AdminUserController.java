package com.example.eventplanner.controller;

import com.example.eventplanner.model.User;
import com.example.eventplanner.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    // ✔ Load all users
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // ✔ Delete user
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("User does not exist");
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted");
    }

    // ✔ Block / Unblock user (toggle status)
    @PutMapping("/users/{id}/toggle-status")
    public ResponseEntity<String> toggleUserStatus(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        user.setActive(!user.isActive());  // toggle the value
        userRepository.save(user);

        String status = user.isActive() ? "unblocked" : "blocked";
        return ResponseEntity.ok("User " + status);
    }
}
