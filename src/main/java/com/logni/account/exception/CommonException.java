package com.logni.account.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonException extends RuntimeException {
    String errorCode;

    public CommonException(){

    }
    public CommonException(String errorCode,String message){
        super(message);
        this.errorCode = errorCode;
    }

}
