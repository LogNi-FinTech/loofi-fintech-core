package com.logni.account.repository.transaction;

import com.logni.account.entities.transactions.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transactions,String> {
}
