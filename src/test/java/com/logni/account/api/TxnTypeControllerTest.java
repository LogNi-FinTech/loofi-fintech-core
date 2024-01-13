package com.logni.account.api;


import com.logni.account.constants.LedgerConstant;
import com.logni.account.constants.TxnTypeConstant;
import com.logni.account.entities.accounts.Ledger;
import com.logni.account.entities.transactions.TransactionType;
import java.util.HashMap;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class TxnTypeControllerTest {
  public String txnTypeUrl;
  public String baseUrl;
  public RestTemplate restTemplate;

  HashMap<Integer, JSONObject> txnTypeMap = new HashMap<>();


  public TxnTypeControllerTest(String baseUrl) {
    this.baseUrl = baseUrl;
    this.txnTypeUrl = baseUrl + "/api/v1/txn/type";
    this.restTemplate = new RestTemplate();
  }

  private JSONObject getLedgerObject(Integer ledgerCode) throws Exception {
    Ledger ledger = restTemplate.getForObject(baseUrl + "/api/v1/ledger/code/" + ledgerCode, Ledger.class);
    return new JSONObject().put("id", ledger.getId());
  }

  public void setUpTxnTypeMap() throws Exception {
    System.out.println(this.baseUrl);
    this.txnTypeMap.putIfAbsent(TxnTypeConstant.BANK_CASH_IN,
      new JSONObject().put("name", "Bank Cash In Customer").put("txnCode", TxnTypeConstant.BANK_CASH_IN)
        .put("description", "Bank Cash In")

        .put("fromType", getLedgerObject(LedgerConstant.CASH_HAND_CODE))
        .put("toType", getLedgerObject(LedgerConstant.CUSTOMER_AC_CODE))
        .put("enabled", true));

    this.txnTypeMap.putIfAbsent(TxnTypeConstant.SEND_MONEY,
      new JSONObject().put("name", "Send Money").put("txnCode", TxnTypeConstant.SEND_MONEY)
        .put("description", "Send Money")

        .put("fromType", getLedgerObject(LedgerConstant.CUSTOMER_AC_CODE))
        .put("toType", getLedgerObject(LedgerConstant.CUSTOMER_AC_CODE))
        .put("enabled", true));

    this.txnTypeMap.putIfAbsent(TxnTypeConstant.BANK_CASH_IN_DISTRIBUTOR,
      new JSONObject().put("name", "Bank Cash In Distributor").put("txnCode", TxnTypeConstant.BANK_CASH_IN_DISTRIBUTOR)
        .put("description", "Bank Cash In Distributor")

        .put("fromType", getLedgerObject(LedgerConstant.CASH_HAND_CODE))
        .put("toType", getLedgerObject(LedgerConstant.DISTRIBUTOR_AC_CODE))
        .put("enabled", true));

    this.txnTypeMap.putIfAbsent(TxnTypeConstant.AGENT_TOPUP,
      new JSONObject().put("name", "Agent TopUp").put("txnCode", TxnTypeConstant.AGENT_TOPUP)
        .put("description", "Agent TopUp From Distributor")

        .put("fromType", getLedgerObject(LedgerConstant.DISTRIBUTOR_AC_CODE))
        .put("toType", getLedgerObject(LedgerConstant.AGENT_AC_CODE))
        .put("enabled", true));

    this.txnTypeMap.putIfAbsent(TxnTypeConstant.PAYMENT,
      new JSONObject().put("name", "Payment At merchant Point").put("txnCode", TxnTypeConstant.PAYMENT)
        .put("description", "Payment At merchant Point")

        .put("fromType", getLedgerObject(LedgerConstant.CUSTOMER_AC_CODE))
        .put("toType", getLedgerObject(LedgerConstant.MERCHANT_AC_CODE))
        .put("enabled", true));

    this.txnTypeMap.putIfAbsent(TxnTypeConstant.CASH_IN_AT_AGENT,
      new JSONObject().put("name", "Cash In Agent Point").put("txnCode", TxnTypeConstant.CASH_IN_AT_AGENT)
        .put("description", "Cash In Agent Point")

        .put("fromType", getLedgerObject(LedgerConstant.AGENT_AC_CODE))
        .put("toType", getLedgerObject(LedgerConstant.CUSTOMER_AC_CODE))
        .put("enabled", true));
    this.txnTypeMap.putIfAbsent(TxnTypeConstant.CASH_OUT_AT_AGENT,
      new JSONObject().put("name", "Cash Out At Agent").put("txnCode", TxnTypeConstant.CASH_OUT_AT_AGENT)
        .put("description", "Cash Out At Agent")

        .put("fromType", getLedgerObject(LedgerConstant.CUSTOMER_AC_CODE))
        .put("toType", getLedgerObject(LedgerConstant.AGENT_AC_CODE))
        .put("enabled", true));


    this.txnTypeMap.putIfAbsent(TxnTypeConstant.SF_SEND_MONEY,
      new JSONObject().put("name", "Service Fee P2P From Customer").put("txnCode", TxnTypeConstant.SF_SEND_MONEY)
        .put("description", "Service Fee P2P From Customer")

        .put("fromType", getLedgerObject(LedgerConstant.CUSTOMER_AC_CODE))
        .put("toType", getLedgerObject(LedgerConstant.SF_SENDMONEY_CODE))
        .put("enabled", true));

    this.txnTypeMap.putIfAbsent(TxnTypeConstant.SF_PAYMENT_FROM_CUSTOMER,
      new JSONObject().put("name", "Service Fee Payment From Customer").put("txnCode", TxnTypeConstant.SF_PAYMENT_FROM_CUSTOMER)
        .put("description", "Service Fee Payment From Customer")

        .put("fromType", getLedgerObject(LedgerConstant.CUSTOMER_AC_CODE))
        .put("toType", getLedgerObject(LedgerConstant.SF_PAYMENT_FROM_CUSTOMER_CODE))
        .put("enabled", true));

    this.txnTypeMap.putIfAbsent(TxnTypeConstant.SF_PAYMENT_FROM_MERCHANT,
      new JSONObject().put("name", "Service Fee Payment From Merchant").put("txnCode", TxnTypeConstant.SF_PAYMENT_FROM_MERCHANT)
        .put("description", "Service Fee Payment From Merchant")

        .put("fromType", getLedgerObject(LedgerConstant.MERCHANT_AC_CODE))
        .put("toType", getLedgerObject(LedgerConstant.SF_PAYMENT_FROM_MERCHANT_CODE))
        .put("enabled", true));


  }

  // @Order(1)
  // @Test
  public void setTxnType() throws Exception {
    System.out.println(txnTypeUrl);
    setUpTxnTypeMap();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    this.txnTypeMap.forEach((k, v) -> {
      HttpEntity<String> request =
        new HttpEntity<String>(v.toString(), headers);
      ResponseEntity responseEntity = restTemplate.postForEntity(txnTypeUrl, request, String.class);
      Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    });
  }

  public void setUpServiceFee() throws Exception {

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    JSONObject requestObj = new JSONObject().put("name", "Send Money Fee")
      .put("description", "Send Money Service Fee")
      .put("type", "FEE")
      .put("payer", "SOURCE")
      .put("receiver", "SYSTEM")
      .put("enabled", true)
      .put("chargeType", "FIXED")
      .put("fixedAmount", 5)
      .put("channel", "Default")
      .put("deductAmount", false)
      .put("originalTxnType", new JSONObject().put("id", getByCode(TxnTypeConstant.SEND_MONEY)))
      .put("generatedTxnType", new JSONObject().put("id", getByCode(TxnTypeConstant.SF_SEND_MONEY)));

    HttpEntity<String> requestWithHeader =
      new HttpEntity<String>(requestObj.toString(), headers);
    ResponseEntity responseEntity = restTemplate.postForEntity(txnTypeUrl + "/sub", requestWithHeader, String.class);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  public Long getByCode(Integer code) {
    TransactionType type = restTemplate.getForObject(baseUrl + "/api/v1/txn/type/byCode/" + code, TransactionType.class);
    return type.getId();
  }

  //@Order(2)
  //@Test
  public void checkFirstTxnType() {
    TransactionType type = restTemplate.getForObject(baseUrl + "/api/v1/txn/type/1", TransactionType.class);
    Assertions.assertEquals(1, type.getId());
    type = restTemplate.getForObject(baseUrl + "/api/v1/txn/type/byCode/" + TxnTypeConstant.BANK_CASH_IN, TransactionType.class);
    Assertions.assertEquals(TxnTypeConstant.BANK_CASH_IN, type.getTxnCode());
  }
}
