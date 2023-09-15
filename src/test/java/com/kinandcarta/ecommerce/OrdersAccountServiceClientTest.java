package com.kinandcarta.ecommerce;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kinandcarta.ecommerce.clients.AccountServiceClient;
import com.kinandcarta.ecommerce.entities.OrdersAccount;
import com.kinandcarta.ecommerce.entities.OrdersAddress;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebClient
class OrdersAccountServiceClientTest {
    AccountServiceClient accountServiceClient;

    final String expectedId = "4f464483-a1f0-4ce9-a19e-3c0f23e84a67";
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

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());

        accountServiceClient = new AccountServiceClient();
    }


    @Test
    void assertThatWebClient_findsAccountByIdRef() {
        assertThat(ordersAccount).isNotNull();
    }

}
