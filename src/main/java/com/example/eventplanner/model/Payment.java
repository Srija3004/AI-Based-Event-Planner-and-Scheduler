package com.example.eventplanner.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long eventId;
    private Double amount;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;

    private String status; // PENDING, PAID, FAILED

    private LocalDateTime paymentDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "planner_id", nullable = false)
    private Planner planner;

}
