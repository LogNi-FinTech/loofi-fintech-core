package com.logni.account.validation.transaction;

import com.logni.account.dto.rest.transaction.TxnRequest;
import com.logni.account.entities.accounts.Account;
import com.logni.account.entities.transactions.TransactionType;

public interface Validation {
    void  validate(TxnRequest txnRequest);
    void validate(Account from, Account to, TransactionType txnType);
}
