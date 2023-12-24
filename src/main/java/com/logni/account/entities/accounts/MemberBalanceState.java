package com.logni.account.entities.accounts;

import com.logni.account.entities.transactions.Transactions;
import java.math.BigDecimal;
import java.time.Instant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "member_balance_state")
public class MemberBalanceState {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_balance_state_gen")
    @SequenceGenerator(name = "member_balance_state_gen", sequenceName = "member_balance_state_id_gen")
    @Column(name = "id")
    private Long id;

    @ManyToOne
    private Account account;

    @Column(name = "balance",precision = 19,scale = 6)
    private BigDecimal balance;

    @Column(name = "balance_at")
    private Instant balanceAt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Transactions transaction;


}
