package com.logni.account.entities.accounts;

import com.logni.account.entities.common.Auditable;

import jakarta.persistence.*;

@Entity
@Table(name = "account_tag")
public class AccountTag extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @Column(name = "tag")
    private String tag;
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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


}
