package com.logni.account.entities.transactions;

import com.logni.account.entities.accounts.Account;
import com.logni.account.utils.JsonType;
import com.sun.istack.NotNull;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "member_account_entries",indexes = @Index(name = "idx_member_account_txn_time",columnList = "txn_time"))
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
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

    @Type(type = "jsonb")
    @Column(name = "data",columnDefinition = "jsonb")
    private String data;

    @Column(name = "txn_time")
    private Instant txnTime; // time partition



}
