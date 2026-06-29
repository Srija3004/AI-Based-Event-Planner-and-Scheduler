package com.example.eventplanner.controller;

import com.example.eventplanner.model.ConnectionRequest;
import com.example.eventplanner.model.User;
import com.example.eventplanner.repository.ConnectionRequestRepository;
import com.example.eventplanner.repository.UserRepository;
import com.example.eventplanner.repository.PlannerRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/connect")
@CrossOrigin(origins = "http://localhost:63342")
public class ConnectionController {

    @Autowired
    private ConnectionRequestRepository connectionRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlannerRepository plannerRepository;

    // ---------- 1. USER: send request ----------
    @PostMapping("/request")
    public ResponseEntity<?> createRequest(@RequestBody ConnectionRequestDTO dto) {

        // 1️⃣ Basic validation
        if (dto.getUserId() == null || dto.getPlannerId() == null) {
            return ResponseEntity.badRequest().body("userId and plannerId are required");
        }

        // 2️⃣ Make sure user & planner actually exist
        if (!userRepository.existsById(dto.getUserId())) {
            return ResponseEntity.badRequest().body("Invalid userId");
        }
        if (!plannerRepository.existsById(dto.getPlannerId())) {
            return ResponseEntity.badRequest().body("Invalid plannerId");
        }

        // 3️⃣ 🔥 Check if a pending request already exists
        var existing = connectionRequestRepository
                .findByUserIdAndPlannerIdAndStatus(dto.getUserId(), dto.getPlannerId(), "PENDING");

        if (existing.isPresent()) {
            // Don't create new row – just tell frontend it's already sent
            return ResponseEntity.badRequest().body("Request already sent");
        }

        // 4️⃣ Create fresh request
        ConnectionRequest req = new ConnectionRequest();
        req.setUserId(dto.getUserId());
        req.setPlannerId(dto.getPlannerId());
        req.setStatus("PENDING");
        // requestDate is auto-set in entity: LocalDateTime.now()

        connectionRequestRepository.save(req);

        return ResponseEntity.ok("Request sent");
    }

    // ---------- 2. PLANNER: view pending requests ----------
    @GetMapping("/requests/{plannerId}")
    public ResponseEntity<List<ClientRequestView>> getRequests(@PathVariable Long plannerId) {
        List<ConnectionRequest> list = connectionRequestRepository.findByPlannerIdAndStatus(plannerId, "PENDING");
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        List<ClientRequestView> result = list.stream().map(r -> {
            User user = userRepository.findById(r.getUserId()).orElse(null);

            ClientRequestView v = new ClientRequestView();
            v.setId(r.getId());
            v.setUserName(user != null ? user.getName() : "Unknown");
            v.setUserEmail(user != null ? user.getEmail() : "Unknown");
            v.setStatus(r.getStatus());
            v.setRequestDate(r.getRequestDate() != null ? r.getRequestDate().format(format) : "");
            return v;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // ---------- 3. PLANNER: accept request ----------
    @PostMapping("/accept/{requestId}")
    public ResponseEntity<?> accept(@PathVariable Long requestId) {

        ConnectionRequest req = connectionRequestRepository.findById(requestId).orElse(null);
        if (req == null) return ResponseEntity.notFound().build();

        // prevent multiple accepts
        var existingAccepted = connectionRequestRepository
                .findByUserIdAndPlannerIdAndStatus(req.getUserId(), req.getPlannerId(), "ACCEPTED");

        if (existingAccepted.isPresent()) {
            return ResponseEntity.badRequest().body("Client already accepted");
        }

        req.setStatus("ACCEPTED");
        connectionRequestRepository.save(req);
        return ResponseEntity.ok("Request accepted");
    }

    // ---------- 4. PLANNER: reject request ----------
    @PostMapping("/reject/{requestId}")
    public ResponseEntity<?> reject(@PathVariable Long requestId) {
        return connectionRequestRepository.findById(requestId)
                .map(req -> {
                    req.setStatus("REJECTED");
                    connectionRequestRepository.save(req);
                    return ResponseEntity.ok("Request rejected");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ---------- 5. Accepted clients (for communication page) ----------
    @GetMapping("/clients/{plannerId}")
    public ResponseEntity<List<ClientSummary>> getAcceptedClients(@PathVariable Long plannerId) {
        List<ConnectionRequest> list = connectionRequestRepository.findByPlannerIdAndStatus(plannerId, "ACCEPTED");

        List<ClientSummary> out = list.stream().map(r -> {
            User user = userRepository.findById(r.getUserId()).orElse(null);

            ClientSummary c = new ClientSummary();
            c.setConnectionId(r.getId());
            c.setUserId(r.getUserId());
            c.setUserName(user != null ? user.getName() : "Unknown");
            return c;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(out);
    }
    // ---------- 6. USER SIDE: fetch planners connected with user ----------
    @GetMapping("/user-clients/{userId}")
    public ResponseEntity<List<ClientSummary>> getPlannersForUser(@PathVariable Long userId) {

        List<ConnectionRequest> list =
                connectionRequestRepository.findByUserIdAndStatus(userId, "ACCEPTED");

        List<ClientSummary> result = list.stream().map(r -> {

            // Get planner from planner table
            var planner = plannerRepository.findById(r.getPlannerId()).orElse(null);

            // Use planner.userId to fetch actual person name
            var plannerAccount = (planner != null)
                    ? userRepository.findById(planner.getUserId()).orElse(null)
                    : null;

            ClientSummary c = new ClientSummary();
            c.setConnectionId(r.getId());
            c.setUserId(r.getPlannerId());  // correct
            c.setUserName(plannerAccount != null ? plannerAccount.getName() : "Unknown");
            return c;

        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }



    // ---------- DTO Classes ----------
    @Data
    public static class ConnectionRequestDTO {
        private Long userId;
        private Long plannerId;
    }

    @Data
    public static class ClientRequestView {
        private Long id;
        private String userName;
        private String userEmail;
        private String status;
        private String requestDate;
    }

    @Data
    public static class ClientSummary {
        private Long connectionId;
        private Long userId;
        private String userName;
    }
}
