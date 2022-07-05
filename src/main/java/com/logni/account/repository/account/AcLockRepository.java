package com.logni.account.repository.account;

import com.logni.account.entities.accounts.AccountLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Optional;

public interface AcLockRepository extends JpaRepository<AccountLock,Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    public Optional<AccountLock> findByAccountId(Long accountId);

}
