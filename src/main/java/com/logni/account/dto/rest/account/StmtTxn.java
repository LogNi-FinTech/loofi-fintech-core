package com.logni.account.dto.rest.account;

import com.logni.account.entities.accounts.Account;
import com.logni.account.entities.transactions.TransactionType;
import com.logni.account.entities.transactions.Transactions;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.Instant;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StmtTxn {

    private String txnId;
    private BigDecimal amount;
    private String description;
    private String note;
    private String referenceId;
    private String channel;
    private String account;
    private String remoteAccount;
    private String txnType;
    private String data;
    private Instant txnTime;
}
