package com.example.eventplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BudgetResponse {
    private double total;
    private double spent;
    private double remaining;
    private List<?> expenses;
}
