package com.logni.account.dto.rest.account;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
