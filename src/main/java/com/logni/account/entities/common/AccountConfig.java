package com.logni.account.entities.common;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AccountConfig {

    @Id
    @Column(name = "id")
    private String id;

    private String value;

}
