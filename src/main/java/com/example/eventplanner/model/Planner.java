package com.example.eventplanner.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "planner")
public class Planner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String location;
    private String about;
    private Integer experienceYears;
    private String specialties;
}
