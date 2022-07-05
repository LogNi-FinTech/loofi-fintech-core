package com.logni.account.exception.account;

import com.logni.account.exception.CommonException;

public class BalanceIntegrityException extends CommonException {


   public BalanceIntegrityException(){

   }
   public BalanceIntegrityException(String errorCode,String message){
      super(errorCode,message);
   }
}
