package com.Tns.BankingManagementSystem.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransactionRequest {

    @Positive(message = "Amount must be greater than zero")
    private Double amount;
}
