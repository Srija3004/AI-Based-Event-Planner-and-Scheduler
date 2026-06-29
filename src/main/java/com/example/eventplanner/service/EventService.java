package com.example.eventplanner.service;

import com.example.eventplanner.model.Event;
import com.example.eventplanner.model.User;
import com.example.eventplanner.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getEventsByUser(User user) {
        return eventRepository.findByUser(user);
}

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}
