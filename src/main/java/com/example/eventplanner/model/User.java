package com.example.eventplanner.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false, unique=true)
    private String email;

    @Column(nullable=false)
    private String password;

    @Column(nullable=false)
    private String role; // USER, VENDOR, ADMIN
    private boolean active = true;
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private List<Event> events;

}
