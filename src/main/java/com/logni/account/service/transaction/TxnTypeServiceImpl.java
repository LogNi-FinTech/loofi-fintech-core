package com.logni.account.service.transaction;

import com.logni.account.entities.transactions.TransactionType;
import com.logni.account.repository.transaction.TxnTypeRepository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TxnTypeServiceImpl implements TxnTypeService {

  @Autowired
  TxnTypeRepository txnTypeRepository;

  public TransactionType getTxnType(TransactionType transactionType) {
    //todo cache here
    if (transactionType.getId() != null && transactionType.getId() > 0) {
      return txnTypeRepository.findById(transactionType.getId()).orElse(null);
    } else if (transactionType.getTxnCode() > 0) {
      return txnTypeRepository.findByTxnCode(transactionType.getTxnCode());
    } else if (!StringUtils.isBlank(transactionType.getName())) {
      return txnTypeRepository.findByName(transactionType.getName());
    } else {
      throw new RuntimeException("Invalid Request. No Txn Type");
    }
  }
}
