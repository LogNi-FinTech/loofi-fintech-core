package com.logni.account.repository.account;

import com.logni.account.entities.accounts.Account;
import com.logni.account.entities.accounts.Ledger;
import com.logni.account.enums.LedgerType;
import org.hibernate.validator.constraints.EAN;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account,Long> {

    @EntityGraph(value = "Account.ledger")
    Account findByIdentifier(String identifier);

    List<Account> findAllByCustomerId(String customer);
    @Query("SELECT a FROM Account a where a.ledger.type=?1")
    List<Account> findAllMemberAc(LedgerType ledgerType);

}
