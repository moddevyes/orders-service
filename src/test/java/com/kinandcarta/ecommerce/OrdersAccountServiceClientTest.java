package com.kinandcarta.ecommerce;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kinandcarta.ecommerce.clients.AccountServiceClient;
import com.kinandcarta.ecommerce.entities.OrdersAccount;
import com.kinandcarta.ecommerce.entities.OrdersAddress;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.http.MediaType;

import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@Slf4j
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebClient
class OrdersAccountServiceClientTest {
    AccountServiceClient accountServiceClient;

    final String expectedId = "4f464483-a1f0-4ce9-a19e-3c0f23e84a67";
    final String path = "/accounts/" + expectedId;
    OrdersAddress ordersAddress = OrdersAddress.builder().id(100L)
            .address1("100")
            .address2("")
            .city("Food Forest City")
            .state("FL")
            .province("")
            .postalCode("33000")
            .country("US").build();

    OrdersAccount ordersAccount = OrdersAccount.builder()
            .id(100L)
            .accountRefId(expectedId)
            .firstName("DukeFirstName")
            .lastName("DukeLastName")
            .emailAddress("dukefirst.last@enjoy.com")
            .addresses(
                    Set.of(ordersAddress)).build();
    ObjectMapper mapper;

    // MockWebServer, https://refactorizando.com/en/mockwebserver-webclient-spring/

    @Value("${commerce.clients.accounts.baseUrl}")
    String baseURL;

    @Value("${commerce.clients.accounts.findByAccountIdRefUrl}")
    String getAccountIdUri;
    static MockWebServer mockWebServer;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
        mockWebServer = new MockWebServer();

        accountServiceClient = new AccountServiceClient("http://localhost:8001", path);
    }

    @AfterEach void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void assertThatWebClient_findsAccountByIdRef() {
        assertThat(ordersAccount).isNotNull();
    }
    @Test
    void shouldThrow_HTTP404_forInvalidAccountIdRef() throws Exception {
        Dispatcher dispatcherMock = new Dispatcher() {
            @NotNull
            @Override
            public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) throws InterruptedException {
                Objects.requireNonNull(recordedRequest, "Request was null for MockServer");
                return new MockResponse().setResponseCode(404);
            }
        };

        mockWebServer.setDispatcher(dispatcherMock);
        mockWebServer.start(8001);

        assertThatThrownBy(() ->
            accountServiceClient.findByAccountIdRef(expectedId)).hasStackTraceContaining("404 NOT_FOUND");
    }

    @Test
    void shouldReturn_ValidOrdersAccount() throws Exception {
        Dispatcher dispatcherMock = new Dispatcher() {
            @SneakyThrows
            @NotNull
            @Override
            public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) {
                Objects.requireNonNull(recordedRequest, "Request was null for MockServer");
                return new MockResponse().setResponseCode(200)
                        .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .setBody(mapper.writeValueAsString(ordersAccount));
            }
        };

        mockWebServer.setDispatcher(dispatcherMock);
        mockWebServer.start(8001);

        OrdersAccount accountFound = accountServiceClient.findByAccountIdRef(expectedId);
        assertThat(accountFound.getAccountRefId()).isEqualTo(expectedId);
    }

}
