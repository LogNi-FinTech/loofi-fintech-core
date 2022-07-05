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

@Entity
@Table(name = "transactions" ,indexes = {@Index(name = "idx_txn_time",columnList = "txn_time")})
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Transactions extends InsertAudit<String> { // have unit transaciton may have multiple sub transaction


    @Id
    @Column(name = "id",length = 10,updatable = false,nullable = false)
    private String id;

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


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getTxnTime() {
        return txnTime;
    }

    public void setTxnTime(Instant txnTime) {
        this.txnTime = txnTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public JsonNode getData() {
        return data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }
}
