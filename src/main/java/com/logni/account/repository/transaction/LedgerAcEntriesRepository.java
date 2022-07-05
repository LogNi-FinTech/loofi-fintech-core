package com.logni.account.repository.transaction;

import com.logni.account.entities.accounts.Account;
import com.logni.account.entities.transactions.LedgerAcEntries;
import com.logni.account.entities.transactions.Transactions;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public interface LedgerAcEntriesRepository extends JpaRepository<LedgerAcEntries,Long> {

    @Query("SELECT SUM(e.amount) FROM LedgerAcEntries e where e.account=?1 and e.txnTime>=?2")
    BigDecimal getBalanceSumFromTime(Account account,  Instant fromTime);

    @Query("SELECT SUM(le.amount) FROM LedgerAcEntries le where le.account=?1")
    BigDecimal getBalanceSum(Account account);

    @Query("SELECT SUM(e.amount) FROM LedgerAcEntries e where e.account=?1 and e.txnTime>=?2 and e.txnTime<?3")
    BigDecimal getBalanceSumFromTimeToTime(Account account,  Instant fromTime, Instant toTime);

    @Query("SELECT SUM(le.amount) FROM LedgerAcEntries le where le.account=?1 and le.txnTime <?2")
    BigDecimal getBalanceSumToTime(Account account,Instant toTime);

    @EntityGraph(value = "lae.stmt")
    Page<LedgerAcEntries> findAllByAccount(Account account, Pageable pageable);

    List<LedgerAcEntries> findAllByTransaction(Transactions transaction);
}
