package com.logni.account.dto.rest.transaction;

import com.logni.account.entities.transactions.TransactionType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
