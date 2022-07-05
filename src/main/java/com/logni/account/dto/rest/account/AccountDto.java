package com.logni.account.dto.rest.account;

import com.logni.account.entities.accounts.AccountTag;
import com.logni.account.entities.accounts.AccountType;
import com.logni.account.enums.AccountState;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class AccountDto {

    @NotBlank
    private String name;
    @NotBlank
    private String identifier;
    @NotNull
    @Min(1)
    private Long ledgerId;

    private String ledgerName;

    @NotBlank
    String customerId;

    private AccountState state;

    private AccountType type;

    List<AccountTag> tagList;

    private BigDecimal lowerLimit;
    private String alternativeAccountNumber;



}
