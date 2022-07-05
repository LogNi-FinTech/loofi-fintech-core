package com.logni.account.entities.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.Instant;
import java.time.LocalDateTime;

@Setter
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class InsertAudit<U> {

    @JsonIgnore
    @CreatedDate
    @Column(name = "created_on")
    private Instant createdOn;

    @JsonIgnore
    @CreatedBy
    @Column(name = "created_by")
    private String createdBy;

}
