package com.logni.account.utils;

import java.util.HashMap;
import java.util.Map;

public class AccountErrors {

    private AccountErrors(){}
    // component code
    public static final String ACCOUNT_SERVICE = "10";

    // feature code
    public static final String ACCOUNT_MANAGEMENT = "01";
    public static final String ACCOUNT_TRANSACTION = "02";


    // error code
    public static final String INVALID_ACCOUNT = "001";

    public static final String ACCOUNT_NOT_ACTIVE = "002";
    public static final String ACCOUNT_NOT_FOUND = "003";
    public static final String TXNTYPE_NOT_FOUND = "004";
    public static final String NOT_ENOUGH_BALANCE = "005";
    public static final String INVALID_AC_TYPE = "006";


    public static final String INTERNAL_ERROR= "500";
    public static final String INVALID_INPUT="007";
    public static final String INVITATION_NOT_PENDING="009";

    public static final String INVALID_AMOUNT = "010";
    public static final String INVALID_TXN_TYPE = "011";
    public static final String FROM_AC_NOT_FOUND = "012";
    public static final String TO_AC_NOT_FOUND = "013";
    public static final String TXN_TYPE_NOT_FOUND = "014";
    public static final String FROM_AC_IS_NOT_ACTIVE = "015";
    public static final String TO_AC_IS_SUSPENDED_OR_CLOSE = "016";
    public static final String TXN_TYPE_IS_NOT_ENABLE = "017";
    public static final String NOT_ALLOWED_FOR_TXN_TYPE = "018";
    public static final String MINIMUM_AMOUNT_EXCEEDED = "019";
    public static final String BALANCE_INTEGRITY_ERROR = "020";
    public static final String TRANSACTION_NOT_FOUND = "021";
    public static final String TRANSACTION_ALREADY_REVERSED = "022";

    // Error mapping
    public static final Map<String, String> ERROR_MAP = new HashMap<>();

    static {
        ERROR_MAP.put(INVALID_ACCOUNT, "Invalid Account");
        ERROR_MAP.put(ACCOUNT_NOT_ACTIVE, "Account is not Active");
        ERROR_MAP.put(ACCOUNT_NOT_FOUND, "Account Not Found");
        ERROR_MAP.put(TXNTYPE_NOT_FOUND, "TxnType Not Found");
        ERROR_MAP.put(INVALID_AC_TYPE, "Invalid AC Type");
        ERROR_MAP.put(INVALID_INPUT, "Invitation Input Data");
        ERROR_MAP.put(INVITATION_NOT_PENDING, "Invitation Is not pending status");
        ERROR_MAP.put(INVALID_AMOUNT, "Debit And Credit Amount is not equal");
        ERROR_MAP.put(NOT_ENOUGH_BALANCE, "NOT_ENOUGH_BALANCE");
        ERROR_MAP.put(INVALID_TXN_TYPE, "INVALID_TXN_TYPE");
        ERROR_MAP.put(FROM_AC_NOT_FOUND, "FROM_AC_NOT_FOUND");
        ERROR_MAP.put(TO_AC_NOT_FOUND, "TO_AC_NOT_FOUND");
        ERROR_MAP.put(TXN_TYPE_NOT_FOUND, "TXN_TYPE_NOT_FOUND");
        ERROR_MAP.put(FROM_AC_IS_NOT_ACTIVE, "FROM_AC_IS_NOT_ACTIVE");
        ERROR_MAP.put(TO_AC_IS_SUSPENDED_OR_CLOSE, "TO_AC_IS_SUSPENDED_OR_CLOSE");
        ERROR_MAP.put(TXN_TYPE_IS_NOT_ENABLE, "TXN_TYPE_IS_NOT_ENABLE");
        ERROR_MAP.put(NOT_ALLOWED_FOR_TXN_TYPE, "NOT_ALLOWED_FOR_TXN_TYPE");
        ERROR_MAP.put(MINIMUM_AMOUNT_EXCEEDED, "MINIMUM_AMOUNT_EXCEEDED");
        ERROR_MAP.put(BALANCE_INTEGRITY_ERROR, "BALANCE_INTEGRITY_ERROR:AC ID %s");
        ERROR_MAP.put(TRANSACTION_NOT_FOUND, "TRANSACTION_DETAIL_NOT_FOUND: ID %s");
        ERROR_MAP.put(TRANSACTION_ALREADY_REVERSED, "TRANSACTION_ALREADY_REVERSED");
    }

    public static String getErrorCode(String featureCode,String errorCode){
        return AccountErrors.ACCOUNT_SERVICE + featureCode + errorCode;
    }
}
