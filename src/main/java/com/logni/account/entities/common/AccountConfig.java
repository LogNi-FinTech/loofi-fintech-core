package com.logni.account.entities.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
