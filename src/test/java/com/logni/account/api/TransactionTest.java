package com.logni.account.api;

import com.logni.account.constants.LedgerConstant;
import com.logni.account.constants.TxnTypeConstant;
import com.logni.account.dto.rest.account.AcBalance;
import com.logni.account.dto.rest.account.AccountDto;
import com.logni.account.dto.rest.transaction.BulkTxnRequest;
import com.logni.account.dto.rest.transaction.TxnRequest;
import com.logni.account.entities.transactions.TransactionType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class TransactionTest {


    String baseUrl;
    RestTemplate restTemplate;
    String txnSingleUrl;
    String txnBulkUrl;



    public TransactionTest(String baseUrl) throws Exception {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
        this.txnSingleUrl=this.baseUrl+"/api/v1/txn";
        this.txnBulkUrl = this.baseUrl+"/api/v1/bulk/txn";

    }


    public void bankCashIn(){

        String toAc = "01674242986";
        String fromAc = LedgerConstant.CASH_HAND_CODE.toString();

        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            JSONObject request = new JSONObject().put("fromAc",fromAc)
                    .put("toAc",toAc)
                    .put("amount",1000)
                    .put("transactionType", new JSONObject().put("txnCode",TxnTypeConstant.BANK_CASH_IN) )
                    .put("note","Bank CashIn Test")
                    .put("referenceId","93483734")
                  //  .put("data",new JSONObject().put("bankRef","B-0038383"))
                    .put("channel","REST")
                    .put("requestId","9484848")
                    .put("maker","TEST-1")
                    .put("checker","TEST-2");

            HttpEntity<String> requestWithHeader =
                    new HttpEntity<String>(request.toString(), headers);

            ResponseEntity responseEntity = restTemplate.postForEntity(txnSingleUrl,requestWithHeader,String.class);
            Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

            AcBalance fromBalance = restTemplate.getForObject(baseUrl+"/api/v1/ledger/balance/"+fromAc,
                    AcBalance.class);

            AcBalance toBalance = restTemplate.getForObject(baseUrl+"/api/v1/account/balance/"+toAc,
                    AcBalance.class);

            Assertions.assertEquals(-1000.00,fromBalance.getBalance().doubleValue());
            Assertions.assertEquals(1000.00,toBalance.getBalance().doubleValue());

        }catch (JSONException ex){


        }
    }

    public void sendMoney(){

        String fromAc = "01674242986";
        String toAc = "01673173598";
        Double amount = 100.00;
        Double fee = 5.00;

        AcBalance fromBalanceInit = restTemplate.getForObject(baseUrl+"/api/v1/account/balance/"+fromAc,
                AcBalance.class);

        AcBalance toBalanceInit = restTemplate.getForObject(baseUrl+"/api/v1/account/balance/"+toAc,
                AcBalance.class);


        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            JSONObject request = new JSONObject().put("fromAc",fromAc)
                    .put("toAc",toAc)
                    .put("amount",amount)
                    .put("transactionType", new JSONObject().put("txnCode",TxnTypeConstant.SEND_MONEY) )
                    .put("note","Send Money Test Test")
                    .put("referenceId","93443734")
                   // .put("data",new JSONObject().put("reason","Bazar"))
                    .put("channel","REST")
                    .put("requestId","9484848");

            HttpEntity<String> requestWithHeader =
                    new HttpEntity<String>(request.toString(), headers);

            ResponseEntity responseEntity = restTemplate.postForEntity(txnSingleUrl,requestWithHeader,String.class);
            Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

            AcBalance fromBalance = restTemplate.getForObject(baseUrl+"/api/v1/account/balance/"+fromAc,
                    AcBalance.class);

            AcBalance toBalance = restTemplate.getForObject(baseUrl+"/api/v1/account/balance/"+toAc,
                    AcBalance.class);

            Assertions.assertEquals((fromBalanceInit.getBalance().doubleValue()-amount-fee),fromBalance.getBalance().doubleValue());
            Assertions.assertEquals((toBalanceInit.getBalance().doubleValue()+amount),toBalance.getBalance().doubleValue());

        }catch (JSONException ex){


        }
    }

    public void payment() {

        // fee from merchant

        String fromAc = "01674242986";
        String toAc = "01674242920";
        Double amount = 100.00;
        Double fee = 1.00;

        AcBalance fromBalanceInit = restTemplate
                .getForObject(baseUrl + "/api/v1/account/balance/" + fromAc,
                        AcBalance.class);

        AcBalance toBalanceInit = restTemplate
                .getForObject(baseUrl + "/api/v1/account/balance/" + toAc,
                        AcBalance.class);


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);


            TransactionType paymentType = new TransactionType();
            paymentType.setTxnCode(TxnTypeConstant.PAYMENT);
            TxnRequest requestParent = new TxnRequest();
                requestParent.setFromAc(fromAc);
                requestParent.setToAc(toAc);
                requestParent.setAmount(new BigDecimal(amount));
                requestParent.setTransactionType(paymentType) ;
                requestParent.setNote("TEST");
                requestParent.setChannel("REST");
                requestParent.setReferenceId("88383838") ;
                requestParent.setRequestId("8484848");
                requestParent.setData(null);


            TransactionType paymentFeeType = new TransactionType();
            paymentFeeType.setTxnCode(TxnTypeConstant.SF_PAYMENT_FROM_MERCHANT);

            TxnRequest requestChild = new TxnRequest();
                requestChild.setFromAc(toAc);
                requestChild.setToAc("SYSTEM");
                requestChild.setAmount(new BigDecimal(fee));
                requestChild.setTransactionType(paymentFeeType);
                requestChild.setNote("TEST");
                requestChild.setChannel("REST");
                requestChild.setRequestId("9ee9e4444");
                requestChild.setReferenceId("74747477475");
                requestChild.setData(null);

            List<TxnRequest> txnList = new ArrayList<>();
            txnList.add(requestParent);
            txnList.add(requestChild);

            BulkTxnRequest bulkTxnRequest = new BulkTxnRequest();
                bulkTxnRequest.setTxnRequestList(txnList);

            HttpEntity<BulkTxnRequest> requestWithHeader =
                    new HttpEntity<BulkTxnRequest>(bulkTxnRequest, headers);

            ResponseEntity responseEntity = restTemplate
                    .postForEntity(txnBulkUrl, requestWithHeader, String.class);
            Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

            AcBalance fromBalance = restTemplate
                    .getForObject(baseUrl + "/api/v1/account/balance/" + fromAc,
                            AcBalance.class);

            AcBalance toBalance = restTemplate
                    .getForObject(baseUrl + "/api/v1/account/balance/" + toAc,
                            AcBalance.class);

            Assertions.assertEquals((fromBalanceInit.getBalance().doubleValue() - amount),
                    fromBalance.getBalance().doubleValue());
            Assertions.assertEquals((toBalanceInit.getBalance().doubleValue() + amount - fee),
                    toBalance.getBalance().doubleValue());

            // fee from customer

            fromAc = "01674242986";
            toAc = "01674242921";
            amount = 100.00;
            fee = 1.00;

            fromBalanceInit = restTemplate
                    .getForObject(baseUrl + "/api/v1/account/balance/" + fromAc,
                            AcBalance.class);

            toBalanceInit = restTemplate.getForObject(baseUrl + "/api/v1/account/balance/" + toAc,
                    AcBalance.class);


                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                requestParent = new TxnRequest();
                requestParent.setFromAc(fromAc);
                requestParent.setToAc(toAc);
                requestParent.setAmount(new BigDecimal(amount));
                requestParent.setTransactionType(paymentType) ;
                requestParent.setNote("TEST");
                requestParent.setChannel("REST");
                requestParent.setReferenceId("88383838") ;
                requestParent.setRequestId("8484848");
                requestParent.setData(null);



         paymentFeeType = new TransactionType();
        paymentFeeType.setTxnCode(TxnTypeConstant.SF_PAYMENT_FROM_CUSTOMER);

         requestChild = new TxnRequest();
                    requestChild.setFromAc(fromAc);
                    requestChild.setToAc("SYSTEM");
                    requestChild.setAmount(new BigDecimal(fee));
                    requestChild.setTransactionType(paymentFeeType);
                    requestChild.setNote("TEST");
                    requestChild.setChannel("REST");
                    requestChild.setRequestId("9ee9e4444");
                    requestChild.setReferenceId("74747477475");
                    requestChild.setData(null);

                txnList.clear();
                txnList.add(requestParent);
                txnList.add(requestChild);
                bulkTxnRequest = new BulkTxnRequest();
                bulkTxnRequest.setTxnRequestList(txnList);

                requestWithHeader =
                        new HttpEntity<BulkTxnRequest>(bulkTxnRequest, headers);

                responseEntity = restTemplate
                        .postForEntity(txnBulkUrl, requestWithHeader, String.class);
                Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

                fromBalance = restTemplate
                        .getForObject(baseUrl + "/api/v1/account/balance/" + fromAc,
                                AcBalance.class);

                toBalance = restTemplate.getForObject(baseUrl + "/api/v1/account/balance/" + toAc,
                        AcBalance.class);

                Assertions.assertEquals((fromBalanceInit.getBalance().doubleValue() - amount - fee),
                        fromBalance.getBalance().doubleValue());
                Assertions.assertEquals((toBalanceInit.getBalance().doubleValue() + amount),
                        toBalance.getBalance().doubleValue());

    }


    public void agentCashIn(){

    }

    public void distbankCashIn(){

    }

    public void cashOutAgentPoint(){

    }

    public void notEnoughBalance(){

    }

    public void journalTest(){

    }
    public void testConcurrentTxnLock(){

    }

    public void testConcurrentTxnLockLowValue(){

    }
    public void testConcurrentTxnLockHighValue(){

    }

}
