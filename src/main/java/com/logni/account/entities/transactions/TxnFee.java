package com.logni.account.entities.transactions;

import com.logni.account.entities.accounts.Account;
import com.logni.account.entities.common.Auditable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "txn_fee")
public class TxnFee extends Auditable<String> {
    //private Channel channel;
    public static enum ChargeType  {
        FIXED, PERCENTAGE, A_RATE, D_RATE, MIXED_A_D_RATES;
    }

    public static enum Subject  {
        SYSTEM, SOURCE, DESTINATION, FIXED_AC;
    }

    public static enum Type  {
        FEE, COMMISSION, VAT, AIT,OFFER,CASH_BACK;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    Type type;

    @NotBlank
    private String                  name;
    private String                  description;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payer")
    private Subject                 payer;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "receiver")
    private Subject                 receiver;
    private boolean                 enabled;

    @NotNull
    @ManyToOne
    private TransactionType         originalTxnType;

    @NotNull
    @ManyToOne
    private TransactionType         generatedTxnType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "charge_type")
    private ChargeType              chargeType;
    private BigDecimal              fixedAmount;
    private BigDecimal              percentage;
    private BigDecimal              maxPercentageAmount;
    private BigDecimal              minPercentageAmount;
    private String                  channel; //todo default[channel wise fee,commission]
    private boolean                 deductAmount; // deducted from main amount or not //todo will do later

    @ManyToOne
    private Account     fromFixedAccount;
    @ManyToOne
    private Account     toFixedAccount;


}
