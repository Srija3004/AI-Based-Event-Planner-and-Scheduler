package com.example.eventplanner.repository;

import com.example.eventplanner.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByEvent_User_Id(Long userId);
}
