package com.logni.account.entities.accounts;

import com.logni.account.entities.common.Auditable;
import com.logni.account.enums.AccountState;

import java.time.Instant;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "account",indexes = {@Index(name = "idx_account_identifier",unique = true,columnList = "identifier"),
        @Index(name = "idx_customer", columnList = "customer_id")})
@NamedEntityGraph(name = "Account.ledger",
        attributeNodes = @NamedAttributeNode("ledger"))
public class Account extends Auditable<String> {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AccountGenerator")
    @SequenceGenerator(name = "AccountGenerator", sequenceName = "ACCOUNT_ID_GEN")
    private Long id;

    @ManyToOne
    private AccountType type; // for reporting

    @NotBlank(message = "AC Identifier is mandatory")
    @Column(name = "identifier")
    private String identifier;


    @NotBlank(message = "Name is mandatory")
    @Column(name = "a_name")
    private String name;

    @Column(name = "customer_id")
    private String customerId;

    @ManyToMany
    private List<AccountTag> accountTags;

    @Column(name = "signature_authorities")
    private String signatureAuthorities;

    //TODO later work with this private Money amount;
    @Column(name = "balance",precision = 19,scale = 6)
    private BigDecimal balance; // only for low volume account

    @Column(name = "lower_limit",precision = 19,scale = 6)
    private BigDecimal lowerLimit;

    @Column(name = "upper_limit",precision = 19,scale = 6)
    private BigDecimal upperLimit;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_account_id")
    private Account referenceAccount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ledger_id")
    private Ledger ledger;

    @Enumerated(EnumType.STRING)
    @Column(name = "a_state")
    private AccountState state; // AccountState

    @Column(name = "alternative_account_number", length = 256, nullable = true)
    private String alternativeAccountNumber; // for legacy system

    private Instant activationDate;


    public Account() {
    }

    public Account(@NotBlank(message = "AC Identifier is mandatory") String identifier, @NotBlank(message = "Name is mandatory") String name, String customerId) {
        this.identifier = identifier;
        this.name = name;
        this.customerId = customerId;
    }


   }
