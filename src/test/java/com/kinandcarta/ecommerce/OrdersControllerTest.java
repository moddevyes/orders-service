package com.kinandcarta.ecommerce;

import com.kinandcarta.ecommerce.entities.OrderLineItems;
import com.kinandcarta.ecommerce.entities.Orders;
import com.kinandcarta.ecommerce.entities.OrdersAccount;
import com.kinandcarta.ecommerce.entities.OrdersAddress;
import com.kinandcarta.ecommerce.exceptions.InvalidAccountException;
import com.kinandcarta.ecommerce.exceptions.MissingAccountException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@Slf4j
/*
    Minimum Fields:

    "id", (not on create, id generated auto_increment)
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
@ExtendWith(MockitoExtension.class)
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

    OrdersAccount ordersAccountEmailNull = OrdersAccount.builder()
            .id(13L)
            .firstName("DukeFirstName")
            .lastName("DukeLastName")
            .emailAddress(null)
            .addresses(
                    Set.of(ordersAddress)).build();
    OrdersAccount ordersAccountFirstLastNull = OrdersAccount.builder()
            .id(1L)
            .firstName(null)
            .lastName(null)
            .emailAddress("dukefirst.last@enjoy.com")
            .addresses(
                    Set.of(ordersAddress)).build();

    Orders minimumOrderNullAccount = Orders.builder()
            .id(1L)
            .ordersAccount(null)
            .orderNumber(orderNumber)
            .orderDate(Instant.now(Clock.systemUTC())).build();

    Orders minimumOrderNoEmailAddress = Orders.builder()
            .id(1L)
            .ordersAccount(ordersAccountEmailNull)
            .orderNumber(orderNumber)
            .orderDate(Instant.now(Clock.systemUTC()))
            .orderLineItems(Set.of(firstProduct, secondProduct)).build();

    Orders minimumOrderNoFirstAndLastName = Orders.builder()
            .id(1L)
            .ordersAccount(ordersAccountFirstLastNull)
            .orderNumber(orderNumber)
            .orderDate(Instant.now(Clock.systemUTC()))
            .orderLineItems(Set.of(firstProduct, secondProduct)).build();
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
    @Mock
    OrdersHandler ordersHandler;

    OrdersController controller;


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
    void createOrderFails_forMissingAccount() {
        // Null Account
        verifyOrderNotCreated_whenAccount_orAccountId_IsNull(minimumOrderNullAccount);
        ResponseEntity<Orders> orderCreated = controller.create(minimumOrderNullAccount);
        assertThat(orderCreated).isEqualTo(ResponseEntity.badRequest().build());
    }

    @Test
    void createOrderFails_ForMissingAccount_EmailAddress() {
        verifyOrderNotCreated_whenAccount_FirstLastName_or_Email_IsNull(minimumOrderNoEmailAddress);
        ResponseEntity<Orders> orderCreated = controller.create(minimumOrderNoEmailAddress);
        assertThat(orderCreated).isEqualTo(ResponseEntity.badRequest().build());
    }

    @Test
    void createOrderFails_ForMissingAccount_FirstAndLastName() {
        verifyOrderNotCreated_whenAccount_FirstLastName_or_Email_IsNull(minimumOrderNoFirstAndLastName);
        ResponseEntity<Orders> orderCreated = controller.create(minimumOrderNoFirstAndLastName);
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

    private void verifyOrderNotCreated_whenAccount_orAccountId_IsNull(final Orders invalidOrderCreateCommand) {
        when(ordersHandler.create(invalidOrderCreateCommand)).thenThrow(MissingAccountException.class);
    }
    private void verifyOrderNotCreated_whenAccount_FirstLastName_or_Email_IsNull(final Orders invalidOrderCreateCommand) {
        when(ordersHandler.create(invalidOrderCreateCommand)).thenThrow(InvalidAccountException.class);
    }



}