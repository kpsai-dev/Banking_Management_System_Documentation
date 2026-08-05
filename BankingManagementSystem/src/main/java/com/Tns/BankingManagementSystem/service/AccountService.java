package com.Tns.BankingManagementSystem.service;

import com.Tns.BankingManagementSystem.dto.BalanceResponse;
import com.Tns.BankingManagementSystem.dto.TransactionRequest;

public interface AccountService {

    void deposit(Long id, TransactionRequest request);

    void withdraw(Long id, TransactionRequest request);

    BalanceResponse getBalance(Long id);

}