package com.example.eventplanner.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "services")  // optional but safer
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;
    private Double price;
    private String description;
    private String availability;

    @ManyToOne
    @JoinColumn(name = "planner_id") // FK column name
    private Planner planner;
}
