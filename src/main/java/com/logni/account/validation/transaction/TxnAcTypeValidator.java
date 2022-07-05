package com.logni.account.validation.transaction;

import com.logni.account.entities.accounts.Account;
import com.logni.account.entities.transactions.TransactionType;

import java.util.List;

public class TxnAcTypeValidator {
    private List<Validation> validators;

    public TxnAcTypeValidator(List<Validation> validators) {
        this.validators = validators;
    }

    public void validate(Account from, Account to, TransactionType txnType) {
        validators.forEach(validator -> validator.validate( from,  to,  txnType));
    }
}
