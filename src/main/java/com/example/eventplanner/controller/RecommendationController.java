package com.example.eventplanner.controller;

import com.example.eventplanner.model.Event;
import com.example.eventplanner.repository.EventRepository;
import com.example.eventplanner.service.AIRecommendationService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class RecommendationController {

    private final EventRepository eventRepository;
    private final AIRecommendationService aiService;

    public RecommendationController(EventRepository eventRepository, AIRecommendationService aiService) {
        this.eventRepository = eventRepository;
        this.aiService = aiService;
    }

    @PostMapping("/recommend/{userId}")
    public Map<String, Object> recommend(
            @PathVariable Long userId,
            @RequestBody(required = false) Map<String, String> request) {

        String userText = request != null ? request.getOrDefault("text", "") : "";

        try {
            String finalPrompt;

            // ⭐ CASE 1: User typed a question → use ONLY that
            if (userText != null && !userText.isBlank()) {
                finalPrompt = """
    You are an AI Event Planner. Provide a detailed, helpful response.

    User Query: %s

    Give the answer in:
    • 5 venue suggestions  
    • Each suggestion must include: location, estimated cost, capacity, and why it matches  
    • 3 alternatives with different budgets  
    • Any additional tips the user should consider  

    Make the response rich, helpful and well-formatted.
    """.formatted(userText);

            } else {

                // ⭐ CASE 2: User typed nothing → use last event
                Event event = eventRepository.findTopByUserIdOrderByIdDesc(userId);

                if (event == null) {
                    return Map.of("error", "No event found.");
                }

                finalPrompt = """
                    You are an AI Event Planner.

                    Event Name: %s
                    City: %s
                    Theme: %s
                    Budget: ₹%s
                    Guests: %s

                    Provide suggestions in bullet points.
                """.formatted(
                        event.getName(),
                        event.getCity(),
                        event.getTheme(),
                        event.getBudget(),
                        event.getGuestCount()
                );
            }

            String aiText = aiService.generate(finalPrompt);

            return Map.of("text", aiText);

        } catch (Exception e) {
            return Map.of("error", "AI processing failed: " + e.getMessage());
        }
    }
}
