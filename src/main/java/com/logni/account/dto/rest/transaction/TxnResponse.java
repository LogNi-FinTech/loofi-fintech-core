package com.logni.account.dto.rest.transaction;

import com.logni.account.dto.rest.Response;

import java.math.BigDecimal;

public class TxnResponse extends Response {
    String fromAc;
    String toAc;
    String fromName;
    String toName;
    BigDecimal fromBalance;
    BigDecimal toBalance;
    String txnId;

    public TxnResponse() {
    }

    public TxnResponse(String txnId, String status) {
        super(status);
        this.txnId = txnId;
    }

    public String getFromAc() {
        return fromAc;
    }

    public void setFromAc(String fromAc) {
        this.fromAc = fromAc;
    }

    public String getToAc() {
        return toAc;
    }

    public void setToAc(String toAc) {
        this.toAc = toAc;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public BigDecimal getFromBalance() {
        return fromBalance;
    }

    public void setFromBalance(BigDecimal fromBalance) {
        this.fromBalance = fromBalance;
    }

    public BigDecimal getToBalance() {
        return toBalance;
    }

    public void setToBalance(BigDecimal toBalance) {
        this.toBalance = toBalance;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }
}
