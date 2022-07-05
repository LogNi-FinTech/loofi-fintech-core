package com.logni.account.repository.account;

import com.logni.account.entities.accounts.Account;
import com.logni.account.entities.accounts.Ledger;
import com.logni.account.enums.LedgerType;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public interface LedgerRepository extends JpaRepository<Ledger,Long> {
    Ledger findByLedgerCode(String ledgerCode);
    Page<Ledger> findAll(Pageable pageable);
    List<Ledger> findAllByType(LedgerType ledgerType);
}
