package com.logni.account.api;

import com.logni.account.constants.LedgerConstant;
import com.logni.account.dto.rest.account.AcBalance;
import com.logni.account.dto.rest.account.AccountDto;
import com.logni.account.entities.accounts.Ledger;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

//@ExtendWith(SpringExtension.class)
public class AcControllerTest {

    @Value("${server.port}")
    private int serverPort;

    HashMap<String, List<JSONObject>> memberWalletMap = new HashMap<>();

    String baseUrl;
    RestTemplate restTemplate;
    String accountUrl;

    @BeforeEach
    public void setUp(){
        System.out.println("---Extension---");
    }

    public AcControllerTest(String baseUrl) throws Exception {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
        setUpMemberWallet();
    }

    public void setUpMemberWallet() throws Exception{
        List<JSONObject> customerList = new ArrayList<>();

        customerList.add(new JSONObject().put("name","Test Customer 1")
                .put("identifier","01674242000")
                .put("customerId","01674242000")
                .put("state","ACTIVE")
                .put("ledgerId",getLedgerId(LedgerConstant.CUSTOMER_AC_CODE)));

        customerList.add(new JSONObject().put("name","Test Customer 2")
                .put("identifier","01674242001")
                .put("customerId","01674242001")
                .put("state","ACTIVE")
                .put("ledgerId",getLedgerId(LedgerConstant.CUSTOMER_AC_CODE)));

        customerList.add(new JSONObject().put("name","Test Customer 3")
                .put("identifier","01674242002")
                .put("customerId","01674242002")
                .put("state","ACTIVE")
                .put("ledgerId",getLedgerId(LedgerConstant.CUSTOMER_AC_CODE)));



        this.memberWalletMap.putIfAbsent("CUSTOMER",customerList);

        List<JSONObject> disList = new ArrayList<>();
        disList.add(new JSONObject().put("name","Distributor 1")
                .put("identifier","01674242900")
                .put("customerId","01674242900")
                .put("state","ACTIVE")
                .put("ledgerId",getLedgerId(LedgerConstant.DISTRIBUTOR_AC_CODE)));

        disList.add(new JSONObject().put("name","Distributor 2")
                .put("identifier","01674242901")
                .put("customerId","01674242901")
                .put("state","ACTIVE")
                .put("ledgerId",getLedgerId(LedgerConstant.DISTRIBUTOR_AC_CODE)));


        this.memberWalletMap.putIfAbsent("DISTRIBUTOR",disList);

        List<JSONObject> agentList = new ArrayList<>();

        agentList.add(new JSONObject().put("name","Agent 1")
                .put("identifier","01674242910")
                .put("customerId","01674242910")
                .put("state","ACTIVE")
                .put("ledgerId",getLedgerId(LedgerConstant.AGENT_AC_CODE)));
        agentList.add(new JSONObject().put("name","Agent 2")
                .put("identifier","01674242911")
                .put("customerId","01674242911")
                .put("state","ACTIVE")
                .put("ledgerId",getLedgerId(LedgerConstant.AGENT_AC_CODE)));
        this.memberWalletMap.putIfAbsent("AGENT",agentList);

        List<JSONObject> merchantList = new ArrayList<>();
        merchantList.add(new JSONObject().put("name","Merchant 1")
                .put("identifier","01674242920")
                .put("customerId","01674242920")
                .put("state","ACTIVE")
                .put("ledgerId",getLedgerId(LedgerConstant.MERCHANT_AC_CODE)));
        merchantList.add(new JSONObject().put("name","Merchant 2")
                .put("identifier","01674242921")
                .put("customerId","01674242921")
                .put("state","ACTIVE")
                .put("ledgerId",getLedgerId(LedgerConstant.MERCHANT_AC_CODE)));

        this.memberWalletMap.putIfAbsent("MERCHANT",merchantList);

    }

    private Long getLedgerId(Integer ledgerCode) throws Exception{
        Ledger ledger= restTemplate.getForObject(baseUrl+"/api/v1/ledger/code/"+ledgerCode,Ledger.class);
        return ledger.getId();
    }

    //@Test
    public void testAcCreation(){
        System.out.println(serverPort);
        System.out.println("AC CREATION.");
    }


    public void setUpAllAc(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        accountUrl = baseUrl+"/api/v1/account";
        this.memberWalletMap.forEach((k,v)->{
            v.forEach(e->{
                HttpEntity<String> request =
                        new HttpEntity<String>(e.toString(), headers);

                ResponseEntity responseEntity = restTemplate.postForEntity(accountUrl,request,String.class);
                Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

            });
        });
    }

    public void  checkAcAndBalance() {
        accountUrl = baseUrl+"/api/v1/account";

        this.memberWalletMap.forEach((k,v)->{
            v.forEach(e->{
                try{
                    AccountDto accountDto = restTemplate.getForObject(baseUrl+"/api/v1/account/"+e.getString("identifier"),
                            AccountDto.class);
                    Assertions.assertEquals(e.getString("identifier"),accountDto.getIdentifier());
                    AcBalance acBalance = restTemplate.getForObject(baseUrl+"/api/v1/account/balance/"+e.getString("identifier"),
                            AcBalance.class);
                    System.out.println("Balance:"+acBalance.getBalance());
                    Assertions.assertEquals(0.00,acBalance.getBalance().doubleValue());

                }catch (JSONException ex){
                }

            });
        });
    }

}
