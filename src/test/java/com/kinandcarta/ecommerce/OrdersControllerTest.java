package com.kinandcarta.ecommerce;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@Slf4j
/*
    Minimum Fields:

    "id",
    "ordersAccount",
    "orderNumber",
    "orderDate"

    Optional and/or Derived:

    "ordersShippingAddress",
    "orderLineItems",
    "totalPrice",
    "createDateTime",
    "updateDateTime"
 */
@SuppressWarnings("rawtypes")
class OrdersControllerTest {
    private static final int TWO_ORDER_LINE_ITEMS = 2;
    final String orderNumber = "ord-" + UUID.randomUUID();
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
            .firstName("DukeFirstName")
            .lastName("DukeLastName")
            .emailAddress("dukefirst.last@enjoy.com")
            .addresses(
                    Set.of(ordersAddress)).build();

    OrderLineItems firstProduct = OrderLineItems.builder()
            .id(3L)
            .orderId(1L)
            .quantity(2)
            .price(new BigDecimal("10"))
            .productId(1L)
            .build();
    OrderLineItems secondProduct = OrderLineItems.builder()
            .id(4L)
            .orderId(1L)
            .quantity(1)
            .price(new BigDecimal("13.99"))
            .productId(3L)
            .build();

    Orders minimumOrderNullAccount = Orders.builder()
            .id(1L)
            .ordersAccount(null)
            .orderNumber(orderNumber)
            .orderDate(Instant.now(Clock.systemUTC())).build();
    Orders minimumOrder = Orders.builder()
            .id(1L)
            .ordersAccount(ordersAccount)
            .orderNumber(orderNumber)
            .orderDate(Instant.now(Clock.systemUTC())).build();
    Orders davidKingMoonMousePad = Orders.builder()
            .id(1L)
            .ordersAccount(ordersAccount)
            .orderNumber(orderNumber)
            .orderDate(Instant.now(Clock.systemUTC()))
            .orderLineItems(Set.of(firstProduct, secondProduct)).build();
    Orders davidKingMoonMousePad_Order2 = Orders.builder()
            .id(2L)
            .ordersAccount(ordersAccount)
            .orderNumber(orderNumber+"000")
            .orderDate(Instant.now(Clock.systemUTC()))
            .orderLineItems(Set.of(firstProduct, secondProduct)).build();
    Orders davidKingMoonMousePad_Order3 = Orders.builder()
            .id(3L)
            .ordersAccount(ordersAccount)
            .orderNumber(orderNumber+"333")
            .orderDate(Instant.now(Clock.systemUTC()))
            .orderLineItems(Set.of(firstProduct, secondProduct)).build();
    Orders davidKingMoonMousePad_Order4 = Orders.builder()
            .id(4L)
            .ordersAccount(ordersAccount)
            .orderNumber(orderNumber+"444")
            .orderDate(Instant.now(Clock.systemUTC()))
            .orderLineItems(Set.of(firstProduct, secondProduct)).build();

    @Mock
    OrdersHandler ordersHandler;

    OrdersController controller;

    @LoadBalanced
    WebClient webClient;

    @Mock AccountClient accountClient;

    @BeforeEach
    void setUp() {
        controller = new OrdersController(ordersHandler);
    }

    @Test void initClassUnderTest() {
        assertThat(controller).isNotNull();
    }
    @Test
    void createOrder_withMinimumFields() {
        Orders orderNew = createOrder_VerifyMinimumFields(minimumOrder);

        assertThat(orderNew.getId()).isNotNull();
        assertThat(orderNew.getOrdersAccount()).isNotNull();
        assertThat(orderNew.getOrderNumber()).isNotNull().isEqualTo(orderNumber);
        assertThat(orderNew.getOrderDate()).isNotNull().isInstanceOf(Instant.class);
    }

    @Test
    void createOrder_withOrderLineItems() {
        Orders orderWithLineItems = createOrder_VerifyMinimumFields(davidKingMoonMousePad);

        assertThat(orderWithLineItems.getId()).isNotNull().isEqualTo(1L);
        assertThat(orderWithLineItems.getOrderLineItems()).isNotNull().hasSize(TWO_ORDER_LINE_ITEMS);
    }

    @Test
    void createOrderFails_whenAccountIsInvalid_MockTest() {
        assertThatThrownBy(() ->
            mockAccountService(minimumOrder))
                .isInstanceOf(InvalidAccountException.class);
    }

    @Test
    void createOrderFails_forMissingAccount() {
        verifyAccount_isValidFromAccountService(minimumOrderNullAccount);
        ResponseEntity<Orders> orderCreated = controller.create(minimumOrderNullAccount);
        assertThat(orderCreated).isEqualTo(ResponseEntity.badRequest().build());
    }

    @Test
    void verify_ordersCanQueryAccountById() {
        assertThat(accountClient).isNotNull();
    }
    @Test @Disabled
    void createOrderFails_forInvalidAccount() {
        verifyAccount_isValidFromAccountService(minimumOrder);
        ResponseEntity<Orders> orderCreated = controller.create(minimumOrder);
        assertThat(orderCreated).isEqualTo(ResponseEntity.badRequest().build());
    }






    // - BASE_CASE__create_minimum_order, verifies the minimum fields are present.
    private Orders createOrder_VerifyMinimumFields(final Orders orderCreateCommand) {
        when(ordersHandler.create(orderCreateCommand)).thenReturn(orderCreateCommand);
        ResponseEntity<Orders> orderCreated = controller.create(orderCreateCommand);
        assertThat(orderCreated).isNotNull();
        assertThat(orderCreated.getBody()).isNotNull();
        return orderCreated.getBody();
    }

    private void verifyAccount_isValidFromAccountService(final Orders orderCreateCommand) {
        /*
         Design: lookup the account:
          1. In the account_replicated_data table
            -  Found?  return Account
            -  NotFound? Lookup via Rest Call to Accounts Service), not found, throw InvalidAccountException, stop Orders creation

         How-to?
         1. RestTemplate with @Async or AsyncRestTemplate (Deprecated) use WebClient
         - https://codetinkering.com/spring-asyncresttemplate-deprecated/
         2. Mocking WebClient
         - https://www.baeldung.com/spring-mocking-webclient

         */
        when(ordersHandler.create(orderCreateCommand)).thenThrow(InvalidAccountException.class);
    }

    private void verifyOrderNotCreated_whenEmailIsInvalid(final Orders orderCreateCommand) {

    }

    // MOCKS

    private void mockAccountService(final Orders findAccountCommand) {
        // TODO: remove after testing -- mocks the account service call or replicated dataset
        Map<Long, OrdersAccount> data = new HashMap<>();
            data.put(1L, OrdersAccount.builder()
                .id(1L)
                .firstName("Firstname100")
                .lastName("LastName100")
                .emailAddress("firstlast100@enjoy.com").build());

        log.debug("DATA: \n" + data);

        if (!data.containsKey(findAccountCommand.getOrdersAccount().getId())) {
            log.debug("Simulate_AccountService_Lookup: Account for ID -> "
                    + findAccountCommand.getOrdersAccount().getId() + ", Not found for Invalid");
            throw new InvalidAccountException("Account for ID -> "
                    + findAccountCommand.getOrdersAccount().getId() + ", Not found for Invalid");
        }
    }

}