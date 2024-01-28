package com.logni.account.repository.account;

import com.logni.account.entities.accounts.Account;
import com.logni.account.enums.AccountHead;
import com.logni.account.enums.LedgerType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

  @EntityGraph(value = "Account.ledger")
  Account findByIdentifier(String identifier);

  List<Account> findAllByCustomerId(String customer);

  @Query("SELECT a FROM Account a where a.ledger.type=?1")
  List<Account> findAllMemberAc(LedgerType ledgerType);

  @Query("SELECT a FROM Account a where a.identifier like %:identifier%")
  Page<Account> findAllByIdentifierLike(Pageable pageable, @Param("identifier") String identifier);

  @Query("SELECT a FROM Account a where a.ledger.head=:acHead")
  Page<Account> findAllByLedgerHead(Pageable pageable, @Param("acHead") AccountHead accountHead);

  @Query("SELECT a FROM Account a where a.ledger.head=:acHead and a.identifier like %:identifier%")
  Page<Account> findAllByIdentifierAndHead(Pageable pageable, @Param("acHead") AccountHead accountHead, @Param("identifier") String identifier);

}
