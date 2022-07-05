package com.logni.account.unit;

import com.logni.account.dto.rest.account.AcActivationDto;
import com.logni.account.dto.rest.account.AcBalance;
import com.logni.account.dto.rest.account.AcCloseDto;
import com.logni.account.dto.rest.account.AccountDto;
import com.logni.account.dto.rest.account.StmtTxn;
import com.logni.account.entities.accounts.Account;
import com.logni.account.enums.AccountState;
import com.logni.account.exception.CommonException;
import com.logni.account.repository.account.AccountRepository;
import com.logni.account.service.account.AccountService;
import com.logni.account.service.account.AccountServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountServiceIntTest {


    AccountServiceImpl accountService;
    private AccountRepository accountRepository;

    @BeforeAll
    public void setAccountService(){
        accountService = new AccountServiceImpl();
        accountRepository = mock(AccountRepository.class);
        accountService.accountRepository = accountRepository;
    }


    @Test
    void shouldPendingAcActivate()  {
        when(accountRepository.findByIdentifier("01674242986")).thenReturn(getAc(AccountState.PENDING));
        Assertions.assertDoesNotThrow(() ->  accountService.activateAccount(getAcDto()));
        verify(accountRepository).findByIdentifier("01674242986");
    }

    @Test()
    void throwExceptionForActiveAc()  {
        when(accountRepository.findByIdentifier("01674242986")).thenReturn(getAc(AccountState.ACTIVE));
        Assertions.assertThrows(CommonException.class,()->accountService.activateAccount(getAcDto()));
    }

    AcActivationDto getAcDto(){
        AcActivationDto accountDto = new AcActivationDto();
        accountDto.setIdentifier("01674242986");
        return accountDto;
    }
    Account getAc(AccountState state){
        Account account = new Account();
        account.setIdentifier("01674242986");
        account.setState(state);
        return account;
    }
}
