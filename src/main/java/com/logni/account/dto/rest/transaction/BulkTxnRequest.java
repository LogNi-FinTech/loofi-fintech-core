package com.logni.account.dto.rest.transaction;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class BulkTxnRequest {

    List<TxnRequest> txnRequestList;
}
