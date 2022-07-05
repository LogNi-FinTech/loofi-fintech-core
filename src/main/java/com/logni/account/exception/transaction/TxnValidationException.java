package com.logni.account.exception.transaction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TxnValidationException extends RuntimeException {
    //todo code, message, status
    String errorCode;
    public TxnValidationException(String errorCode,String message) {
        super(message);
        this.errorCode=errorCode;
    }
}
