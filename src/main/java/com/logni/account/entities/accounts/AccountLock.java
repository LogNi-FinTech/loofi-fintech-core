package com.logni.account.entities.accounts;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "account_lock",indexes = {@Index(name = "idx_account_id", unique = true,columnList = "account_id"),})
public class AccountLock {

    @Id
    @Column(name = "account_id")
    Long accountId;

}
