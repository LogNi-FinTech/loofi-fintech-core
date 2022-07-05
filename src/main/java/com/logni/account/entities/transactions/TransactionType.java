package com.logni.account.entities.transactions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.logni.account.entities.accounts.Ledger;
import com.logni.account.entities.common.Auditable;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "transaction_type",indexes = {@Index(name ="idx_unique_txn_code", unique = true, columnList = "txn_code")})
public class TransactionType extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @NotNull
    @ManyToOne
    private Ledger fromType;


    @NotNull
    @ManyToOne
    private Ledger toType;

    @Min(1)
    @Column(name = "txn_code")
    private int txnCode;

    @NotBlank(message = "Name is mandatory")
    private String name;

    private String description;

    private boolean enabled;

    @JsonIgnore
    @OneToMany
    @JoinColumn(name = "original_txn_type_id")
    List<TxnFee> txnFeeList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ledger getFromType() {
        return fromType;
    }

    public void setFromType(Ledger fromType) {
        this.fromType = fromType;
    }

    public Ledger getToType() {
        return toType;
    }

    public void setToType(Ledger toType) {
        this.toType = toType;
    }

    public int getTxnCode() {
        return txnCode;
    }

    public void setTxnCode(int txnCode) {
        this.txnCode = txnCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<TxnFee> getTxnFeeList() {
        return txnFeeList;
    }

    public void setTxnFeeList(List<TxnFee> txnFeeList) {
        this.txnFeeList = txnFeeList;
    }
}
