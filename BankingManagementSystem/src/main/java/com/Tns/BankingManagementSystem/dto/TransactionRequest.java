package com.Tns.BankingManagementSystem.dto;

import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import org.apache.catalina.filters.ExpiresFilter;

@Data
@Builder
public class TransactionRequest {

    @Positive(message = "Amount must be greater than zero")
    private Double amount;


}
