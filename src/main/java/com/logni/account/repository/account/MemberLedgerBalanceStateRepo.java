package com.logni.account.repository.account;

import com.logni.account.entities.accounts.Ledger;
import com.logni.account.entities.accounts.MemberLedgerBalanceState;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberLedgerBalanceStateRepo extends JpaRepository<MemberLedgerBalanceState, Long> {

  MemberLedgerBalanceState findTopByLedgerAndBalanceAtBeforeOrderByIdDesc(Ledger ledger, Instant time);
}
