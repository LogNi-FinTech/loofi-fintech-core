package com.logni.account.dto.rest.transaction;

import com.logni.account.entities.transactions.TransactionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TxnLine {

    @NotBlank
    private String accountNumber;
    @DecimalMin(value = "0.0001", inclusive = false)
    private BigDecimal amount;
    @NotNull
    private TransactionType txnType;
    private  String remoteAccount;


}
