package com.logni.account.repository.transaction;

import com.logni.account.entities.transactions.Transactions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transactions, Long> {
  Optional<Transactions> findByTxnId(String txnId);
}
