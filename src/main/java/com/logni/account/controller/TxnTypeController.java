package com.logni.account.controller;


import com.logni.account.config.UserData;
import com.logni.account.entities.transactions.TransactionType;
import com.logni.account.entities.transactions.TxnFee;
import com.logni.account.repository.transaction.TxnFeeRepository;
import com.logni.account.repository.transaction.TxnTypeRepository;
import java.util.Optional;
import javax.annotation.Resource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TxnTypeController {
  private final TxnTypeRepository txnTypeRepository;
  private final TxnFeeRepository txnFeeRepository;

  @Resource(name = "requestScopeTokenData")
  private UserData userData;

  @PostMapping("/v1/txn/type")
  @Transactional
  public ResponseEntity createTxnType(@RequestBody @Valid TransactionType transactionType) {
    if (userData.getUserId() == null) {
      transactionType.setCreatedBy("REST");
    } else {
      transactionType.setCreatedBy(userData.getUserId());
    }
    txnTypeRepository.save(transactionType);
    return ResponseEntity.ok().body("");
  }


  @GetMapping("/v1/txn/type")
  @Transactional(readOnly = true)
  public ResponseEntity<Page<TransactionType>> txnTypeList(Pageable pageable) {
    return ResponseEntity.ok().body(txnTypeRepository.findAll(pageable));
  }

  @GetMapping("/v1/txn/type/{id}")
  @Transactional(readOnly = true)
  public ResponseEntity<TransactionType> getTypeById(@PathVariable("id") Long id) {
    Optional<TransactionType> typeOp = txnTypeRepository.findById(id);
    if (typeOp.isPresent()) {
      typeOp.get().setFromType(typeOp.get().getFromType());
      typeOp.get().setToType(typeOp.get().getToType());
      return ResponseEntity.ok(typeOp.get());
    } else return ResponseEntity.notFound().build();
  }

  @GetMapping("/v1/txn/type/byCode/{code}")
  @Transactional(readOnly = true)
  public ResponseEntity<TransactionType> getTypeByCode(@PathVariable("code") Integer code) {
    TransactionType type = txnTypeRepository.findByTxnCode(code);
    if (type != null) {
      return ResponseEntity.ok(type);
    } else return ResponseEntity.notFound().build();
  }

  @PutMapping("/v1/txn/type")
  @Transactional
  public ResponseEntity updateTxnType(@RequestBody @Valid TransactionType transactionType) {
    txnTypeRepository.save(transactionType);
    return ResponseEntity.ok().body("");
  }

  @PostMapping("/v1/txn/type/sub")
  @Transactional
  public ResponseEntity createTxnFee(@RequestBody @Valid TxnFee txnFee) {
    txnFee.setCreatedBy("REST");
    txnFeeRepository.save(txnFee);
    return ResponseEntity.ok().body("");
  }

  @GetMapping("/v1/txn/type/sub")
  @Transactional(readOnly = true)
  ResponseEntity<Page<TxnFee>> feeList(Pageable pageable) {
    return ResponseEntity.ok().body(txnFeeRepository.findAll(pageable));
  }

  @PutMapping("/v1/txn/type/sub")
  @Transactional
  public ResponseEntity updateFee(@RequestBody @Valid TxnFee txnFee) {
    txnFeeRepository.save(txnFee);
    return ResponseEntity.ok().body("");
  }

}

