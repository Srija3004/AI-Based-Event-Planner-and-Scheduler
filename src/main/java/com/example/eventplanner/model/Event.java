package com.example.eventplanner.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String city;
    private String venue;
    private String date;
    private String time;
    private Integer guestCount;
    private Integer duration;
    private Double budget;
    private String theme;
    private String requirements;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "planner_id")
    private Planner planner;

    private String status = "pending";

    // 🔥 NEW FIELDS FOR PAYMENT
    @Column(name = "payment_status")
    private String paymentStatus = "UNPAID";

    @Column(name = "razorpay_order_id")
    private String razorpayOrderId;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    private Double paymentAmount;


}
