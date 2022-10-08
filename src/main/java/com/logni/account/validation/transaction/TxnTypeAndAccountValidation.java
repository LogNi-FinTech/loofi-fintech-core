package com.logni.account.validation.transaction;

import com.logni.account.dto.rest.transaction.TxnRequest;
import com.logni.account.entities.accounts.Account;
import com.logni.account.entities.transactions.TransactionType;
import com.logni.account.enums.AccountState;
import com.logni.account.exception.transaction.TxnValidationException;
import com.logni.account.utils.AccountErrors;

public class TxnTypeAndAccountValidation implements Validation {

    @Override
    public void validate(TxnRequest txnRequest) {

        TransactionType type = txnRequest.getTransactionType();
        if(type==null){
            throw new TxnValidationException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_TRANSACTION,
                    AccountErrors.INVALID_TXN_TYPE),AccountErrors.ERROR_MAP.get(AccountErrors.INVALID_TXN_TYPE));
        }
        if(((type.getId()==null||type.getId()<=0)
                &&(type.getTxnCode()<=0))){
            throw new TxnValidationException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_TRANSACTION,
                    AccountErrors.INVALID_TXN_TYPE),AccountErrors.ERROR_MAP.get(AccountErrors.INVALID_TXN_TYPE));
        }
    }

    @Override
    public void validate(Account from, Account to, TransactionType txnType) {
         if(from==null){
             throw new TxnValidationException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_TRANSACTION,
                     AccountErrors.FROM_AC_NOT_FOUND),AccountErrors.ERROR_MAP.get(AccountErrors.FROM_AC_NOT_FOUND));

         }
         if(to==null){
             throw new TxnValidationException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_TRANSACTION,
                     AccountErrors.TO_AC_NOT_FOUND),AccountErrors.ERROR_MAP.get(AccountErrors.TO_AC_NOT_FOUND));

         }

         if(txnType==null){
             throw new TxnValidationException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_TRANSACTION,
                     AccountErrors.TXN_TYPE_NOT_FOUND),AccountErrors.ERROR_MAP.get(AccountErrors.TXN_TYPE_NOT_FOUND));
         }

         if(from.getState()!= AccountState.ACTIVE){
             throw new TxnValidationException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_TRANSACTION,
                     AccountErrors.FROM_AC_IS_NOT_ACTIVE),AccountErrors.ERROR_MAP.get(AccountErrors.FROM_AC_IS_NOT_ACTIVE));
         }

        if((to.getState()== AccountState.SUSPENDED)||(to.getState()== AccountState.CLOSE)){
            throw new TxnValidationException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_TRANSACTION,
                    AccountErrors.TO_AC_IS_SUSPENDED_OR_CLOSE),AccountErrors.ERROR_MAP.get(AccountErrors.TO_AC_IS_SUSPENDED_OR_CLOSE));
        }

        if(!txnType.isEnabled()){
            throw new TxnValidationException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_TRANSACTION,
                    AccountErrors.TXN_TYPE_IS_NOT_ENABLE),AccountErrors.ERROR_MAP.get(AccountErrors.TXN_TYPE_IS_NOT_ENABLE));
        }
        if(!from.getLedger().getId().equals(txnType.getFromType().getId())){
            throw new TxnValidationException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_TRANSACTION,
                    AccountErrors.NOT_ALLOWED_FOR_TXN_TYPE),AccountErrors.ERROR_MAP.get(AccountErrors.NOT_ALLOWED_FOR_TXN_TYPE));

        }
        if(!to.getLedger().getId().equals(txnType.getToType().getId())){
            throw new TxnValidationException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_TRANSACTION,
                    AccountErrors.NOT_ALLOWED_FOR_TXN_TYPE),AccountErrors.ERROR_MAP.get(AccountErrors.NOT_ALLOWED_FOR_TXN_TYPE));

        }

    }
}
