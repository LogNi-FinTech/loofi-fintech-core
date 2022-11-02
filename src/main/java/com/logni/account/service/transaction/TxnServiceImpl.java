package com.logni.account.service.transaction;

import com.logni.account.dto.rest.account.StmtTxn;
import com.logni.account.dto.rest.transaction.BaseReverseRequest;
import com.logni.account.dto.rest.transaction.BulkTxnRequest;
import com.logni.account.dto.rest.transaction.JournalRequest;
import com.logni.account.dto.rest.transaction.TxnDetail;
import com.logni.account.dto.rest.transaction.TxnRequest;
import com.logni.account.dto.rest.transaction.TxnResponse;
import com.logni.account.entities.accounts.Account;
import com.logni.account.entities.accounts.MemberBalanceState;
import com.logni.account.entities.transactions.*;
import com.logni.account.enums.LedgerType;
import com.logni.account.exception.CommonException;
import com.logni.account.exception.account.BalanceIntegrityException;
import com.logni.account.exception.transaction.TxnValidationException;
import com.logni.account.repository.account.AcLockRepository;
import com.logni.account.repository.account.AccountRepository;
import com.logni.account.repository.account.MemberBalanceStateRepository;
import com.logni.account.repository.transaction.LedgerAcEntriesRepository;
import com.logni.account.repository.transaction.MemberAcEntriesRepository;
import com.logni.account.repository.transaction.TransactionRepository;
import com.logni.account.service.account.AccountService;
import com.logni.account.utils.AccountErrors;
import com.logni.account.utils.Constants;
import com.logni.account.validation.transaction.TxnAcTypeValidator;
import com.logni.account.validation.transaction.TxnValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TxnServiceImpl implements TxnService {

  private final TxnValidator txnValidator;
  private final TxnAcTypeValidator txnAcTypeValidator;
  private final AccountService accountService;
  private final TxnTypeService txnTypeService;
  private final TransactionRepository transactionRepository;
  private final MemberAcEntriesRepository memberAcEntriesRepository;
  private final LedgerAcEntriesRepository ledgerAcEntriesRepository;
  private final AccountRepository accountRepository;
  private final AcLockRepository acLockRepository;
  private final MemberBalanceStateRepository memberBalanceStateRepository;


  @Transactional
  public TxnResponse doJournalTxn(JournalRequest journalRequest) {
    validateJournalRequest(journalRequest);
    Transactions transaction = generateTxn(journalRequest);
    journalRequest.getDebtors().forEach(txnLine -> {
      TransactionType txnType = txnTypeService.getTxnType(txnLine.getTxnType());
      txnTypeNotFoundCheck(txnType);
      Account from = accountRepository.findByIdentifier(txnLine.getAccountNumber());
      Account remoteAc = accountRepository.findByIdentifier(txnLine.getRemoteAccount());

      accountNotFoundCheck(from);
      if (from.getLedger().getType() == LedgerType.MEMBER) {
        acLockRepository.findByAccountId(from.getId());
      }
      validateBalance(from, txnLine.getAmount());

      List<MemberAcEntries> fromMemberAcEntries = null;
      List<LedgerAcEntries> fromLedgerAcEntries = null;
      if (from.getLedger().getType() == LedgerType.MEMBER) {
        fromMemberAcEntries = generateMemberAcEntries(from, remoteAc, txnType, txnLine.getAmount(), transaction, false);
      }
      if (from.getLedger().getType() == LedgerType.SYSTEM) {
        fromLedgerAcEntries = generateLedgerAcEntries(from, remoteAc, txnType, txnLine.getAmount(), transaction, false);
      }
      if (fromLedgerAcEntries != null) {
        ledgerAcEntriesRepository.saveAll(fromLedgerAcEntries);
      }
      if (fromMemberAcEntries != null) {
        memberAcEntriesRepository.saveAll(fromMemberAcEntries);
        fromMemberAcEntries.forEach(memberAcEntries -> {
          updateMemberBalance(memberAcEntries, transaction);
        });
      }
    });

    journalRequest.getCreditors().forEach(txnLine -> {
      TransactionType txnType = txnTypeService.getTxnType(txnLine.getTxnType());
      txnTypeNotFoundCheck(txnType);
      Account to = accountRepository.findByIdentifier(txnLine.getAccountNumber());
      accountNotFoundCheck(to);
      Account remoteAc = accountRepository.findByIdentifier(txnLine.getRemoteAccount());

      if (to.getLedger().getType() == LedgerType.MEMBER) {
        acLockRepository.findByAccountId(to.getId());
      }
      List<MemberAcEntries> toMemberAcEntries = null;
      List<LedgerAcEntries> toLedgerAcEntries = null;
      if (to.getLedger().getType() == LedgerType.MEMBER) {
        toMemberAcEntries = generateMemberAcEntries(to, remoteAc, txnType, txnLine.getAmount(), transaction, true);
      }
      if (to.getLedger().getType() == LedgerType.SYSTEM) {
        toLedgerAcEntries = generateLedgerAcEntries(to, remoteAc, txnType, txnLine.getAmount(), transaction, true);
      }
      if (toLedgerAcEntries != null) {
        ledgerAcEntriesRepository.saveAll(toLedgerAcEntries);
      }
      if (toMemberAcEntries != null) {
        memberAcEntriesRepository.saveAll(toMemberAcEntries);
        toMemberAcEntries.forEach(memberAcEntries -> {
          updateMemberBalance(memberAcEntries, transaction);
        });
      }
    });
    return new TxnResponse(transaction.getTxnId(), Constants.STATUS_PROCESSED);
  }

  private Transactions generateTxn(JournalRequest journalRequest) {

    Transactions transaction = new Transactions();
    transaction.setTxnId(generateTxnId());
    transaction.setChannel(journalRequest.getChannel());
    transaction.setData(journalRequest.getData());
    transaction.setDescription((journalRequest.getDescription() != null ? journalRequest.getDescription() : "") + (journalRequest.getNote() != null
      ? journalRequest.getNote()
      : ""));
    transaction.setNote(journalRequest.getNote());
    transaction.setReferenceId(journalRequest.getReferenceId());
    transaction.setTag(journalRequest.getTag());
    transaction.setTxnTime(Instant.now());
    transaction.setCreatedBy(journalRequest.getMaker() + "-" + journalRequest.getChecker());
    transaction.setCreatedOn(Instant.now());
    return transactionRepository.save(transaction);
  }

  private void validateJournalRequest(JournalRequest journalRequest) {

    BigDecimal totalDebitAmount = BigDecimal.ZERO;
    BigDecimal totalCreditAmount = BigDecimal.ZERO;
    journalRequest.getDebtors().forEach(txnLine -> {
      totalDebitAmount.add(txnLine.getAmount());
    });
    journalRequest.getCreditors().forEach(txnLine -> {
      totalCreditAmount.add(txnLine.getAmount());
    });
    if (!totalCreditAmount.equals(totalDebitAmount)) {
      throw new CommonException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_TRANSACTION, AccountErrors.INVALID_AMOUNT),
        AccountErrors.ERROR_MAP.get(AccountErrors.INVALID_AMOUNT));
    }

  }

  private void txnTypeNotFoundCheck(TransactionType txnType) {
    if (txnType == null) {
      throw new CommonException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_TRANSACTION, AccountErrors.TXNTYPE_NOT_FOUND),
        AccountErrors.ERROR_MAP.get(AccountErrors.TXNTYPE_NOT_FOUND));
    }
  }

  private void accountNotFoundCheck(Account account) {
    if (account == null) {
      throw new CommonException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_MANAGEMENT, AccountErrors.ACCOUNT_NOT_FOUND),
        AccountErrors.ERROR_MAP.get(AccountErrors.ACCOUNT_NOT_FOUND));
    }
  }

  @Transactional
  public TxnResponse doBulkTxn(BulkTxnRequest bulkTxnRequest) {
    List<TxnResponse> responseList = new ArrayList<>();
    TransactionType transactionType = txnTypeService.getTxnType(bulkTxnRequest.getTxnRequestList().get(0).getTransactionType());
    TxnRequest parentTxnRequest = bulkTxnRequest.getTxnRequestList().get(0);
    if (parentTxnRequest.getData() == null || "null".equals(parentTxnRequest.getData().toString())) {
      parentTxnRequest.setData(null);
    }
    Transactions transaction = generateTxn(transactionType, parentTxnRequest);
    boolean isParent = true;
    for (TxnRequest txnRequest : bulkTxnRequest.getTxnRequestList()) {
      responseList.add(doTxn(txnRequest, transaction, isParent));
      isParent = false;
    }
    return responseList.get(0);
  }

  @Transactional
  public TxnResponse doTxn(TxnRequest txnRequest) {
    return doTxn(txnRequest, null, true);
  }

  public TxnResponse doTxn(TxnRequest txnRequest, Transactions transaction, Boolean isFromLock) {
    log.info("Txn Request:{}", txnRequest);
    txnValidator.validate(txnRequest);
    TransactionType transactionType = txnTypeService.getTxnType(txnRequest.getTransactionType());
    Account from = null;
    Account to = null;
    if (Constants.SYSTEM.equalsIgnoreCase(txnRequest.getFromAc())) {
      from = transactionType.getFromType().getSystemAccount();
    } else {
      from = accountService.getAccountByIdentifier(txnRequest.getFromAc());
    }
    if (Constants.SYSTEM.equalsIgnoreCase(txnRequest.getToAc())) {
      to = transactionType.getToType().getSystemAccount();
    } else {
      to = accountService.getAccountByIdentifier(txnRequest.getToAc());
    }
    transaction = performTxn(from, to, transactionType, txnRequest, txnRequest.getAmount(), transaction, isFromLock);
    return buildTxnResponse(txnRequest, from, to, transaction);
  }

  private Transactions performTxn(Account from, Account to, TransactionType txnType, TxnRequest txnRequest, BigDecimal amount,
                                  Transactions transaction, Boolean isFromLock) {
    txnAcTypeValidator.validate(from, to, txnType);
    lockAccounts(from, to, isFromLock);
    validateBalance(from, amount);
    if (transaction == null) {
      transaction = generateTxn(txnType, txnRequest);
    }
    performLedgerEntries(from, to, txnType, amount, transaction);
    if (txnType.getTxnFeeList() != null && txnType.getTxnFeeList().size() > 0) {
      performSubTransaction(txnType.getTxnFeeList(), txnRequest, transaction, txnType);
    }
    return transaction;
  }

  private void performSubTransaction(List<TxnFee> txnFeeList, TxnRequest txnRequest, Transactions transaction, TransactionType parentTxnType) {
    for (TxnFee txnFee : txnFeeList) {
      if (!txnFee.isEnabled()) {
        continue;
      }
      TransactionType genTxnType = txnFee.getGeneratedTxnType();
      Account from = null;
      Account to = null;
      switch (txnFee.getPayer()) {
        case SYSTEM:
          if (genTxnType.getFromType().getSystemAccount() != null) {
            from = genTxnType.getFromType().getSystemAccount();
          } else {
            throw new CommonException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_TRANSACTION, AccountErrors.INVALID_TXN_TYPE),
              AccountErrors.ERROR_MAP.get(AccountErrors.INVALID_TXN_TYPE));
          }
          break;
        case SOURCE:
          if (Constants.SYSTEM.equalsIgnoreCase(txnRequest.getFromAc())) {
            from = parentTxnType.getFromType().getSystemAccount();
          } else {
            from = accountRepository.findByIdentifier(txnRequest.getFromAc());
          }

          break;
        case DESTINATION:
          if (Constants.SYSTEM.equalsIgnoreCase(txnRequest.getToAc())) {
            from = parentTxnType.getToType().getSystemAccount();
          } else {
            from = accountRepository.findByIdentifier(txnRequest.getToAc());
          }

          break;
        case FIXED_AC:
          from = txnFee.getFromFixedAccount();
          break;

      }

      switch (txnFee.getReceiver()) {
        case SYSTEM:
          if (genTxnType.getToType().getSystemAccount() != null) {
            to = genTxnType.getToType().getSystemAccount();
          } else {
            throw new CommonException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_TRANSACTION, AccountErrors.INVALID_TXN_TYPE),
              AccountErrors.ERROR_MAP.get(AccountErrors.INVALID_TXN_TYPE));
          }
          break;
        case SOURCE:
          if (Constants.SYSTEM.equalsIgnoreCase(txnRequest.getFromAc())) {
            to = parentTxnType.getToType().getSystemAccount();
          } else {
            to = accountRepository.findByIdentifier(txnRequest.getFromAc());
          }

          break;
        case DESTINATION:
          if (Constants.SYSTEM.equalsIgnoreCase(txnRequest.getToAc())) {
            to = parentTxnType.getToType().getSystemAccount();
          } else {
            to = accountRepository.findByIdentifier(txnRequest.getToAc());
          }

          break;
        case FIXED_AC:
          to = txnFee.getToFixedAccount();
          break;

      }
      BigDecimal subTxnAmount = calculateSubTxnAmount(txnFee, txnRequest.getAmount());

      if (from != null && to != null) {
        performTxn(from, to, txnFee.getGeneratedTxnType(), txnRequest, subTxnAmount, transaction, false);
      }

    }
  }

  private BigDecimal calculateSubTxnAmount(TxnFee txnFee, BigDecimal amount) {

    BigDecimal subTxnAmount = BigDecimal.ZERO;
    switch (txnFee.getChargeType()) {
      case FIXED:
        subTxnAmount = txnFee.getFixedAmount();
        break;
      case PERCENTAGE:
        subTxnAmount = amount.multiply(txnFee.getPercentage()).divide(BigDecimal.valueOf(100));

        if (txnFee.getMaxPercentageAmount() != null && subTxnAmount.compareTo(txnFee.getMaxPercentageAmount()) > 0) {
          subTxnAmount = txnFee.getMaxPercentageAmount();
        }
        if (txnFee.getMinPercentageAmount() != null && subTxnAmount.compareTo(txnFee.getMaxPercentageAmount()) < 0) {
          subTxnAmount = txnFee.getMinPercentageAmount();
        }
        break;
      case A_RATE:
        throw new RuntimeException("NOT_IMPLEMENTED");
        //break;
      case D_RATE:
        throw new RuntimeException("NOT_IMPLEMENTED");
        //break;

    }

    if (Constants.minimumTxnAmount.compareTo(subTxnAmount) > 0) {
      throw new CommonException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_TRANSACTION, AccountErrors.MINIMUM_AMOUNT_EXCEEDED),
        AccountErrors.ERROR_MAP.get(AccountErrors.MINIMUM_AMOUNT_EXCEEDED));
    }

    return subTxnAmount;
  }

  private void lockAccounts(Account from, Account to, Boolean isFromLock) {
    if (from.getLedger().getType() == LedgerType.MEMBER && isFromLock) {
      acLockRepository.findByAccountId(from.getId());
    }
    if (to.getLedger().getType() == LedgerType.MEMBER && isFromLock) {
      acLockRepository.findByAccountId(to.getId());
    }
  }

  public void performLedgerEntries(Account from, Account to, TransactionType type, BigDecimal amount, Transactions transaction) {
    List<MemberAcEntries> fromMemberAcEntries = null;
    List<LedgerAcEntries> fromLedgerAcEntries = null;
    List<MemberAcEntries> toMemberAcEntries = null;
    List<LedgerAcEntries> toLedgerAcEntries = null;
    if (from.getLedger().getType() == LedgerType.MEMBER) {
      fromMemberAcEntries = generateMemberAcEntries(from, to, type, amount, transaction, false);
    } else {
      fromLedgerAcEntries = generateLedgerAcEntries(from, to, type, amount, transaction, false);
    }
    if (to.getLedger().getType() == LedgerType.MEMBER) {
      toMemberAcEntries = generateMemberAcEntries(to, from, type, amount, transaction, true);
    } else {
      toLedgerAcEntries = generateLedgerAcEntries(to, from, type, amount, transaction, true);
    }
    if (fromMemberAcEntries != null) {
      memberAcEntriesRepository.saveAll(fromMemberAcEntries);
      fromMemberAcEntries.forEach(memberAcEntries -> {
        updateMemberBalance(memberAcEntries, transaction);
      });
    }
    if (toMemberAcEntries != null) {
      memberAcEntriesRepository.saveAll(toMemberAcEntries);
      toMemberAcEntries.forEach(memberAcEntries -> {
        updateMemberBalance(memberAcEntries, transaction);
      });
    }
    if (fromLedgerAcEntries != null) {
      ledgerAcEntriesRepository.saveAll(fromLedgerAcEntries);
    }
    if (toLedgerAcEntries != null) {
      ledgerAcEntriesRepository.saveAll(toLedgerAcEntries);
    }

  }

  private void updateMemberBalance(MemberAcEntries memberAcEntries, Transactions transaction) {
    Account account = memberAcEntries.getAccount();
    BigDecimal balance = account.getBalance().add(memberAcEntries.getAmount());
    account.setBalance(balance);
    account.setLastModifiedDate(Instant.now());
    accountRepository.save(account);
    saveMemberAcBalanceState(account, balance, transaction);
  }


  private void saveMemberAcBalanceState(Account account, BigDecimal balance, Transactions transactions) {
    MemberBalanceState memberBalanceState = new MemberBalanceState();
    memberBalanceState.setAccount(account);
    memberBalanceState.setBalance(balance);
    memberBalanceState.setBalanceAt(Instant.now());
    memberBalanceState.setTransaction(transactions);
    memberBalanceStateRepository.save(memberBalanceState);
  }

  private Transactions generateTxn(TransactionType type, TxnRequest txnRequest) {

    Transactions transaction = new Transactions();
    transaction.setTxnId(generateTxnId());
    transaction.setChannel(txnRequest.getChannel());
    transaction.setData(txnRequest.getData());
    transaction.setDescription(
      type.getName() + "From:" + txnRequest.getFromAc() + "|TO:" + txnRequest.getToAc() + "." + (txnRequest.getDescription() != null
        ? txnRequest.getDescription()
        : ""));
    transaction.setNote(txnRequest.getNote());
    transaction.setReferenceId(txnRequest.getReferenceId());
    transaction.setTag(txnRequest.getTag());
    transaction.setTxnTime(Instant.now());
    transaction.setCreatedBy(txnRequest.getMaker() + "-" + txnRequest.getChecker());
    transaction.setCreatedOn(Instant.now());
    return transactionRepository.save(transaction);
  }

  private List<MemberAcEntries> generateMemberAcEntries(Account account, Account remoteAc, TransactionType type, BigDecimal amount,
                                                        Transactions transaction, boolean isCredit) {
    MemberAcEntries memberAcEntries = new MemberAcEntries();
    memberAcEntries.setTxnTime(transaction.getTxnTime());
    memberAcEntries.setAmount(isCredit ? amount : amount.negate());
    memberAcEntries.setAccount(account);
    memberAcEntries.setRemoteAccount(remoteAc);
    memberAcEntries.setTransaction(transaction);
    memberAcEntries.setTxnType(type);
    List<MemberAcEntries> memberAcEntriesList = new ArrayList<>();
    memberAcEntriesList.add(memberAcEntries);
    return memberAcEntriesList;
  }

  private List<LedgerAcEntries> generateLedgerAcEntries(Account account, Account remoteAc, TransactionType type, BigDecimal amount,
                                                        Transactions transaction, boolean isCredit) {
    LedgerAcEntries ledgerAcEntries = new LedgerAcEntries();
    ledgerAcEntries.setTxnTime(transaction.getTxnTime());
    ledgerAcEntries.setAmount(isCredit ? amount : amount.negate());
    ledgerAcEntries.setAccount(account);
    ledgerAcEntries.setRemoteAccount(remoteAc);
    ledgerAcEntries.setTransaction(transaction);
    ledgerAcEntries.setTxnType(type);
    List<LedgerAcEntries> ledgerAcEntriesList = new ArrayList<>();
    ledgerAcEntriesList.add(ledgerAcEntries);
    return ledgerAcEntriesList;
  }

  private void validateBalance(Account from, BigDecimal amount) {
    if (from.getLedger().getType() == LedgerType.MEMBER) {
      BigDecimal lowCredit = from.getLowerLimit();
      BigDecimal fromBalance = null;
      if (lowCredit == null) {
        lowCredit = BigDecimal.ZERO;
      }
      fromBalance = from.getBalance();
      if (fromBalance.subtract(amount).compareTo(lowCredit) < 0) {
        throw new TxnValidationException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_TRANSACTION, AccountErrors.NOT_ENOUGH_BALANCE),
          AccountErrors.ERROR_MAP.get(AccountErrors.NOT_ENOUGH_BALANCE));
      }
    }

  }

  private String generateTxnId() {
    return UUID.randomUUID().toString().replace("-", "").toUpperCase().substring(0, 10);
  }

  private TxnResponse buildTxnResponse(TxnRequest request, Account from, Account to, Transactions transaction) {
    TxnResponse txnResponse = new TxnResponse();
    txnResponse.setStatus(Constants.STATUS_PROCESSED);
    txnResponse.setFromAc(from.getIdentifier());
    txnResponse.setToAc(to.getIdentifier());
    txnResponse.setFromName(from.getName());
    txnResponse.setToName(to.getName());
    if (from.getLedger().getType() == LedgerType.MEMBER) {
      txnResponse.setFromBalance(from.getBalance());
    }
    if (to.getLedger().getType() == LedgerType.MEMBER) {
      txnResponse.setToBalance(to.getBalance());
    }
    txnResponse.setTxnId(transaction.getTxnId());
    return txnResponse;

  }

  @Transactional(readOnly = true)
  public TxnDetail getTxnDetail(String txnId) {
    Optional<Transactions> transactionsOptional = transactionRepository.findById(txnId);
    transactionsOptional.orElseThrow(
      () -> new CommonException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_TRANSACTION, AccountErrors.TRANSACTION_NOT_FOUND),
        String.format(AccountErrors.ERROR_MAP.get(AccountErrors.TRANSACTION_NOT_FOUND), txnId)));
    List<MemberAcEntries> memberEntries = memberAcEntriesRepository.findAllByTransaction(transactionsOptional.get());
    BigDecimal sum = memberEntries.stream().map(MemberAcEntries::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    List<LedgerAcEntries> ledgerAcEntries = null;
    if (BigDecimal.ZERO.compareTo(sum) != 0) {
      ledgerAcEntries = ledgerAcEntriesRepository.findAllByTransaction(transactionsOptional.get());
    }
    TxnDetail txnDetail = new TxnDetail();
    BeanUtils.copyProperties(transactionsOptional.get(), txnDetail); //MODEL_MAPPER.map(transactionsOptional.get(),TxnDetail.class);
    List<StmtTxn> stmtTxnList = memberEntries.stream().map(me -> accountService.adaptStatement(me)).collect(Collectors.toList());
    if (ledgerAcEntries != null && !ledgerAcEntries.isEmpty()) {
      stmtTxnList.addAll(ledgerAcEntries.stream().map(le -> accountService.adaptStatement(le)).collect(Collectors.toList()));
    }
    txnDetail.setLedgerEntries(stmtTxnList);
    return txnDetail;
  }

  @Transactional(rollbackFor = Exception.class)
  public TxnResponse doReverseTxn(BaseReverseRequest reverseRequest) {
    Optional<Transactions> transactionsOptional = transactionRepository.findById(reverseRequest.getTxnId());
    transactionsOptional.orElseThrow(
      () -> new CommonException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_TRANSACTION, AccountErrors.TRANSACTION_NOT_FOUND),
        String.format(AccountErrors.ERROR_MAP.get(AccountErrors.TRANSACTION_NOT_FOUND), reverseRequest.getTxnId())));

    List<MemberAcEntries> memberEntries = memberAcEntriesRepository.findAllByTransaction(transactionsOptional.get());
    BigDecimal sum = memberEntries.stream().map(MemberAcEntries::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    List<LedgerAcEntries> ledgerAcEntries = null;
    if (BigDecimal.ZERO.compareTo(sum) != 0) {
      ledgerAcEntries = ledgerAcEntriesRepository.findAllByTransaction(transactionsOptional.get());
    }
    Transactions originalTransaction = transactionsOptional.get();
    originalTransaction.setTag(Constants.REVERSE_ORIGINAL_TAG);
    Transactions reversTransaction = getTransactionsForReverse(reverseRequest);
    reversTransaction = transactionRepository.save(reversTransaction);
    List<MemberAcEntries> rMemberEntries = new ArrayList<>();
    List<LedgerAcEntries> rLedgerAcEntries = new ArrayList<>();
    extractedLedgerEntries(ledgerAcEntries, reversTransaction, rLedgerAcEntries);
    extractedMemberEntries(memberEntries, reversTransaction, rMemberEntries);

    for (MemberAcEntries rMemberEntry : rMemberEntries) {
      Account account = rMemberEntry.getAccount();
      boolean debit = rMemberEntry.getAmount().compareTo(BigDecimal.ZERO) < 0;
      lockAcForReverse(account, debit);
      if (debit) {
        validateBalance(account, rMemberEntry.getAmount());
      }
      updateMemberBalance(rMemberEntry, reversTransaction);
    }
    TxnResponse txnResponse = new TxnResponse();
    txnResponse.setStatus(Constants.STATUS_PROCESSED);
    txnResponse.setTxnId(reversTransaction.getTxnId());
    return txnResponse;
  }

  private void lockAcForReverse(Account account, boolean debit) {
    if (debit && account.getLedger().getType() == LedgerType.MEMBER) {
      acLockRepository.findByAccountId(account.getId());
    }
    if (debit && account.getLedger().getType() == LedgerType.MEMBER) {
      acLockRepository.findByAccountId(account.getId());
    }
    if (!debit && account.getLedger().getType() == LedgerType.MEMBER) {
      acLockRepository.findByAccountId(account.getId());
    }
  }

  private void extractedMemberEntries(List<MemberAcEntries> memberEntries, Transactions reversTransaction, List<MemberAcEntries> rMemberEntries) {
    if (memberEntries != null && !memberEntries.isEmpty()) {
      memberEntries.forEach(me -> {
        MemberAcEntries meRe = new MemberAcEntries();
        meRe.setTransaction(reversTransaction);
        meRe.setAmount(me.getAmount().negate());
        meRe.setTxnTime(reversTransaction.getTxnTime());
        meRe.setTxnType(me.getTxnType());
        meRe.setRemoteAccount(me.getRemoteAccount());
        meRe.setAccount(me.getAccount());
        rMemberEntries.add(meRe);
      });
    }
  }

  private void extractedLedgerEntries(List<LedgerAcEntries> ledgerAcEntries, Transactions reversTransaction,
                                      List<LedgerAcEntries> rLedgerAcEntries) {
    if (ledgerAcEntries != null && !ledgerAcEntries.isEmpty()) {
      ledgerAcEntries.forEach(le -> {
        LedgerAcEntries leRe = new LedgerAcEntries();
        leRe.setTransaction(reversTransaction);
        leRe.setAmount(le.getAmount().negate());
        leRe.setTxnTime(reversTransaction.getTxnTime());
        leRe.setTxnType(le.getTxnType());
        leRe.setAccount(le.getAccount());
        leRe.setRemoteAccount(le.getRemoteAccount());
        rLedgerAcEntries.add(leRe);
      });
    }
  }

  private Transactions getTransactionsForReverse(BaseReverseRequest reverseRequest) {
    Transactions reversTransaction = new Transactions();
    reversTransaction.setTag(Constants.REVERSE_TAG);
    reversTransaction.setTxnTime(Instant.now());
    reversTransaction.setChannel(reversTransaction.getChannel());
    reversTransaction.setNote(reverseRequest.getNote());
    reversTransaction.setData(reverseRequest.getData());
    reversTransaction.setTxnId(this.generateTxnId());
    reversTransaction.setReferenceId(reverseRequest.getTxnId());
    return reversTransaction;
  }
}
