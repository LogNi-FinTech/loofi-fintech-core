package com.logni.account.dto.rest.transaction;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class JournalRequest {

  @NotNull(message = "Channel Required")
  private String channel; // WEB,Android-Mobile,IOS-Mobile,USSD,GW,ATM,API

  private String description;
  private String note;
  private String referenceId;
  private JsonNode data;

  private String tag;
  private String requestId;//idempotent key
  private String maker;
  private String checker; // created by-> Maker:<>,Checker:<>
  private String productCode;

  @NotNull
  private Set<TxnLine> debtors;
  @NotNull
  private Set<TxnLine> creditors;

}
