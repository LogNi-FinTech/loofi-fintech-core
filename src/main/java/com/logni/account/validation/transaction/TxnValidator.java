package com.logni.account.validation.transaction;

import com.logni.account.dto.rest.transaction.TxnRequest;

import java.util.List;

public class TxnValidator {

    private  List<Validation> validators;

    public TxnValidator(List<Validation> validators) {
        this.validators = validators;
    }

    public void validate(TxnRequest txnRequest) {
        validators.forEach(validator -> validator.validate(txnRequest));

    }
}
