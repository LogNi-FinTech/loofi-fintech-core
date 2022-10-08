package com.logni.account.service.account;

import com.logni.account.dto.rest.account.LedgerBalanceDto;
import com.logni.account.entities.accounts.Account;
import com.logni.account.entities.accounts.AccountBalanceState;
import com.logni.account.entities.accounts.Ledger;
import com.logni.account.entities.accounts.LedgerBalanceArchive;
import com.logni.account.entities.accounts.MemberLedgerBalanceState;
import com.logni.account.entities.transactions.MemberAcEntries;
import com.logni.account.enums.LedgerType;
import com.logni.account.repository.account.AcBalanceStateRepository;
import com.logni.account.repository.account.AccountRepository;
import com.logni.account.repository.account.LedgerBalanceArchiveRepo;
import com.logni.account.repository.account.LedgerRepository;
import com.logni.account.repository.account.MemberLedgerBalanceStateRepo;
import com.logni.account.repository.transaction.LedgerAcEntriesRepository;
import com.logni.account.repository.transaction.MemberAcEntriesRepository;
import com.logni.account.utils.AcDateTimeUtil;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class EodServiceImpl implements EodService {

    public static Integer BATCH_SIZE= 10;

    @Autowired
    LedgerService ledgerService;

    @Autowired
    LedgerRepository ledgerRepository;

    @Autowired
    MemberAcEntriesRepository memberAcEntriesRepository;


    @Autowired
    MemberLedgerBalanceStateRepo memberLedgerBalanceStateRepo;
    @Autowired
    AcBalanceStateRepository acBalanceStateRepository;

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    LedgerBalanceArchiveRepo ledgerBalanceArchiveRepo;

    public void closeBalanceCalculation(Instant closeTime){
        calculateBalanceForSystemAc(closeTime);
        calculateForAllMemberWallet(closeTime);
    }

    @Transactional
    public void calculateBalanceForSystemAc(Instant closeTime){
        List<LedgerBalanceDto> ledgerBalanceDtos= ledgerService.getSystemLedgerBalance(closeTime);
        ledgerBalanceDtos.forEach(ledgerBalanceDto -> {
            if(!ledgerBalanceDto.getOnlyParent()){
                Ledger ledger = ledgerRepository.findByLedgerCode(ledgerBalanceDto.getLedgerCode());
                AccountBalanceState accountBalanceState = new AccountBalanceState();
                accountBalanceState.setBalance(ledgerBalanceDto.getBalance());
                accountBalanceState.setAccount(ledger.getSystemAccount());
                accountBalanceState.setBalanceAt(closeTime);
                accountBalanceState.setCreatedOn(Instant.now());
                acBalanceStateRepository.save(accountBalanceState);
            }

        });
    }

    @Transactional
    public void calculateForAllMemberWallet(Instant closeTime){
        Instant yesterDayEndTime = AcDateTimeUtil.getLastDateEndTime();
        Instant yesterDayStartTime = yesterDayEndTime.minus(1, ChronoUnit.DAYS);
        List<Account> allMemberAcs= memberAcEntriesRepository.getAccountsWhoDidTxnLastDay(yesterDayStartTime,yesterDayEndTime);
        calculateCloseBalance(allMemberAcs,closeTime);
    }

    public void calculateAllCloseBalance(Instant closeTime){
        calculateBalanceForSystemAc(closeTime);
        List<Account> allMemberAcs = accountRepository.findAllMemberAc(LedgerType.MEMBER);
        calculateCloseBalance(allMemberAcs,closeTime);
    }

    public void calculateCloseBalance(List<Account> accounts,Instant closeTime){
        List<AccountBalanceState> balanceStateList = new ArrayList<>();
        int counter=0;
        // todo need to do it multi thread executor
        for (Account account:accounts){

            AccountBalanceState balanceState = acBalanceStateRepository.findTopByAccountAndBalanceAtLessThanOrderByBalanceAtDesc(account,closeTime);
            BigDecimal balanceDiff;
            if(balanceState==null){
                balanceDiff= memberAcEntriesRepository.getBalanceSumAcUptoTime(account,closeTime);
            }else {
                balanceDiff= memberAcEntriesRepository.getBalanceSumAcFromToUptoTime(account,balanceState.getBalanceAt(),closeTime);
            }

            BigDecimal closeBalance = (balanceState!=null&&balanceState.getBalance()!=null)?balanceState.getBalance():BigDecimal.ZERO;
            balanceDiff = (balanceDiff!=null)?balanceDiff:BigDecimal.ZERO;
            closeBalance = closeBalance.add(balanceDiff);
            AccountBalanceState closeBalanceState = new AccountBalanceState();
            closeBalanceState.setCreatedOn(Instant.now());
            closeBalanceState.setAccount(account);
            closeBalanceState.setBalanceAt(closeTime);
            closeBalanceState.setBalance(closeBalance);
            log.info("Calculating Closing balance. AC:{}, Balance:{}, At: {}",account.getIdentifier(),closeBalance,closeTime);
            if(counter>=BATCH_SIZE){
                log.info("Inserting Batch: Size:{}",counter);
                acBalanceStateRepository.saveAllAndFlush(balanceStateList);
                counter=0;
                balanceStateList.clear();

            }else {
                counter++;
                balanceStateList.add(closeBalanceState);
            }


        };
        if(counter>0&&balanceStateList.size()>0){
            acBalanceStateRepository.saveAllAndFlush(balanceStateList);
        }
    }


    @Transactional
    public void calculateCloseBalanceForMemberLedger(Instant closeTime){
        List<LedgerBalanceDto> balanceDtoList= ledgerService.getMemberLedgerBalance(closeTime);

        balanceDtoList.forEach(ledgerBalanceDto -> {
            MemberLedgerBalanceState memberLedgerBalanceState = new MemberLedgerBalanceState();
            memberLedgerBalanceState.setBalance(ledgerBalanceDto.getBalance());
            memberLedgerBalanceState.setBalanceAt(closeTime);
            memberLedgerBalanceState.setCreatedOn(Instant.now());
            Ledger memberLedger = ledgerRepository.findByLedgerCode(ledgerBalanceDto.getLedgerCode());
            memberLedgerBalanceState.setLedger(memberLedger);
            memberLedgerBalanceStateRepo.save(memberLedgerBalanceState);

        });

    }

}
