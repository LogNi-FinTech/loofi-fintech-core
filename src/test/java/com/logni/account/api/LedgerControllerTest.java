package com.logni.account.api;

import com.logni.account.constants.LedgerConstant;
import com.logni.account.entities.accounts.Ledger;
import com.logni.account.enums.AccountHead;
import com.logni.account.enums.LedgerType;

import java.util.HashMap;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


//@TestMethodOrder(OrderAnnotation.class)
public class LedgerControllerTest {


    public RestTemplate restTemplate;
    public String baseUrl;
    public String ledgerUrl;

    HashMap ledgerMap;

    public LedgerControllerTest( String baseUrl) {
        this.baseUrl = baseUrl;
        this.ledgerUrl = baseUrl+ "/api/v1/ledger";
        this.restTemplate =  new RestTemplate();
    }



    void setUpLedgerMap() throws Exception{
        this.ledgerMap = new HashMap<String,JSONObject>() ;

        this.ledgerMap.put(LedgerConstant.CASH_HAND_CODE,
                new JSONObject().put("name",LedgerConstant.CASH_HAND).put("ledgerCode",LedgerConstant.CASH_HAND_CODE)
                        .put("description",LedgerConstant.CASH_HAND)
                        .put("head", AccountHead.ASSET)
                        .put("type", LedgerType.SYSTEM)
                        .put("onlyParent", false)
                        .put("showAccountsInChart",true));

        this.ledgerMap.putIfAbsent(LedgerConstant.CUSTOMER_AC_CODE,
                new JSONObject().put("name",LedgerConstant.CUSTOMER_AC).put("ledgerCode",LedgerConstant.CUSTOMER_AC_CODE)
                        .put("description",LedgerConstant.CUSTOMER_AC)
                        .put("head", AccountHead.LIABILITY)
                        .put("type", LedgerType.MEMBER)
                        .put("onlyParent", false)
                        .put("showAccountsInChart",true));
        this.ledgerMap.putIfAbsent(LedgerConstant.MERCHANT_AC_CODE,
                new JSONObject().put("name",LedgerConstant.MERCHANT_AC).put("ledgerCode",LedgerConstant.MERCHANT_AC_CODE)
                        .put("description",LedgerConstant.MERCHANT_AC)
                        .put("head", AccountHead.LIABILITY)
                        .put("type", LedgerType.MEMBER)
                        .put("onlyParent", false)
                        .put("showAccountsInChart",true));

        this.ledgerMap.putIfAbsent(LedgerConstant.AGENT_AC_CODE,
                new JSONObject().put("name",LedgerConstant.AGENT_AC).put("ledgerCode",LedgerConstant.AGENT_AC_CODE)
                        .put("description",LedgerConstant.AGENT_AC)
                        .put("head", AccountHead.LIABILITY)
                        .put("type", LedgerType.MEMBER)
                        .put("onlyParent", false)
                        .put("showAccountsInChart",true));

        this.ledgerMap.putIfAbsent(LedgerConstant.DISTRIBUTOR_AC,
                new JSONObject().put("name",LedgerConstant.DISTRIBUTOR_AC).put("ledgerCode",LedgerConstant.DISTRIBUTOR_AC_CODE)
                        .put("description",LedgerConstant.DISTRIBUTOR_AC)
                        .put("head", AccountHead.LIABILITY)
                        .put("type", LedgerType.MEMBER)
                        .put("onlyParent", false)
                        .put("showAccountsInChart",true));

        this.ledgerMap.putIfAbsent(LedgerConstant.SF_SENDMONEY_CODE,
                new JSONObject().put("name",LedgerConstant.SF_SENDMONEY).put("ledgerCode",LedgerConstant.SF_SENDMONEY_CODE)
                        .put("description",LedgerConstant.SF_SENDMONEY)
                        .put("head", AccountHead.INCOME)
                        .put("type", LedgerType.SYSTEM)
                        .put("onlyParent", false)
                        .put("showAccountsInChart",true));

        this.ledgerMap.putIfAbsent(LedgerConstant.AGENT_COM_CODE,
                new JSONObject().put("name",LedgerConstant.AGENT_COM).put("ledgerCode",LedgerConstant.AGENT_COM_CODE)
                        .put("description",LedgerConstant.AGENT_COM)
                        .put("head", AccountHead.EXPENSE)
                        .put("type", LedgerType.SYSTEM)
                        .put("onlyParent", true)
                        .put("showAccountsInChart",true));
        this.ledgerMap.putIfAbsent(LedgerConstant.AGENT_COM_CASHIN_CODE,
                new JSONObject().put("name",LedgerConstant.AGENT_COM_CASHIN).put("ledgerCode",LedgerConstant.AGENT_COM_CASHIN_CODE)
                        .put("description",LedgerConstant.AGENT_COM_CASHIN)
                        .put("head", AccountHead.EXPENSE)
                        .put("type", LedgerType.SYSTEM)
                        .put("onlyParent", false)
                        .put("showAccountsInChart",true));
        this.ledgerMap.putIfAbsent(LedgerConstant.AGENT_COM_CASHOUT_CODE,
                new JSONObject().put("name",LedgerConstant.AGENT_COM_CASHOUT).put("ledgerCode",LedgerConstant.AGENT_COM_CASHOUT_CODE)
                        .put("description",LedgerConstant.AGENT_COM_CASHOUT)
                        .put("head", AccountHead.EXPENSE)
                        .put("type", LedgerType.SYSTEM)
                        .put("onlyParent", false)
                        .put("showAccountsInChart",true));

        this.ledgerMap.putIfAbsent(LedgerConstant.SF_PAYMENT_CODE,
                new JSONObject().put("name",LedgerConstant.SF_PAYMENT).put("ledgerCode",LedgerConstant.SF_PAYMENT_CODE)
                        .put("description",LedgerConstant.SF_PAYMENT)
                        .put("head", AccountHead.INCOME)
                        .put("type", LedgerType.SYSTEM)
                        .put("onlyParent", true)
                        .put("showAccountsInChart",true));

        this.ledgerMap.putIfAbsent(LedgerConstant.SF_PAYMENT_FROM_CUSTOMER_CODE,
                new JSONObject().put("name",LedgerConstant.SF_PAYMENT_FROM_CUSTOMER).put("ledgerCode",LedgerConstant.SF_PAYMENT_FROM_CUSTOMER_CODE)
                        .put("description",LedgerConstant.SF_PAYMENT_FROM_CUSTOMER)
                        .put("head", AccountHead.INCOME)
                        .put("type", LedgerType.SYSTEM)
                        .put("onlyParent", false)
                        .put("showAccountsInChart",true));

        this.ledgerMap.putIfAbsent(LedgerConstant.SF_PAYMENT_FROM_MERCHANT_CODE,
                new JSONObject().put("name",LedgerConstant.SF_PAYMENT_FROM_MERCHANT).put("ledgerCode",LedgerConstant.SF_PAYMENT_FROM_MERCHANT_CODE)
                        .put("description",LedgerConstant.SF_PAYMENT_FROM_MERCHANT)
                        .put("head", AccountHead.INCOME)
                        .put("type", LedgerType.SYSTEM)
                        .put("onlyParent", false)
                        .put("showAccountsInChart",true));

    }

   // @Order(1)
   // @Test
    public void setUpLedger() throws Exception {
        setUpLedgerMap();
        System.out.println(ledgerUrl);
        this.ledgerMap.forEach((k,v)->{
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> request =
                        new HttpEntity<String>(v.toString(), headers);
                    ResponseEntity responseEntity = this.restTemplate.postForEntity(ledgerUrl,request,String.class);
                    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

            });

    }

   // @Order(2)
   // @Test
    public void checkFirstLedger() {

            Ledger ledger =restTemplate.getForObject(this.baseUrl+"/api/v1/ledger/1",Ledger.class);
            Assertions.assertEquals(1,ledger.getId());

    }
}
