package com.logni.account.controller;

import com.logni.account.dto.rest.account.AcBalance;
import com.logni.account.dto.rest.account.LedgerBalanceDto;
import com.logni.account.dto.rest.account.StmtTxn;
import com.logni.account.entities.accounts.Ledger;
import com.logni.account.enums.LedgerType;
import com.logni.account.repository.account.AccountRepository;
import com.logni.account.repository.account.LedgerRepository;
import com.logni.account.service.account.AccountService;
import com.logni.account.service.account.LedgerService;
import com.logni.account.utils.AcDateTimeUtil;
import io.swagger.v3.oas.annotations.Operation;

import java.time.Instant;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/v1/ledger")
public class LedgerController {

    @Autowired
    LedgerRepository ledgerRepository;

    @Autowired
    LedgerService ledgerService;

    @Autowired
    AccountService accountService;

    @Operation(summary = "Create Ledger.", description = "", tags={ "ledger" })
    @PostMapping
    public ResponseEntity<Ledger> createLedger(@RequestBody @Valid Ledger ledger){
        Ledger ledgerResp = ledgerService.createLedger(ledger);
        return  ResponseEntity.ok().body(ledgerResp);
    }

    @Transactional(readOnly = true)
    @Operation(summary = "Get Ledger Info.", description = "", tags={ "ledger" })
    @GetMapping("/{id}")
    public ResponseEntity<Ledger> getLedger(@PathVariable("id") Long id){

        Optional<Ledger> ledgerOptional = ledgerRepository.findById(id);
        if(ledgerOptional.isPresent()){
            return ResponseEntity.ok(ledgerOptional.get());
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get Ledger Info.", description = "", tags={ "ledger" })
    @GetMapping("/code/{code}")
    public ResponseEntity<Ledger> getLedgerByCode(@PathVariable("code") String code){
        return ResponseEntity.ok().body(ledgerRepository.findByLedgerCode(code));
    }
    @Operation(summary = "Ledger List.", description = "", tags={ "ledger" })
    @GetMapping
    public ResponseEntity<Page<Ledger>> ledgers(Pageable pageable){
        return ResponseEntity.ok().body(ledgerRepository.findAll(pageable));
    }
    
    @Operation(summary = "Ledger List By Type", description = "", tags={ "ledger ByType" })
    @GetMapping("/byType/{type}")
    public ResponseEntity<List<Ledger>> ledgerByType(@PathVariable("type") LedgerType type ){
        return ResponseEntity.ok().body(ledgerRepository.findAllByType(type));
    }

    @Transactional
    @Operation(summary = "Update Ledger .", description = "", tags={ "ledger" })
    @PutMapping
    public ResponseEntity updateLedger(@RequestBody @Valid Ledger ledger){
        ledgerRepository.save(ledger);
        return  ResponseEntity.ok().body("");
    }


    @Operation(summary = "Ledger Balance.", description = "", tags={ "ledger" })
    @GetMapping("/balance/{ledgerCode}")
    public ResponseEntity<AcBalance> getLedgerBalance(@PathVariable("ledgerCode") String ledgerCode){
        log.debug("Ledger Balance.Identifier:{}",ledgerCode);
        long s = System.currentTimeMillis();
        AcBalance acBalance = accountService.getLedgerBalance(ledgerCode);
        log.info("Ledger Balance Response.Latency: {} ms",(System.currentTimeMillis()-s));
        return ResponseEntity.ok(acBalance);

    }

    @Operation(summary = "Ledger Statement.", description = "", tags={ "ledger" })
    @GetMapping("/statement")
    public ResponseEntity<Page<StmtTxn>> getAccountStatement(@PathParam("ledgerCode") String ledgerCode,
                                                             Pageable pageable){
        return ResponseEntity.ok(accountService.ledgerStatement(ledgerCode,pageable));
    }


    @Operation(summary = "All Ledger balance.", description = "", tags={ "ledger" })
    @GetMapping("/all/balance")
    public ResponseEntity<List<LedgerBalanceDto>> allLedgersBalance(@RequestParam(value = "time",required = false)Instant time){
        if(time==null)
            time = Instant.now();
        return ResponseEntity.ok().body(ledgerService.getAllLedgerBalance(time));
    }

    @Operation(summary = "All Ledger balance.", description = "", tags={ "ledger" })
    @GetMapping("/all/open/balance")
    public ResponseEntity<List<LedgerBalanceDto>> allLedgersOpenBalance(@RequestParam(value = "openDate",required = true)String openDate){
        Instant time = AcDateTimeUtil.convertStringToGMT(openDate);
        log.info("Get All Opening Balance. Date:{}, UTC Time: {}",openDate,time);

        if(time==null||time.isAfter(Instant.now()))
            return ResponseEntity.badRequest().build();
        else
            return ResponseEntity.ok().body(ledgerService.getAllLedgerBalance(time));
    }

    @Operation(summary = "All System Ledger balance.", description = "", tags={ "ledger" })
    @GetMapping("/system/balance")
    public ResponseEntity<List<LedgerBalanceDto>> systemLedgersBalance(@RequestParam(value = "time",required = false)Instant time){
        if(time==null)
            time = Instant.now();
        return ResponseEntity.ok().body(ledgerService.getSystemLedgerBalance(time));
    }
    @Operation(summary = "All member Ledger balance.", description = "", tags={ "ledger" })
    @GetMapping("/member/balance")
    public ResponseEntity<List<LedgerBalanceDto>> memberLedgersBalance(@RequestParam(value = "time",required = false)Instant time){
        if(time==null)
            time = Instant.now();
        return ResponseEntity.ok().body(ledgerService.getMemberLedgerBalance(time));
    }


}
