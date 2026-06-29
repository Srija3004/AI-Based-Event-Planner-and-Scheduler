package com.example.eventplanner.repository;

import com.example.eventplanner.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Payment findByRazorpayOrderId(String razorpayOrderId);
    List<Payment> findByPlanner_IdAndStatus(Long plannerId, String status);

}
