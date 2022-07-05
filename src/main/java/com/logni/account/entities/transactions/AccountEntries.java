package com.logni.account.entities.transactions;

import com.fasterxml.jackson.databind.JsonNode;
import com.logni.account.entities.accounts.Account;
import com.logni.account.utils.JsonType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

//@Embeddable
public class AccountEntries { // maintain member type ledger

    @ManyToOne
    private Transactions transaction; // this should be unique

    @ManyToOne
    private Account account;

    @ManyToOne
    private Account remoteAccount;

    @Column(name = "amount",precision = 19,scale = 6)
    private BigDecimal amount;

    @ManyToOne
    private TransactionType txnType;

    @Type(type = "jsonb")
    @Column(name = "data",columnDefinition = "jsonb")
    private String data;

    public Transactions getTransaction() {
        return transaction;
    }

    public void setTransaction(Transactions transaction) {
        this.transaction = transaction;
    }


    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getRemoteAccount() {
        return remoteAccount;
    }

    public void setRemoteAccount(Account remoteAccount) {
        this.remoteAccount = remoteAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getTxnType() {
        return txnType;
    }

    public void setTxnType(TransactionType txnType) {
        this.txnType = txnType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
