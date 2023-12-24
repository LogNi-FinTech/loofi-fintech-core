package com.logni.account.entities.accounts;

import java.math.BigDecimal;
import java.time.Instant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class LedgerBalanceArchive {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ledger_balance_archive_gen")
    @SequenceGenerator(name = "ledger_balance_archive_gen", sequenceName = "ledger_balance_archive_gen_id_gen")
    @Column(name = "id")
    private Long id;

    @ManyToOne
    private Ledger ledger;

    @Column(name = "balance",precision = 19,scale = 6)
    private BigDecimal balance;

    @Column(name = "balance_at")
    private Instant balanceAt;

    @Column(name = "created_on")
    private Instant createdOn;

}
