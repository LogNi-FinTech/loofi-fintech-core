package com.logni.account.entities.accounts;

import com.logni.account.entities.common.Auditable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BigDecimal getReserveBalance() {
        return reserveBalance;
    }

    public void setReserveBalance(BigDecimal reserveBalance) {
        this.reserveBalance = reserveBalance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

}
