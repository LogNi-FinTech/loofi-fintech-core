package com.logni.account.service.account;

import com.logni.account.dto.rest.CustomerAcDTO;
import com.logni.account.dto.rest.account.AcActivationDto;
import com.logni.account.dto.rest.account.AcBalance;
import com.logni.account.dto.rest.account.AcCloseDto;
import com.logni.account.dto.rest.account.AccountDto;
import com.logni.account.dto.rest.account.StmtTxn;
import com.logni.account.entities.accounts.Account;
import com.logni.account.entities.accounts.Ledger;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    void createMemberAccount(AccountDto accountDto);
    void createCustomerAccount(CustomerAcDTO customerAcDTO);
    Account getAccountByIdentifier(String identifier);
    AccountDto getAccountDetails(String identifier);
    List<AccountDto> getAccountByCustomerId(String customerId);
    AcBalance getAccountBalance(String identifier);

    Page<StmtTxn> accountStatement(String identifier, Pageable pageable);

    void activateAccount(AcActivationDto activationDto);
    void closeAccount(AcCloseDto acCloseDto);
    AccountDto updateAccount(AccountDto accountDto);

    AcBalance getLedgerBalance(String ledgerCode);
    BigDecimal getLedgerBalance(Ledger ledger, Instant time);
    BigDecimal getMemberLedgerBalance(Ledger ledger,Instant time);

    Page<StmtTxn> ledgerStatement(String ledgerCode, Pageable pageable);
    StmtTxn adaptStatement(Object e);
}
