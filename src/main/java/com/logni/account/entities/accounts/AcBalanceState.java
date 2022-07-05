package com.logni.account.entities.accounts;

import com.logni.account.entities.transactions.Transactions;
import com.sun.istack.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "low_vol_ac_balance_state")
public class AcBalanceState {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "low_vol_ac_balance_state_gen")
    @SequenceGenerator(name = "low_vol_ac_balance_state_gen", sequenceName = "low_vol_ac_balance_state_id_gen")
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
    private Transactions transaction; // this should be unique


}
