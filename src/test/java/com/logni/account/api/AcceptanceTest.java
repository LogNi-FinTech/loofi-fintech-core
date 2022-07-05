package com.logni.account.api;


import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AcceptanceTest {

    @LocalServerPort
    int randomServerPort;

    private RestTemplate restTemplate;
    private String baseUrl;


    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        baseUrl = "http://localhost:" + randomServerPort;
    }


}
