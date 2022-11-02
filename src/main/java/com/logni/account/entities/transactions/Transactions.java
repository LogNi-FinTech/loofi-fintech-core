package com.logni.account.entities.transactions;


import com.fasterxml.jackson.databind.JsonNode;
import com.logni.account.entities.common.InsertAudit;
import com.logni.account.utils.JsonType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "transactions" ,indexes = {@Index(name = "idx_txn_time",columnList = "txn_time")})
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Transactions extends InsertAudit<String> { // have unit transaciton may have multiple sub transaction

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TransactionGen")
    @SequenceGenerator(name = "TransactionGen", sequenceName = "TRANSACTION_ID_GEN")
    private Long id;

    @Column(name = "txn_id")
    private String txnId;

    @Column(name = "txn_time")
    private Instant txnTime; // time partition
    private String description;
    private String note;
    private String referenceId;
    private String channel;
    private String tag;

    @Type(type = "jsonb")
    @Column(name = "data",columnDefinition = "jsonb")
    private JsonNode  data;
}
