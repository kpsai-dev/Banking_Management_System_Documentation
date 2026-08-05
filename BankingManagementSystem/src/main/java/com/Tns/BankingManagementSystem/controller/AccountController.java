package com.Tns.BankingManagementSystem.controller;

import com.Tns.BankingManagementSystem.dto.BalanceResponse;
import com.Tns.BankingManagementSystem.dto.TransactionRequest;
import com.Tns.BankingManagementSystem.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/{id}/deposit")
    public ResponseEntity<Void> deposit(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request) {

        accountService.deposit(id, request);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<Void> withdraw(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request) {

        accountService.withdraw(id, request);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<BalanceResponse> getBalance(
            @PathVariable Long id) {

        return ResponseEntity.ok(accountService.getBalance(id));
    }
}
