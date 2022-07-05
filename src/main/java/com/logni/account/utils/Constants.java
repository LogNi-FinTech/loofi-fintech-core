package com.logni.account.utils;

import java.math.BigDecimal;

public class Constants {
    private Constants(){}
    public static final String STATUS_PROCESSED = "PROCESSED";
    public static final String CUSTOMER_LEDGER_ID = "CUSTOMER_LEDGER_ID";
    public static final String STATUS_FAILED = "FAILED";
    public static final String SYSTEM = "SYSTEM";
    public static final String REVERSE_TAG = "REVERSE";
    public static final String REVERSE_ORIGINAL_TAG = "REVERSED";
    public static final BigDecimal minimumTxnAmount = new BigDecimal(0.0001);
    public static final BigDecimal maximumTxnAmount = new BigDecimal(1000000000);

}
