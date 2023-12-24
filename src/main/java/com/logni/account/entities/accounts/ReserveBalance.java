package com.logni.account.entities.accounts;

import com.logni.account.entities.common.Auditable;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "reserve_balance")
public class ReserveBalance extends Auditable<String> {  // Maintain Reserve history at cold storage as at every balance check it will be called

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    private Account account;

    @Column(name = "reserve_balance",precision = 19,scale = 6)
    private BigDecimal reserveBalance;

    @Column(name = "status")
    private String status; // A->Active, I ->Inactive

    @Column(name = "note")
    private String note;

    @Column(name = "tag")
    private String tag; // for report


}
