package com.example.eventplanner.repository;

import com.example.eventplanner.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("""
        SELECT m FROM Message m
        WHERE (m.senderId = :a AND m.receiverId = :b)
           OR (m.senderId = :b AND m.receiverId = :a)
        ORDER BY m.timestamp ASC
        """)
    List<Message> findConversationBetween(@Param("a") Long a, @Param("b") Long b);
}
