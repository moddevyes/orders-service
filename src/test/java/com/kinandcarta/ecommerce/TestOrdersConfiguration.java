package com.kinandcarta.ecommerce;

import com.kinandcarta.ecommerce.clients.AccountServiceClient;
import com.kinandcarta.ecommerce.infrastructure.OrdersAccountRepository;
import com.kinandcarta.ecommerce.infrastructure.OrdersAddressRepository;
import com.kinandcarta.ecommerce.infrastructure.OrdersLineItemsRepository;
import com.kinandcarta.ecommerce.infrastructure.OrdersRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

@TestConfiguration
@ActiveProfiles("test")
public class TestOrdersConfiguration {

    @Value("${commerce.clients.accounts.baseUrl}")
    String baseURLTesting;

    @Value("${commerce.clients.accounts.findByAccountIdRefUrl}")
    String getAccountIdUri;

    final String expectedAccountIdRef = "4f464483-a1f0-4ce9-a19e-3c0f23e84a67";
    final String path = "/accounts/" + expectedAccountIdRef;

    @MockBean
    OrdersLineItemsRepository ordersLineItemsRepository;
    @MockBean
    OrdersAccountRepository ordersAccountRepository;
    @MockBean
    OrdersAddressRepository ordersAddressRepository;

    @MockBean
    OrdersRepository ordersRepository;

    @MockBean
    AccountServiceClient accountServiceClient;

//    @Bean
//    @Qualifier("testingAccountServiceClient")
//    public AccountServiceClient accountServiceClientTesting() {
//        return new AccountServiceClient(baseURLTesting, path);
//    }

    @Bean
    @Qualifier("testingOrdersHandler")
    public OrdersHandler ordersHandlerTesting() {
        return new OrdersHandler(ordersRepository,
                ordersLineItemsRepository,
                ordersAccountRepository,
                ordersAddressRepository
                );
    }
    @Bean
    @Qualifier("testingOrdersController")
    public OrdersController ordersController() {
        return new OrdersController(ordersHandlerTesting(), accountServiceClient);
    }
}
