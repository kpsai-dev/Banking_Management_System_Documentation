package com.Tns.BankingManagementSystem.repository;

import com.Tns.BankingManagementSystem.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
