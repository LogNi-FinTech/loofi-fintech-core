package com.logni.account.service.transaction;

import com.logni.account.dto.rest.transaction.BaseReverseRequest;
import com.logni.account.dto.rest.transaction.BulkTxnRequest;
import com.logni.account.dto.rest.transaction.JournalRequest;
import com.logni.account.dto.rest.transaction.TxnDetail;
import com.logni.account.dto.rest.transaction.TxnRequest;
import com.logni.account.dto.rest.transaction.TxnResponse;

public interface TxnService {
    TxnResponse doTxn(TxnRequest txnRequest);
    TxnResponse doBulkTxn(BulkTxnRequest bulkTxnRequest);
    TxnResponse doJournalTxn(JournalRequest journalRequest);
    TxnDetail getTxnDetail(String txnId);
    TxnResponse doReverseTxn(BaseReverseRequest reverseRequest);
}
