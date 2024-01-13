package com.logni.account.dto.rest.transaction;

import com.fasterxml.jackson.databind.JsonNode;
import com.logni.account.entities.transactions.TransactionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

import lombok.ToString;

@ToString
@Getter
@Setter
public class TxnRequest {

  @NotBlank(message = "From AC Required")
  private String fromAc;
  @NotBlank(message = "TO AC Required")
  private String toAc;

  @DecimalMin(value = "0.0001", inclusive = false)
  private BigDecimal amount;

  @NotNull
  private TransactionType transactionType; // either id or TxnCode at this object

  @NotNull(message = "Channel Required")
  private String channel; // WEB,Android-Mobile,IOS-Mobile,USSD,GW,ATM,API

  private String description;
  private String note;
  private String referenceId;
  private JsonNode data;
  private String productCode;

  private String tag;
  private String requestId;//idempotent key
  private String maker;
  private String checker; // created by-> Maker:<>,Checker:<>

//    private BigDecimal rate;
//    private BigDecimal minRate;
//    private BigDecimal maxRate;
//    private Boolean feeFromCustomer;
//    private String externalCustomer;
//
//    private String txnTag;
//    private String batchId;


}
