package com.Tns.BankingManagementSystem.service;

import com.Tns.BankingManagementSystem.dto.BalanceResponse;
import com.Tns.BankingManagementSystem.dto.TransactionRequest;
import com.Tns.BankingManagementSystem.entity.Account;
import com.Tns.BankingManagementSystem.exception.BusinessException;
import com.Tns.BankingManagementSystem.exception.ResourceNotFoundException;
import com.Tns.BankingManagementSystem.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @CachePut(value="accounts",key="#id")
    @Override
    public void deposit(Long id, TransactionRequest request) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found"));

        account.setBalance(account.getBalance() + request.getAmount());

        accountRepository.save(account);
    }

    @CachePut(value="accounts",key="#id")
    @Override
    public void withdraw(Long id, TransactionRequest request) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found"));

        if (account.getBalance() < request.getAmount()) {

            throw new BusinessException("Insufficient balance");
        }

        account.setBalance(account.getBalance() - request.getAmount());

        accountRepository.save(account);
    }

    @Cacheable(value = "accounts", key = "#id")
    @Override
    public BalanceResponse getBalance(Long id) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found"));

        return BalanceResponse.builder()
                .accountId(account.getId())
                .balance(account.getBalance())
                .build();
    }
}
