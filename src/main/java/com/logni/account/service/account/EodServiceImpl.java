package com.logni.account.service.account;

import com.logni.account.dto.rest.account.LedgerBalanceDto;
import com.logni.account.entities.accounts.Account;
import com.logni.account.entities.accounts.Ledger;
import com.logni.account.entities.accounts.MemberLedgerBalanceState;
import com.logni.account.enums.LedgerType;
import com.logni.account.repository.account.AccountRepository;
import com.logni.account.repository.account.LedgerRepository;
import com.logni.account.repository.account.MemberLedgerBalanceStateRepo;
import com.logni.account.repository.transaction.MemberAcEntriesRepository;
import com.logni.account.utils.AcDateTimeUtil;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
    AccountRepository accountRepository;

    public void closeBalanceCalculation(Instant closeTime){
        calculateBalanceForSystemAc(closeTime);
        calculateForAllMemberWallet(closeTime);
    }

    @Transactional
    public void calculateBalanceForSystemAc(Instant closeTime){

    }

    @Transactional
    public void calculateForAllMemberWallet(Instant closeTime){
        Instant yesterDayEndTime = AcDateTimeUtil.getLastDateEndTime();
        Instant yesterDayStartTime = yesterDayEndTime.minus(1, ChronoUnit.DAYS);
        List<Account> allMemberAcs= memberAcEntriesRepository.getAccountsWhoDidTxnLastDay(yesterDayStartTime,yesterDayEndTime);
        calculateCloseBalance(allMemberAcs,closeTime);
    }

    public void calculateAllCloseBalance(Instant closeTime){
        List<Account> allMemberAcs = accountRepository.findAllMemberAc(LedgerType.MEMBER);
        calculateCloseBalance(allMemberAcs,closeTime);
    }

    public void calculateCloseBalance(List<Account> accounts,Instant closeTime){

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
