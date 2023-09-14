package com.kinandcarta.ecommerce;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kinandcarta.ecommerce.clients.AccountServiceClient;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;

@Slf4j
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebClient
public class MockAbstractWebServer {
    AccountServiceClient accountServiceClient;

    final String expectedAccountIdRef = "4f464483-a1f0-4ce9-a19e-3c0f23e84a67";
    final String path = "/accounts/" + expectedAccountIdRef;
    ObjectMapper mapper;

    // MockWebServer, https://refactorizando.com/en/mockwebserver-webclient-spring/

    @Value("${commerce.clients.accounts.baseUrl}")
    String baseURL;

    @Value("${commerce.clients.accounts.findByAccountIdRefUrl}")
    String getAccountIdUri;
    static MockWebServer mockWebServer;
    @BeforeAll
     void setUp() {
        mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
        mockWebServer = new MockWebServer();

        accountServiceClient = new AccountServiceClient("http://localhost:8001", path);
    }

    @AfterAll
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }
}
