package com.logni.account.service.account;

import com.logni.account.config.UserData;
import com.logni.account.dto.rest.account.LedgerBalanceDto;
import com.logni.account.entities.accounts.Account;
import com.logni.account.entities.accounts.Ledger;
import com.logni.account.entities.accounts.LedgerBalanceArchive;
import com.logni.account.enums.AccountState;
import com.logni.account.enums.LedgerType;
import com.logni.account.repository.account.AccountRepository;
import com.logni.account.repository.account.LedgerBalanceArchiveRepo;
import com.logni.account.repository.account.LedgerRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import jakarta.annotation.Resource;

@Service
public class LedgerServiceImpl implements LedgerService {
  @Autowired
  LedgerRepository ledgerRepository;

  @Autowired
  AccountRepository accountRepository;
  @Autowired
  AccountService accountService;

  @Autowired
  LedgerBalanceArchiveRepo ledgerBalanceArchiveRepo;

  @Resource(name = "requestScopeTokenData")
  private UserData userData;

  @Transactional
  public Ledger createLedger(Ledger ledger) {

    ledger.setCreatedDate(Instant.now());
    ledger.setCreatedBy(userData.getUserId() == null ? "REST" : userData.getUserId());
    ledger = ledgerRepository.save(ledger);
    //todo work with created by
    if (ledger.getType() == LedgerType.SYSTEM && !ledger.getOnlyParent()) {
      Account account = new Account(ledger.getLedgerCode(), ledger.getName(), null);
      account.setState(AccountState.ACTIVE);
      account.setCreatedDate(Instant.now());
      account.setLedger(ledger);
      account.setCreatedBy(ledger.getCreatedBy());
      account = accountRepository.save(account);
      ledger.setSystemAccount(account);
      ledger = ledgerRepository.save(ledger);
    }
    return ledger;
  }


  public List<LedgerBalanceDto> getAllLedgerBalance(Instant time) {
    List<Ledger> ledgerList = ledgerRepository.findAll();
    return getLedgerBalance(ledgerList, time);
  }

  public List<LedgerBalanceDto> getSystemLedgerBalance(Instant time) {
    List<Ledger> ledgerList = ledgerRepository.findAllByType(LedgerType.SYSTEM);
    return getLedgerBalance(ledgerList, time);
  }

  public List<LedgerBalanceDto> getMemberLedgerBalance(Instant time) {
    List<Ledger> ledgerList = ledgerRepository.findAllByType(LedgerType.MEMBER);
    return getLedgerBalance(ledgerList, time);
  }

  public List<LedgerBalanceDto> getLedgerBalance(List<Ledger> ledgerList, Instant time) {
    List<LedgerBalanceDto> ledgerBalanceDtos = new ArrayList<>();
    ledgerList.forEach(ledger -> {
      BigDecimal balance = BigDecimal.ZERO;
      if (!ledger.getOnlyParent()) {
        LedgerBalanceArchive balanceArchive = ledgerBalanceArchiveRepo.findByLedgerAndBalanceAt(ledger, time);
        if (ledger.getType() == LedgerType.SYSTEM) {
          if (balanceArchive != null) {
            balance = balanceArchive.getBalance();
          } else {
            balance = accountService.getLedgerBalance(ledger, time);
          }
        } else {
          if (balanceArchive != null) {
            balance = balanceArchive.getBalance();
          } else {
            balance = accountService.getMemberLedgerBalance(ledger, time);
          }
        }
        if (balanceArchive == null) {
          saveLedgerBalanceAtArchive(ledger, balance, time);
        }
      }
      ledgerBalanceDtos.add(adaptLedgerDto(ledger, balance, time));
    });
    return ledgerBalanceDtos;
  }

  private void saveLedgerBalanceAtArchive(Ledger ledger, BigDecimal balance, Instant time) {
    LedgerBalanceArchive ledgerBalanceArchive = new LedgerBalanceArchive();
    ledgerBalanceArchive.setBalance(balance);
    ledgerBalanceArchive.setLedger(ledger);
    ledgerBalanceArchive.setBalanceAt(time);
    ledgerBalanceArchive.setCreatedOn(Instant.now());
    ledgerBalanceArchiveRepo.save(ledgerBalanceArchive);
  }

  private LedgerBalanceDto adaptLedgerDto(Ledger ledger, BigDecimal balance, Instant time) {
    LedgerBalanceDto ledgerBalanceDto = new LedgerBalanceDto();
    ledgerBalanceDto.setBalance(balance);
    ledgerBalanceDto.setBalanceAt(time);
    ledgerBalanceDto.setHead(ledger.getHead());
    ledgerBalanceDto.setLedgerCode(ledger.getLedgerCode());
    ledgerBalanceDto.setOnlyParent(ledger.getOnlyParent());
    ledgerBalanceDto.setType(ledger.getType());
    ledgerBalanceDto.setName(ledger.getName());
    if (ledger.getParentLedger() != null) {
      ledgerBalanceDto.setParentLedgerDto(adaptLedgerDto(ledger.getParentLedger(), BigDecimal.ZERO, time));
    }
    return ledgerBalanceDto;
  }
}
