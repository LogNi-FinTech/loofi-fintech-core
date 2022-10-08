package com.logni.account.repository.account;

import com.logni.account.entities.accounts.Ledger;
import com.logni.account.entities.accounts.LedgerBalanceArchive;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerBalanceArchiveRepo extends JpaRepository<LedgerBalanceArchive,Long> {

    LedgerBalanceArchive findByLedgerAndBalanceAt(Ledger ledger, Instant balanceAt);

}
