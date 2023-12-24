package com.logni.account.entities.transactions;

import com.fasterxml.jackson.databind.JsonNode;
import com.logni.account.entities.common.InsertAudit;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "transactions" ,indexes = {@Index(name = "idx_txn_time",columnList = "txn_time")})
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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data")
    private JsonNode data;
}
