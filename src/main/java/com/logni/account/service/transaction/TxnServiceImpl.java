package com.logni.account.service.transaction;


import com.logni.account.dto.rest.account.StmtTxn;
import com.logni.account.dto.rest.transaction.BaseReverseRequest;
import com.logni.account.dto.rest.transaction.BulkTxnRequest;
import com.logni.account.dto.rest.transaction.JournalRequest;
import com.logni.account.dto.rest.transaction.TxnDetail;
import com.logni.account.dto.rest.transaction.TxnRequest;
import com.logni.account.dto.rest.transaction.TxnResponse;
import com.logni.account.entities.accounts.Account;
import com.logni.account.entities.accounts.AcBalanceState;
import com.logni.account.entities.transactions.*;
import com.logni.account.enums.LedgerType;
import com.logni.account.exception.CommonException;
import com.logni.account.exception.transaction.TxnValidationException;
import com.logni.account.repository.account.AcLockRepository;
import com.logni.account.repository.account.AccountRepository;
import com.logni.account.repository.account.LowVolBalanceStateRepository;
import com.logni.account.repository.transaction.LedgerAcEntriesRepository;
import com.logni.account.repository.transaction.MemberAcEntriesRepository;
import com.logni.account.repository.transaction.TransactionRepository;
import com.logni.account.service.account.AccountService;
import com.logni.account.utils.AccountErrors;
import com.logni.account.utils.Constants;
import com.logni.account.validation.transaction.TxnAcTypeValidator;
import com.logni.account.validation.transaction.TxnValidator;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TxnServiceImpl implements TxnService{

    @Autowired
    TxnValidator txnValidator;

    @Autowired
    TxnAcTypeValidator txnAcTypeValidator;

    @Autowired
    AccountService accountService;

    @Autowired
    TxnTypeService txnTypeService;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    MemberAcEntriesRepository memberAcEntriesRepository;

    @Autowired
    LedgerAcEntriesRepository ledgerAcEntriesRepository;

    @Autowired
    AccountRepository accountRepository;


    @Autowired
    AcLockRepository acLockRepository;

    @Autowired
    LowVolBalanceStateRepository lowVolBalanceStateRepository;


    @Transactional
    public TxnResponse doJournalTxn(JournalRequest journalRequest){
        validateJournalRequest(journalRequest);

        Transactions transaction = generateTxn(journalRequest);

        journalRequest.getDebtors().forEach(txnLine -> {
            TransactionType txnType = txnTypeService.getTxnType(txnLine.getTxnType());
            txnTypeNotFoundCheck(txnType);
            Account from = accountRepository.findByIdentifier(txnLine.getAccountNumber());
            Account remoteAc = accountRepository.findByIdentifier(txnLine.getRemoteAccount());

            accountNotFoundCheck(from);
            if(from.getLedger().getType()==LedgerType.MEMBER){
                acLockRepository.findByAccountId(from.getId());
            }
            validateBalance(from,txnLine.getAmount());

            List<MemberAcEntries> fromMemberAcEntries = null;
            List<LedgerAcEntries> fromLedgerAcEntries = null;
            if(from.getLedger().getType()==LedgerType.MEMBER){
                fromMemberAcEntries= generateMemberAcEntries(from,remoteAc,txnType,txnLine.getAmount(),transaction,false);
            }
            if(from.getLedger().getType()==LedgerType.SYSTEM){
                fromLedgerAcEntries= generateLedgerAcEntries(from,remoteAc,txnType,txnLine.getAmount(),transaction,false);
            }

            if(fromLedgerAcEntries!=null){
                ledgerAcEntriesRepository.saveAll(fromLedgerAcEntries);
            }
            if(fromMemberAcEntries!=null){
                memberAcEntriesRepository.saveAll(fromMemberAcEntries);
                fromMemberAcEntries.forEach(memberAcEntries -> {updateAcBalance(memberAcEntries,transaction);});
            }


        });

        journalRequest.getCreditors().forEach(txnLine -> {
            TransactionType txnType = txnTypeService.getTxnType(txnLine.getTxnType());
            txnTypeNotFoundCheck(txnType);
            Account to = accountRepository.findByIdentifier(txnLine.getAccountNumber());
            accountNotFoundCheck(to);
            Account remoteAc = accountRepository.findByIdentifier(txnLine.getRemoteAccount());
            acLockRepository.findByAccountId(to.getId());
            List<MemberAcEntries> toMemberAcEntries = null;
            List<LedgerAcEntries> toLedgerAcEntries = null;

            if(to.getLedger().getType()==LedgerType.MEMBER){
                toMemberAcEntries= generateMemberAcEntries(to,remoteAc,txnType,txnLine.getAmount(),transaction,true);
            }
            if(to.getLedger().getType()==LedgerType.SYSTEM){
                toLedgerAcEntries= generateLedgerAcEntries(to,remoteAc,txnType,txnLine.getAmount(),transaction,true);
            }

            if(toLedgerAcEntries!=null){
                ledgerAcEntriesRepository.saveAll(toLedgerAcEntries);
            }
            if(toMemberAcEntries!=null){
                memberAcEntriesRepository.saveAll(toMemberAcEntries);
                toMemberAcEntries.forEach(memberAcEntries -> {updateAcBalance(memberAcEntries,transaction);});
            }
        });

        return new TxnResponse(transaction.getId(),Constants.STATUS_PROCESSED);

    }

    private Transactions generateTxn(JournalRequest journalRequest){

        Transactions transaction = new Transactions();
        transaction.setId(generateTxnId());
        transaction.setChannel(journalRequest.getChannel());
        transaction.setData(journalRequest.getData());
        transaction.setDescription((journalRequest.getDescription()!=null?journalRequest.getDescription():"")+
                (journalRequest.getNote()!=null?journalRequest.getNote():""));
        transaction.setNote(journalRequest.getNote());
        transaction.setReferenceId(journalRequest.getReferenceId());
        transaction.setTag(journalRequest.getTag());
        transaction.setTxnTime(Instant.now());

        transaction.setCreatedBy(journalRequest.getMaker()+"-"+journalRequest.getChecker());
        transaction.setCreatedOn(Instant.now());
        return transactionRepository.save(transaction);
    }


    private void validateJournalRequest(JournalRequest journalRequest){

        BigDecimal totalDebitAmount=BigDecimal.ZERO;
        BigDecimal totalCreditAmount=BigDecimal.ZERO;
        journalRequest.getDebtors().forEach(txnLine -> {
            totalDebitAmount.add(txnLine.getAmount());
        });
        journalRequest.getCreditors().forEach(txnLine -> {
            totalCreditAmount.add(txnLine.getAmount());
        });
        if(!totalCreditAmount.equals(totalDebitAmount)){
            throw  new CommonException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_TRANSACTION,AccountErrors.INVALID_AMOUNT),
                    AccountErrors.ERROR_MAP.get(AccountErrors.INVALID_AMOUNT));
        }

    }

    private void txnTypeNotFoundCheck(TransactionType txnType){
        if(txnType==null){
            throw  new CommonException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_TRANSACTION,AccountErrors.TXNTYPE_NOT_FOUND),
                    AccountErrors.ERROR_MAP.get(AccountErrors.TXNTYPE_NOT_FOUND));
        }
    }
    private void accountNotFoundCheck(Account account){
        if(account==null){
            throw  new CommonException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_MANAGEMENT,AccountErrors.ACCOUNT_NOT_FOUND),
                    AccountErrors.ERROR_MAP.get(AccountErrors.ACCOUNT_NOT_FOUND));
        }
    }

    @Transactional
    public TxnResponse doBulkTxn(BulkTxnRequest bulkTxnRequest){
        // First txn will be parent.
        List<TxnResponse> responseList= new ArrayList<>();
        TransactionType transactionType = txnTypeService.getTxnType(bulkTxnRequest.getTxnRequestList().get(0).getTransactionType());
        TxnRequest parentTxnRequest = bulkTxnRequest.getTxnRequestList().get(0);
        if(parentTxnRequest.getData()==null||"null".equals(parentTxnRequest.getData().toString())){
            parentTxnRequest.setData(null);
        }
        Transactions transaction = generateTxn(transactionType,parentTxnRequest);
        boolean isParent = true;
        for (TxnRequest txnRequest:bulkTxnRequest.getTxnRequestList()){
            responseList.add(doTxn(txnRequest,transaction,isParent));
            isParent=false;
        }

        return responseList.get(0);
    }

    @Transactional
    public TxnResponse doTxn(TxnRequest txnRequest){
        return doTxn( txnRequest,null,true);
    }



   // @Transactional
    public TxnResponse doTxn(TxnRequest txnRequest,Transactions transaction,Boolean isFromLock){
        log.info("Txn Request:{}",txnRequest);
        //validation
        txnValidator.validate(txnRequest);
        TransactionType transactionType = txnTypeService.getTxnType(txnRequest.getTransactionType());

        Account from = null;
        Account to = null;
        if(Constants.SYSTEM.equalsIgnoreCase(txnRequest.getFromAc())){
            from = transactionType.getFromType().getSystemAccount();
        }else {
            from = accountService.getAccountByIdentifier(txnRequest.getFromAc());
        }
        if(Constants.SYSTEM.equalsIgnoreCase(txnRequest.getToAc())){
            to = transactionType.getToType().getSystemAccount();
        }else {
            to = accountService.getAccountByIdentifier(txnRequest.getToAc());
        }

        transaction = performTxn(from,to,transactionType,txnRequest,txnRequest.getAmount(),transaction,isFromLock);
        // build response
        return buildTxnResponse( txnRequest,  from,  to, transaction);

    }

    private Transactions performTxn(Account from,Account to, TransactionType txnType,TxnRequest txnRequest,
                            BigDecimal amount,Transactions transaction,Boolean isFromLock){
        txnAcTypeValidator.validate(from,to,txnType);

        //lock MEMBER HIGH&LOW from AC and MEMBER LOW volume TO AC
        lockAccounts(from,to,isFromLock);
        validateBalance(from,amount);
        if(transaction==null)
            transaction = generateTxn(txnType,txnRequest);

        performLedgerEntries(from,to,txnType,amount,transaction);
        if(txnType.getTxnFeeList()!=null&&txnType.getTxnFeeList().size()>0){
            performSubTransaction(txnType.getTxnFeeList(),txnRequest,transaction,txnType);
        }
        return transaction;
    }

    private void performSubTransaction(List<TxnFee> txnFeeList,TxnRequest txnRequest,Transactions transaction,
                                        TransactionType parentTxnType){
        for (TxnFee txnFee:txnFeeList) {
             if(!txnFee.isEnabled())
                 continue;
             TransactionType genTxnType =    txnFee.getGeneratedTxnType();
             Account from=null;
             Account to=null;
            switch (txnFee.getPayer()){
                case SYSTEM:
                    if(genTxnType.getFromType().getSystemAccount()!=null){
                        from = genTxnType.getFromType().getSystemAccount();
                    }else {
                        throw new CommonException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_TRANSACTION,AccountErrors.INVALID_TXN_TYPE),
                                AccountErrors.ERROR_MAP.get(AccountErrors.INVALID_TXN_TYPE));
                    }
                    break;
                case SOURCE:
                    if(Constants.SYSTEM.equalsIgnoreCase(txnRequest.getFromAc())){
                        from = parentTxnType.getFromType().getSystemAccount();
                    }else {
                        from = accountRepository.findByIdentifier(txnRequest.getFromAc());
                    }

                    break;
                case DESTINATION:
                    if(Constants.SYSTEM.equalsIgnoreCase(txnRequest.getToAc())){
                        from = parentTxnType.getToType().getSystemAccount();
                    }else {
                        from = accountRepository.findByIdentifier(txnRequest.getToAc());
                    }

                    break;
                case FIXED_AC:
                    from = txnFee.getFromFixedAccount();
                    break;

            }

            switch (txnFee.getReceiver()){
                case SYSTEM:
                    if(genTxnType.getToType().getSystemAccount()!=null){
                        to = genTxnType.getToType().getSystemAccount();
                    }else {
                        throw new CommonException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_TRANSACTION,AccountErrors.INVALID_TXN_TYPE),
                                AccountErrors.ERROR_MAP.get(AccountErrors.INVALID_TXN_TYPE));
                    }
                    break;
                case SOURCE:
                    if(Constants.SYSTEM.equalsIgnoreCase(txnRequest.getToAc())){
                        to = parentTxnType.getToType().getSystemAccount();
                    }else {
                        to = accountRepository.findByIdentifier(txnRequest.getToAc());
                    }

                    break;
                case DESTINATION:
                    if(Constants.SYSTEM.equalsIgnoreCase(txnRequest.getToAc())){
                        to = parentTxnType.getToType().getSystemAccount();
                    }else {
                        to = accountRepository.findByIdentifier(txnRequest.getToAc());
                    }

                    break;
                case FIXED_AC:
                    to = txnFee.getToFixedAccount();
                    break;

            }
            BigDecimal subTxnAmount = calculateSubTxnAmount(txnFee,txnRequest.getAmount());

            if(from!=null&&to!=null)
              performTxn(from,to,txnFee.getGeneratedTxnType(),txnRequest,subTxnAmount,transaction,false);

        }
    }
    private BigDecimal calculateSubTxnAmount(TxnFee txnFee,BigDecimal amount){


        BigDecimal subTxnAmount= BigDecimal.ZERO;
        switch (txnFee.getChargeType()){
            case FIXED:
                subTxnAmount=  txnFee.getFixedAmount();
                break;
            case PERCENTAGE:
                subTxnAmount = amount.multiply(txnFee.getPercentage());

                if(txnFee.getMaxPercentageAmount()!=null&&subTxnAmount.compareTo(txnFee.getMaxPercentageAmount())>0)
                {
                    subTxnAmount = txnFee.getMaxPercentageAmount();
                }
                if(txnFee.getMinPercentageAmount()!=null&&subTxnAmount.compareTo(txnFee.getMaxPercentageAmount())<0)
                {
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

        if(Constants.minimumTxnAmount.compareTo(subTxnAmount)>0)
            throw new CommonException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_TRANSACTION,
                    AccountErrors.MINIMUM_AMOUNT_EXCEEDED),AccountErrors.ERROR_MAP.get(AccountErrors.MINIMUM_AMOUNT_EXCEEDED));

        return subTxnAmount;
    }

    private void lockAccounts(Account from,Account to,Boolean isFromLock){
            acLockRepository.findByAccountId(from.getId());
            acLockRepository.findByAccountId(to.getId());
    }

    public void performLedgerEntries(Account from,Account to, TransactionType type, BigDecimal amount,Transactions transaction){

        List<MemberAcEntries> fromMemberAcEntries = null;
        List<LedgerAcEntries> fromLedgerAcEntries = null;
        List<MemberAcEntries> toMemberAcEntries = null;
        List<LedgerAcEntries> toLedgerAcEntries = null;
        if(from.getLedger().getType()==LedgerType.MEMBER){
            fromMemberAcEntries =  generateMemberAcEntries(from,to,type,amount,transaction,false);
        }else {
            fromLedgerAcEntries =  generateLedgerAcEntries(from,to,type,amount,transaction,false);
        }
        if(to.getLedger().getType()==LedgerType.MEMBER){
            toMemberAcEntries =  generateMemberAcEntries(to,from,type,amount,transaction,true);
        }else {
            toLedgerAcEntries =  generateLedgerAcEntries(to,from,type,amount,transaction,true);
        }

        if(fromMemberAcEntries!=null){
            fromMemberAcEntries = memberAcEntriesRepository.saveAll(fromMemberAcEntries);
            fromMemberAcEntries.forEach(memberAcEntry -> {updateAcBalance(memberAcEntry,transaction);});
        }
        if(toMemberAcEntries!=null){
            toMemberAcEntries= memberAcEntriesRepository.saveAll(toMemberAcEntries);
            toMemberAcEntries.forEach(memberAcEntry -> {updateAcBalance(memberAcEntry,transaction);});
        }
        if(fromLedgerAcEntries!=null){
            fromLedgerAcEntries = ledgerAcEntriesRepository.saveAll(fromLedgerAcEntries);
            fromLedgerAcEntries.forEach(ledgerAcEntry -> {updateLedgerAcBalance(ledgerAcEntry,transaction);});
        }
        if(toLedgerAcEntries!=null){
            toLedgerAcEntries = ledgerAcEntriesRepository.saveAll(toLedgerAcEntries);
            toLedgerAcEntries.forEach(ledgerAcEntry->{updateLedgerAcBalance(ledgerAcEntry,transaction);});
        }

    }

    private void updateAcBalance(MemberAcEntries memberAcEntry,Transactions transaction){
        Account account = memberAcEntry.getAccount();
            BigDecimal balance = account.getBalance().add(memberAcEntry.getAmount());
            account.setBalance(balance);
            account.setLastModifiedDate(Instant.now());
            accountRepository.save(account);
            saveAcBalanceState(account,balance,transaction);
    }

    private void updateLedgerAcBalance(LedgerAcEntries ledgerAcEntry,Transactions transaction){
        Account account = ledgerAcEntry.getAccount();
        BigDecimal balance = account.getBalance().add(ledgerAcEntry.getAmount());
        account.setBalance(balance);
        account.setLastModifiedDate(Instant.now());
        accountRepository.save(account);
        saveAcBalanceState(account,balance,transaction);
    }

    private void saveAcBalanceState(Account account,BigDecimal balance, Transactions transactions){
        AcBalanceState acBalanceState = new AcBalanceState();
        acBalanceState.setAccount(account);
        acBalanceState.setBalance(balance);
        acBalanceState.setBalanceAt(Instant.now());
        acBalanceState.setTransaction(transactions);
        lowVolBalanceStateRepository.save(acBalanceState);
    }

    private Transactions generateTxn( TransactionType type, TxnRequest txnRequest){

        Transactions transaction = new Transactions();
        transaction.setId(generateTxnId());
        transaction.setChannel(txnRequest.getChannel());
        transaction.setData(txnRequest.getData());
        transaction.setDescription(type.getName()+"From:"+txnRequest.getFromAc()+"|TO:"+txnRequest.getToAc()+"."
                +(txnRequest.getDescription()!=null?txnRequest.getDescription():""));
        transaction.setNote(txnRequest.getNote());
        transaction.setReferenceId(txnRequest.getReferenceId());
        transaction.setTag(txnRequest.getTag());
        transaction.setTxnTime(Instant.now());

        transaction.setCreatedBy(txnRequest.getMaker()+"-"+txnRequest.getChecker());
        transaction.setCreatedOn(Instant.now());
        return transactionRepository.save(transaction);
    }

    private List<MemberAcEntries> generateMemberAcEntries(Account account, Account remoteAc, TransactionType type,
                                                          BigDecimal amount,Transactions transaction,boolean isCredit){

        MemberAcEntries memberAcEntries = new MemberAcEntries();
        memberAcEntries.setTxnTime(transaction.getTxnTime());
        memberAcEntries.setAmount(isCredit?amount:amount.negate());
        memberAcEntries.setAccount(account);
        memberAcEntries.setRemoteAccount(remoteAc);
        memberAcEntries.setTransaction(transaction);
        memberAcEntries.setTxnType(type);

        List<MemberAcEntries> memberAcEntriesList = new ArrayList<>();
        memberAcEntriesList.add(memberAcEntries);
        return memberAcEntriesList;
    }
    private List<LedgerAcEntries> generateLedgerAcEntries(Account account, Account remoteAc, TransactionType type,
                                                          BigDecimal amount,Transactions transaction,boolean isCredit){
        LedgerAcEntries ledgerAcEntries = new LedgerAcEntries();
        ledgerAcEntries.setTxnTime(transaction.getTxnTime());

        ledgerAcEntries.setAmount(isCredit?amount:amount.negate());
        ledgerAcEntries.setAccount(account);
        ledgerAcEntries.setRemoteAccount(remoteAc);
        ledgerAcEntries.setTransaction(transaction);
        ledgerAcEntries.setTxnType(type);
        List<LedgerAcEntries> ledgerAcEntriesList = new ArrayList<>();
        ledgerAcEntriesList.add(ledgerAcEntries);
        return ledgerAcEntriesList;
    }

    private void validateBalance(Account from,BigDecimal amount){
        if(from.getLedger().getType()== LedgerType.MEMBER){
            BigDecimal lowCredit = from.getLowerLimit();
            BigDecimal fromBalance = null;
            if(lowCredit==null)
                lowCredit = BigDecimal.ZERO;
            fromBalance = from.getBalance();
            if(fromBalance.subtract(amount).compareTo(lowCredit)<0){
                throw new TxnValidationException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_TRANSACTION,
                        AccountErrors.NOT_ENOUGH_BALANCE),AccountErrors.ERROR_MAP.get(AccountErrors.NOT_ENOUGH_BALANCE));
            }
        }

    }
    private String generateTxnId(){
        //todo pre generated txn ID
       return UUID.randomUUID().toString().replace("-","").toUpperCase().substring(0,10);
    }

    private TxnResponse buildTxnResponse(TxnRequest request, Account from, Account to,Transactions transaction){
        TxnResponse txnResponse = new TxnResponse();
        txnResponse.setStatus(Constants.STATUS_PROCESSED);
        txnResponse.setFromAc(from.getIdentifier());
        txnResponse.setToAc(to.getIdentifier());
        txnResponse.setFromName(from.getName());
        txnResponse.setToName(to.getName());
        if(from.getLedger().getType()==LedgerType.MEMBER)
            txnResponse.setFromBalance(from.getBalance());
        if(to.getLedger().getType()==LedgerType.MEMBER)
            txnResponse.setToBalance(to.getBalance());
        txnResponse.setTxnId(transaction.getId());
        return txnResponse;

    }

    @Transactional(readOnly = true)
    public TxnDetail getTxnDetail(String txnId){
        Optional<Transactions> transactionsOptional = transactionRepository.findById(txnId);
        transactionsOptional.orElseThrow(()->new CommonException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_TRANSACTION,AccountErrors.TRANSACTION_NOT_FOUND),
        String.format(AccountErrors.ERROR_MAP.get(AccountErrors.TRANSACTION_NOT_FOUND),txnId)));
        List<MemberAcEntries> memberEntries = memberAcEntriesRepository.findAllByTransaction(transactionsOptional.get());
        BigDecimal sum = memberEntries.stream().map(MemberAcEntries::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<LedgerAcEntries> ledgerAcEntries = null;
        if(BigDecimal.ZERO.compareTo(sum)!=0){
            ledgerAcEntries = ledgerAcEntriesRepository.findAllByTransaction(transactionsOptional.get());
        }
        TxnDetail txnDetail = new TxnDetail() ;
        BeanUtils.copyProperties(transactionsOptional.get(),txnDetail); //MODEL_MAPPER.map(transactionsOptional.get(),TxnDetail.class);
        List<StmtTxn> stmtTxnList = memberEntries.stream()
              .map(me->accountService.adaptStatement(me)).collect(Collectors.toList());
        if(ledgerAcEntries!=null&&!ledgerAcEntries.isEmpty()){
            stmtTxnList.addAll(ledgerAcEntries.stream().map(le->accountService.adaptStatement(le)).collect(Collectors.toList()));
        }
        txnDetail.setLedgerEntries(stmtTxnList);
        return txnDetail;
    }

    @Transactional(rollbackFor = Exception.class)
    public TxnResponse doReverseTxn(BaseReverseRequest reverseRequest){
        Optional<Transactions> transactionsOptional = transactionRepository.findById(reverseRequest.getTxnId());
        transactionsOptional.orElseThrow(()->new CommonException(AccountErrors.getErrorCode(AccountErrors.ACCOUNT_TRANSACTION,AccountErrors.TRANSACTION_NOT_FOUND),
              String.format(AccountErrors.ERROR_MAP.get(AccountErrors.TRANSACTION_NOT_FOUND),reverseRequest.getTxnId())));

        List<MemberAcEntries> memberEntries = memberAcEntriesRepository.findAllByTransaction(transactionsOptional.get());
        BigDecimal sum = memberEntries.stream().map(MemberAcEntries::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<LedgerAcEntries> ledgerAcEntries = null;
        if(BigDecimal.ZERO.compareTo(sum)!=0){
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
            lockAcForReverse(account);
            if(debit){
                validateBalance(account,rMemberEntry.getAmount());
            }
            updateAcBalance(rMemberEntry,reversTransaction);
        }
        TxnResponse txnResponse = new TxnResponse();
        txnResponse.setStatus(Constants.STATUS_PROCESSED);
        txnResponse.setTxnId(reversTransaction.getId());
        return txnResponse;
    }

    private void lockAcForReverse(Account account) {
        acLockRepository.findByAccountId(account.getId());
    }

    private void extractedMemberEntries(List<MemberAcEntries> memberEntries, Transactions reversTransaction, List<MemberAcEntries> rMemberEntries) {
        if(memberEntries !=null&&!memberEntries.isEmpty()){
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

    private void extractedLedgerEntries(List<LedgerAcEntries> ledgerAcEntries, Transactions reversTransaction, List<LedgerAcEntries> rLedgerAcEntries) {
        if(ledgerAcEntries !=null&&!ledgerAcEntries.isEmpty()){
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
        reversTransaction.setId(this.generateTxnId());
        reversTransaction.setReferenceId(reverseRequest.getTxnId());
        return reversTransaction;
    }

    
}
