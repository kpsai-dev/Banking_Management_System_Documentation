package com.Tns.BankingManagementSystem.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.catalina.filters.ExpiresFilter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {

    @Positive(message = "Amount must be greater than zero")
    private Double amount;


}
