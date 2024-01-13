package com.logni.account.entities.accounts;


import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "ledger_account_balance_state",indexes = {@Index(name = "idx_balance_at",columnList = "balance_at")})
public class AccountBalanceState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    private Account account;

    @Column(name = "balance",precision = 19,scale = 6)
    private BigDecimal balance;

    @Column(name = "balance_at")
    private Instant balanceAt;

    @Column(name = "created_on")
    private Instant createdOn;

}
