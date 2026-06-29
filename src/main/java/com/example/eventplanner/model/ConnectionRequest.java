package com.example.eventplanner.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "connection_requests")
@Data
public class ConnectionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;      // who is requesting
    private Long plannerId;   // who will receive request

    private String status = "pending";   // pending / accepted / rejected

    private LocalDateTime requestDate = LocalDateTime.now();
}
