package com.logni.account.dto.rest;

import com.logni.account.enums.AccountState;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerAcDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String identifier;
    @NotBlank
    String customerId;

    private AccountState state;

}
