package com.logni.account.entities.accounts;

import com.logni.account.entities.common.Auditable;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.persistence.*;

@Entity
@Table(name = "product")
public class Product extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code")
    private String code;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
