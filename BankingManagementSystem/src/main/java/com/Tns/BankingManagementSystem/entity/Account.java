package com.Tns.BankingManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "EmpAccounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String accountHolderName;

    @Column(nullable = false)
    private Double balance;
}