package com.logni.account.entities.accounts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.logni.account.entities.common.Auditable;
import com.logni.account.enums.AccountHead;
import com.logni.account.enums.LedgerType;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


import java.util.List;

@Entity
@Table(name = "ledger",indexes = {@Index(name = "idx_ledger_code",unique = true,columnList = "ledger_code"),})
public class Ledger  extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "only_parent")
    Boolean onlyParent;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private LedgerType type;//Member, System

    // will be head ledger customer,merchant,agent,distributor,system,disbursement,


    @NotNull(message = "Ledger head is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(name = "head")
    private AccountHead head; // asset,liability,income,expense,payable,receivable,accrual


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_ac_id")
    private Account systemAccount; // when system will be a account id

    @ManyToOne(fetch = FetchType.LAZY)
    private Currency currency;

    @NotBlank(message = "Code is mandatory")
    @Column(name = "ledger_code")
    private String ledgerCode; // unique ledger code

    @NotBlank(message = "Name is mandatory")
    @Column(name = "a_name")
    private String name;
    @Column(name = "description")
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_ledger_id")
    private Ledger parentLedger;

    @Column(name = "show_accounts_in_chart")
    private Boolean showAccountsInChart;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    List<Product> products;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getOnlyParent() {
        return onlyParent;
    }

    public void setOnlyParent(Boolean onlyParent) {
        this.onlyParent = onlyParent;
    }

    public LedgerType getType() {
        return type;
    }

    public void setType(LedgerType type) {
        this.type = type;
    }


    public AccountHead getHead() {
        return head;
    }

    public void setHead(AccountHead head) {
        this.head = head;
    }

    public Account getSystemAccount() {
        return systemAccount;
    }

    public void setSystemAccount(Account systemAccount) {
        this.systemAccount = systemAccount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getLedgerCode() {
        return ledgerCode;
    }

    public void setLedgerCode(String ledgerCode) {
        this.ledgerCode = ledgerCode;
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

    public Ledger getParentLedger() {
        return parentLedger;
    }

    public void setParentLedger(Ledger parentLedger) {
        this.parentLedger = parentLedger;
    }


    public Boolean getShowAccountsInChart() {
        return showAccountsInChart;
    }

    public void setShowAccountsInChart(Boolean showAccountsInChart) {
        this.showAccountsInChart = showAccountsInChart;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
