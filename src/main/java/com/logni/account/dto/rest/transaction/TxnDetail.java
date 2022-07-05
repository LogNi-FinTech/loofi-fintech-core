package com.logni.account.dto.rest.transaction;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.logni.account.dto.rest.account.StmtTxn;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TxnDetail {

   private String id;
   private Instant txnTime; // time partition
   private String description;
   private String note;
   private String referenceId;
   private String channel;
   private String tag;
   private JsonNode data;

   List<StmtTxn> ledgerEntries;
}
