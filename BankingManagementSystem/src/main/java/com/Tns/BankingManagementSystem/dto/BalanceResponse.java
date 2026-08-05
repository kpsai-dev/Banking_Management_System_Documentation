package com.Tns.BankingManagementSystem.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BalanceResponse {

    private Long accountId;
    private Double balance;
}
