package com.logni.account.service.transaction;

import com.logni.account.entities.transactions.TransactionType;

public interface TxnTypeService {
    TransactionType getTxnType(TransactionType transactionType);
}
