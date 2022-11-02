package com.logni.account;

import com.logni.account.api.AcControllerTest;
import com.logni.account.api.LedgerControllerTest;
import com.logni.account.api.TransactionTest;
import com.logni.account.api.TxnTypeControllerTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class AccountApplicationTests {

	@Value("${server.port}")
	private int serverPort;


	private String baseUrl;

	@BeforeEach
	void setup(){
		baseUrl = "http://localhost:"+serverPort;
		System.out.println(baseUrl);
	}

	@Test
	@Order(1)
	void contextLoads() {
		System.out.println("-------------Context Loaded------------");

	}

	@Test
	@Order(2)
	public void ledgerTest() throws Exception{
		LedgerControllerTest ld = new LedgerControllerTest(this.baseUrl);
		ld.setUpLedger();
		ld.checkFirstLedger();
	}

	@Test
	@Order(3)
	public void txnTypeTest() throws Exception{
		TxnTypeControllerTest tt = new TxnTypeControllerTest(this.baseUrl);
		tt.setTxnType();
		tt.checkFirstTxnType();
		tt.setUpServiceFee();
	}

	@Test
	@Order(4)
	public void setUpAndTestMemberAc() throws Exception{
		AcControllerTest act = new AcControllerTest(this.baseUrl);
		act.setUpAllAc();
		act.checkAcAndBalance();

	}

	@Test
	@Order(5)
	public void bankCashInTest() throws Exception{
		TransactionTest t = new TransactionTest(this.baseUrl);
		t.bankCashIn();

	}

	@Test
	@Order(6)
	public void sendMoneyTest() throws Exception{
		TransactionTest t = new TransactionTest(this.baseUrl);
		t.sendMoney();


	}


	@Test
	@Order(7)
	public void paymentTest() throws Exception{
		TransactionTest t = new TransactionTest(this.baseUrl);
		t.payment();

	}

}
