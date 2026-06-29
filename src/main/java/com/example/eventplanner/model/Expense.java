package com.example.eventplanner.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;
    private String description;
    private Double amount;
    private String status; // paid | pending

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}
