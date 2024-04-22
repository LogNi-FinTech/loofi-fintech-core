package com.logni.account.entities.transactions;

import com.logni.account.entities.accounts.Account;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;


@Getter
@Setter
@Entity
@Table(name = "member_account_entries",indexes = @Index(name = "idx_member_account_txn_time",columnList = "txn_time"))
@NamedEntityGraph(name = "ma.stmt",attributeNodes = {
                @NamedAttributeNode("transaction"),
                @NamedAttributeNode("account"),
                @NamedAttributeNode("remoteAccount"),
                @NamedAttributeNode("txnType")})
public class MemberAcEntries  {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MemberAcEntriesGen")
    @SequenceGenerator(name = "MemberAcEntriesGen", sequenceName = "MEMBER_AC_ENTRIES_ID_GEN")
    @Column(name = "id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Transactions transaction;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account remoteAccount;

    @Column(name = "amount",precision = 19,scale = 6)
    private BigDecimal amount;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private TransactionType txnType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data")
    private String data;

    @Column(name = "txn_time")
    private Instant txnTime; // time partition



}
