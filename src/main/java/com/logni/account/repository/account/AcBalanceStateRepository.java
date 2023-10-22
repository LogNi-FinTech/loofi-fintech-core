package com.logni.account.repository.account;

import com.logni.account.entities.accounts.Account;
import com.logni.account.entities.accounts.AccountBalanceState;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcBalanceStateRepository extends JpaRepository<AccountBalanceState,Long> {

    AccountBalanceState findTopByAccountOrderByBalanceAtDesc(Account account);
    AccountBalanceState findTopByAccountAndBalanceAtLessThanOrderByBalanceAtDesc(Account account,
            Instant closeTime);
}
