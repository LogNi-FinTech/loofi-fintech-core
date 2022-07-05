package com.logni.account.controller;

import com.logni.account.dto.rest.transaction.BaseReverseRequest;
import com.logni.account.dto.rest.transaction.BulkTxnRequest;
import com.logni.account.dto.rest.transaction.JournalRequest;
import com.logni.account.dto.rest.transaction.TxnDetail;
import com.logni.account.dto.rest.transaction.TxnRequest;
import com.logni.account.dto.rest.transaction.TxnResponse;
import com.logni.account.entities.transactions.Transactions;
import com.logni.account.service.transaction.TxnService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class TransactionController {

    @Autowired
    TxnService txnService;

    @Operation(summary = "Perform all type of transaction and refund transaction", description = "", tags={ "transaction" })
    @PostMapping(path = "/txn")
    ResponseEntity<TxnResponse> doTxn(@RequestBody @Valid TxnRequest txnRequest){
        log.info("Req: {} TO: {} Amount: {}| Type:{}",txnRequest.getToAc(),txnRequest.getAmount(),txnRequest.getTransactionType().getTxnCode(),txnRequest.getFromAc());
        long s = System.currentTimeMillis();
        TxnResponse txnResponse = txnService.doTxn(txnRequest);
        log.info("Response: Status:{} Latency:{}  ms", txnResponse.getStatus(),(System.currentTimeMillis()-s));
        return ResponseEntity.ok(txnResponse);
    }

    @Operation(summary = "Perform bulk transaction", description = "", tags={ "transaction" })
    @PostMapping(path = "/bulk/txn")
    ResponseEntity<TxnResponse> doBulkTxn(@RequestBody @Valid BulkTxnRequest bulkTxnRequest){
        log.info("Bulk Txn Req: ");
        long s = System.currentTimeMillis();
        TxnResponse txnResponse = txnService.doBulkTxn(bulkTxnRequest);
        log.info("Response: Status:{} Latency:{}  ms", txnResponse.getStatus(),(System.currentTimeMillis()-s));
        return ResponseEntity.ok(txnResponse);
    }

    @Operation(summary = "Perform Journal Entries", description = "", tags={ "transaction" })
    @PostMapping(path = "/journal")
    ResponseEntity<TxnResponse> doBulkJournal(@RequestBody @Valid JournalRequest journalRequest){
        log.info("Journal Request");
        long s = System.currentTimeMillis();
        TxnResponse txnResponse = txnService.doJournalTxn(journalRequest);
        log.info("Response: Status:{} Latency:{}  ms", txnResponse.getStatus(),(System.currentTimeMillis()-s));
        return ResponseEntity.ok(txnResponse);
    }

    //todo get txn Details(for Txn id)
    @Operation(summary = "Transaction detail", description = "", tags={ "transaction" })
    @GetMapping(path = "/txn/{txnId}")
    ResponseEntity<TxnDetail> txnDetail(@PathVariable("txnId") String txnId){
         return ResponseEntity.ok(txnService.getTxnDetail(txnId));
    }


    @Operation(summary = "Txn Reverse By BackOffice User", description = "", tags={ "transaction" })
    @PostMapping(path = "/reverse")
    ResponseEntity<TxnResponse> txnReverse(@RequestBody @Valid BaseReverseRequest reverseRequest){
        TxnResponse txnResponse = txnService.doReverseTxn(reverseRequest);
        return ResponseEntity.ok(txnResponse);
    }
// todo internal reverse auto
//    @Operation(summary = "Txn Auto Reverse", description = "", tags={ "transaction" })
//    @PostMapping(path = "/reverse/auto")
//    ResponseEntity<TxnResponse> reverseAuto(@RequestBody @Valid TxnRequest txnRequest){
//
//    }

}