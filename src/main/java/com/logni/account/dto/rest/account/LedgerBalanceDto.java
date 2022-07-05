package com.logni.account.dto.rest.account;

import com.logni.account.entities.accounts.Ledger;
import com.logni.account.enums.AccountHead;
import com.logni.account.enums.LedgerType;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LedgerBalanceDto {

    private String ledgerCode;
    private String name;
    private Boolean onlyParent;
    private LedgerType type;
    private AccountHead head;
    private LedgerBalanceDto parentLedgerDto;

    private BigDecimal balance;
    private Instant balanceAt;

}
