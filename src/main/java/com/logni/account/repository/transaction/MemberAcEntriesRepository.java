package com.logni.account.repository.transaction;

import com.logni.account.dto.rest.account.AcBalance;
import com.logni.account.entities.accounts.Account;
import com.logni.account.entities.accounts.Ledger;
import com.logni.account.entities.transactions.MemberAcEntries;
import com.logni.account.entities.transactions.Transactions;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberAcEntriesRepository extends JpaRepository<MemberAcEntries,Long> {

    @EntityGraph(value = "ma.stmt")
    Page<MemberAcEntries> findAllByAccount(Account account, Pageable pageable);

    @Query("SELECT SUM(e.amount) FROM MemberAcEntries e where e.account.ledger=?1 and e.txnTime>=?2 and e.txnTime<?3")
    BigDecimal getBalanceSumFromToUptoTime(Ledger ledger,  Instant fromTime,Instant toTime);

    @Query("SELECT SUM(e.amount) FROM MemberAcEntries e where e.account.ledger=?1 and e.txnTime<?2")
    BigDecimal getBalanceSumUptoTime(Ledger ledger,  Instant uptoTime);

    List<MemberAcEntries> findAllByTransaction(Transactions transaction);

}
