package com.logni.account.controller;

import com.logni.account.service.account.EodService;
import com.logni.account.utils.AcDateTimeUtil;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/eod")
public class EodController {

    @Autowired
    EodService eodService;

    @PostMapping("/close/balance")
    public ResponseEntity dayEndBalanceClose(){

       Instant closeTime =  AcDateTimeUtil.getToDayUTCOpenTime();
       log.info("Current Local:{}, UTC:{}",LocalDateTime.now(),Instant.now());
       log.info("Close Balance Calculation:Closing Time: {}", closeTime);
        eodService.closeBalanceCalculation(closeTime);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/close/all/balance")
    public ResponseEntity dayEndAllBalanceClose(){
        log.info("Current Local:{}, UTC:{}",LocalDateTime.now(),Instant.now());
        Instant closeTime =  AcDateTimeUtil.getToDayUTCOpenTime();

        log.info("Close Balance Calculation:Closing Time: {}", closeTime);
        eodService.calculateAllCloseBalance(closeTime);
        return ResponseEntity.ok().build();

    }


    @PostMapping("/close/member/ledger")
    public ResponseEntity dayEndMemberLedgerBalanceClose(){
        log.info("EOD Service for member ledger close. For Time: {}",AcDateTimeUtil.getLastDateEndTime());
        eodService.calculateCloseBalanceForMemberLedger(AcDateTimeUtil.getToDayUTCOpenTime());
        return ResponseEntity.ok().build();
    }


}
