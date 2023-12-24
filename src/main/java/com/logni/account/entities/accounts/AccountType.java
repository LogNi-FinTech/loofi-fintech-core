package com.logni.account.entities.accounts;

import com.logni.account.entities.common.Auditable;

import jakarta.persistence.*;

@Entity
@Table(name = "account_type")
public class AccountType extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    AccountType parent;

    private String description;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AccountType getParent() {
        return parent;
    }

    public void setParent(AccountType parent) {
        this.parent = parent;
    }
}
