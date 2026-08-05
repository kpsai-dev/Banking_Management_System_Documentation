package com.Tns.BankingManagementSystem.service;

import com.Tns.BankingManagementSystem.dto.BalanceResponse;
import com.Tns.BankingManagementSystem.dto.TransactionRequest;
import com.Tns.BankingManagementSystem.entity.Account;
import com.Tns.BankingManagementSystem.exception.BusinessException;
import com.Tns.BankingManagementSystem.exception.ResourceNotFoundException;
import com.Tns.BankingManagementSystem.repository.AccountRepository;
import com.Tns.BankingManagementSystem.service.AccountServiceImpl;
import lombok.Builder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    @DisplayName("Should deposit amount successfully")
    void shouldDepositSuccessfully() {

        Account account = Account.builder()
                .id(1L)
                .balance(1000.0)
                .build();

        TransactionRequest request = TransactionRequest.builder()
                .amount(500.0)
                .build();

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        accountService.deposit(1L, request);

        assertEquals(1500.0, account.getBalance());

        verify(accountRepository).save(account);
    }

    @Test
    @DisplayName("Should withdraw amount successfully")
    void shouldWithdrawSuccessfully() {

        Account account = Account.builder()
                .id(1L)
                .balance(2000.0)
                .build();

        TransactionRequest request = TransactionRequest.builder()
                .amount(500.0)
                .build();

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        accountService.withdraw(1L, request);

        assertEquals(1500.0, account.getBalance());

        verify(accountRepository).save(account);
    }


    @Test
    @DisplayName("Should throw BusinessException when balance is insufficient")
    void shouldThrowBusinessException() {

        Account account = Account.builder()
                .id(1L)
                .balance(1000.0)
                .build();

        TransactionRequest request = TransactionRequest.builder()
                .amount(5000.0)
                .build();

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        assertThrows(BusinessException.class,
                () -> accountService.withdraw(1L, request));

        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when account is not found")
    void shouldThrowResourceNotFoundException() {

        when(accountRepository.findById(100L))
                .thenReturn(Optional.empty());

        TransactionRequest request = TransactionRequest.builder()
                .amount(100.0)
                .build();

        assertThrows(ResourceNotFoundException.class,
                () -> accountService.deposit(100L, request));

        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return account balance")
    void shouldReturnBalance() {

        Account account = Account.builder()
                .id(1L)
                .balance(3500.0)
                .build();

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        BalanceResponse response = accountService.getBalance(1L);

        assertNotNull(response);

        assertEquals(3500.0, response.getBalance());

        verify(accountRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when fetching balance of invalid account")
    void shouldThrowExceptionWhenBalanceAccountNotFound() {

        when(accountRepository.findById(200L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> accountService.getBalance(200L));
    }
}