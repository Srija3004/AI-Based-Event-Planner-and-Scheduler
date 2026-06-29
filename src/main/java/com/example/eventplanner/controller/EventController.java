package com.example.eventplanner.controller;

import com.example.eventplanner.model.Event;
import com.example.eventplanner.model.Planner;
import com.example.eventplanner.model.User;
import com.example.eventplanner.repository.EventRepository;
import com.example.eventplanner.repository.PlannerRepository;
import com.example.eventplanner.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = {
        "http://localhost:63342",  // IntelliJ preview
        "http://127.0.0.1:63342",  // backup for IntelliJ
        "http://localhost:5500",   // optional VSCode live server
        "http://127.0.0.1:5500"
})
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlannerRepository plannerRepository; // ✅ new

    // ✅ Save Event (linked to user + planner)
    @PostMapping("/create")
    public Map<String, Object> createEvent(@RequestBody Map<String, Object> payload) {

        Long userId = Long.valueOf(payload.get("userId").toString());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found " + userId));

        // ✅ get plannerId sent from frontend
//        Long plannerId = Long.valueOf(payload.get("plannerId").toString());
//        Planner planner = plannerRepository.findById(plannerId)
//                .orElseThrow(() -> new RuntimeException("Planner not found with ID " + plannerId));
        Long fixedPlannerId = 1L;
        Planner planner = plannerRepository.findById(fixedPlannerId)
                .orElseThrow(() -> new RuntimeException("Planner not found with ID " + fixedPlannerId));

        Event e = new Event();
        e.setName((String) payload.get("name"));
        e.setDescription((String) payload.get("description"));
        e.setCity((String) payload.get("city"));
        e.setVenue((String) payload.get("venue"));
        e.setDate((String) payload.get("date"));
        e.setTime((String) payload.get("time"));
        e.setGuestCount(payload.get("guestCount") != null ? ((Number) payload.get("guestCount")).intValue() : null);
        e.setDuration(payload.get("duration") != null ? ((Number) payload.get("duration")).intValue() : null);
        e.setBudget(payload.get("budget") != null ? ((Number) payload.get("budget")).doubleValue() : null);
        e.setTheme((String) payload.get("theme"));
        e.setRequirements((String) payload.get("requirements"));

        e.setUser(user);        // link USER
        e.setPlanner(planner);  // link PLANNER

        Event saved = eventRepository.save(e);

        Map<String, Object> response = new HashMap<>();
        response.put("event", saved);
        response.put("recommendations", Map.of(
                "venue", "Suggested venue: " + e.getCity() + " Grand Hall",
                "catering", "Recommended catering for " + e.getGuestCount() + " guests",
                "timeline", "Start at " + e.getTime()
        ));

        return response;
    }

    @GetMapping("/planner/{plannerId}")
    public List<Map<String, Object>> getEventsForPlanner(@PathVariable Long plannerId) {
        List<Event> events = eventRepository.findByPlanner_Id(plannerId);

        List<Map<String, Object>> response = new ArrayList<>();
        for (Event e : events) {
            Map<String, Object> eventMap = new HashMap<>();
            eventMap.put("id", e.getId());
            eventMap.put("name", e.getName());
            eventMap.put("date", e.getDate());
            eventMap.put("time", e.getTime());
            eventMap.put("city", e.getCity());
            eventMap.put("venue", e.getVenue());
            eventMap.put("guestCount", e.getGuestCount());
            eventMap.put("budget", e.getBudget());
            eventMap.put("theme", e.getTheme());
            eventMap.put("status", e.getStatus());
            eventMap.put("userName", e.getUser().getName());  // 👈 Sending user name
            response.add(eventMap);
        }
        return response;
    }
    // ACCEPT EVENT
    @PostMapping("/bookings/{eventId}/accept")
    public ResponseEntity<String> acceptBooking(@PathVariable Long eventId) {
        return eventRepository.findById(eventId).map(event -> {
            event.setStatus("confirmed");
            eventRepository.save(event);
            return ResponseEntity.ok("Event booking confirmed");
        }).orElse(ResponseEntity.notFound().build());
    }

    // REJECT EVENT
    @PostMapping("/bookings/{eventId}/reject")
    public ResponseEntity<String> rejectBooking(@PathVariable Long eventId) {
        return eventRepository.findById(eventId).map(event -> {
            event.setStatus("rejected");
            eventRepository.save(event);
            return ResponseEntity.ok("Event booking rejected");
        }).orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Event>> getEventsByUser(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(user -> ResponseEntity.ok(eventRepository.findByUser(user)))
                .orElse(ResponseEntity.notFound().build());
    }
    // CANCEL EVENT (USER)
    @PostMapping("/cancel/{eventId}")
    public ResponseEntity<String> cancelEvent(@PathVariable Long eventId) {
        return eventRepository.findById(eventId).map(event -> {
            if (event.getStatus().equals("confirmed")) {
                return ResponseEntity.badRequest().body("Confirmed events cannot be canceled.");
            }
            event.setStatus("cancelled");
            eventRepository.save(event);
            return ResponseEntity.ok("Event cancelled successfully");
        }).orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/update/{eventId}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long eventId, @RequestBody Event updateData) {
        return eventRepository.findById(eventId).map(event -> {

            event.setName(updateData.getName());
            event.setDate(updateData.getDate());
            event.setTime(updateData.getTime());
            event.setCity(updateData.getCity());
            event.setVenue(updateData.getVenue());
            event.setBudget(updateData.getBudget());
            event.setTheme(updateData.getTheme());

            Event updated = eventRepository.save(event);
            return ResponseEntity.ok(updated);

        }).orElse(ResponseEntity.notFound().build());
    }
}