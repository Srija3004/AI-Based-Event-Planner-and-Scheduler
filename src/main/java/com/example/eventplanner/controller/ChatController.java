package com.example.eventplanner.controller;

import com.example.eventplanner.model.Message;
import com.example.eventplanner.repository.MessageRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:63342")
public class ChatController {

    @Autowired
    private MessageRepository messageRepository;

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody ChatMessageDTO dto) {
        Message msg = new Message();
        msg.setSenderId(dto.getSenderId());
        msg.setReceiverId(dto.getReceiverId());
        msg.setContent(dto.getMessage());
        msg.setTimestamp(LocalDateTime.now());

        messageRepository.save(msg);
        return ResponseEntity.ok("Message sent");
    }

    @GetMapping("/messages/{userId}/{plannerId}")
    public List<Message> getChat(@PathVariable Long userId, @PathVariable Long plannerId) {
        return messageRepository.findConversationBetween(userId, plannerId);
    }

    @Data
    public static class ChatMessageDTO {
        private Long senderId;
        private Long receiverId;
        private String message;
    }
}


