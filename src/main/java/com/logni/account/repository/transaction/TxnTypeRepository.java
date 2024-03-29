package com.logni.account.repository.transaction;

import com.logni.account.entities.transactions.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TxnTypeRepository extends JpaRepository<TransactionType,Long> {
    TransactionType findByTxnCode(Integer txnCode);
    TransactionType findByName(String name);
}
