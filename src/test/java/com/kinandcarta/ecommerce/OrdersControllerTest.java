package com.kinandcarta.ecommerce;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kinandcarta.ecommerce.clients.AccountServiceClient;
import com.kinandcarta.ecommerce.entities.*;
import com.kinandcarta.ecommerce.exceptions.InvalidAccountException;
import com.kinandcarta.ecommerce.exceptions.MissingAccountException;
import com.kinandcarta.ecommerce.exceptions.MissingAddressException;
import com.kinandcarta.ecommerce.exceptions.OrdersNotFoundException;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


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
            .accountRefId("12345")
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
            .accountRefId("12345")
            .firstName("DukeFirstName")
            .lastName("DukeLastName")
            .emailAddress(null)
            .addresses(
                    Set.of(ordersAddress)).build();
    OrdersAccount ordersAccountFirstLastNull = OrdersAccount.builder()
            .id(1L)
            .accountRefId("12345")
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

    Orders minimumOrderNullAddress = Orders.builder()
            .id(12L)
            .ordersAccount(ordersAccount)
            .ordersShippingAddress(null)
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
            .ordersShippingAddress(ordersAddress)
            .orderLineItems(Set.of(firstProduct, secondProduct)).build();
    @Mock
    OrdersHandler ordersHandler;
    @Mock
    AccountServiceClient accountServiceClient;
    final String expectedAccountIdRef = "4f464483-a1f0-4ce9-a19e-3c0f23e84a67";
    final String path = "/accounts/" + expectedAccountIdRef;

    @Value("${commerce.clients.accounts.baseUrl}")
    String baseURL;

    @Value("${commerce.clients.accounts.findByAccountIdRefUrl}")
    String getAccountIdUri;
    static MockWebServer mockWebServer;
    ObjectMapper mapper;

    OrdersController controller;


    @BeforeEach
    void setUp() throws Exception {
        mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());

        initializeMockServer_HappyPath();

        controller = new OrdersController(ordersHandler, accountServiceClient);
    }


    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test void initClassUnderTest() {
        assertThat(controller).isNotNull();
        assertThat(ordersHandler).isNotNull();
        assertThat(accountServiceClient).isNotNull();
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
    void createOrderFails_forMissingAccount() throws Exception {
        mockServerFor_FailingUseCase(HttpStatus.BAD_REQUEST.value());

        // Null Account
        ResponseEntity<Orders> orderCreated = controller.create(minimumOrderNullAccount);
        assertThat(orderCreated).isEqualTo(ResponseEntity.badRequest().build());
    }
    @Test
    void createOrderFails_whenAccountIsMissing_Address() {
        verifyOrderNotCreated_whenAccount_orAccount_Address_IsNull(minimumOrderNullAddress);
        ResponseEntity<Orders> orderCreated = controller.create(minimumOrderNullAddress);
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

    @Test
    void shouldUpdate_Order() {
        // get it
        when(ordersHandler.update(1L, minimumOrder)).thenReturn(minimumOrder);
        ResponseEntity<Orders> toUpdate = controller.update(1L, minimumOrder);
        assertThat(toUpdate).isNotNull();
        assertThat(toUpdate.getBody()).isNotNull();
        // update
        Orders updatingOrder = toUpdate.getBody();
        updatingOrder.setOrderLineItems(new HashSet<>());
        updatingOrder.setOrderLineItems(Set.of(firstProduct, secondProduct));
        updatingOrder.setOrdersShippingAddress(ordersAddress);
        updatingOrder.setUpdateDateTime(Instant.now(Clock.systemUTC()));
        updatingOrder.getOrdersShippingAddress().setShippingAddress(Boolean.TRUE);
        // verify updates
        assertThat(minimumOrder.getOrdersAccount()).isEqualTo(updatingOrder.getOrdersAccount());
        assertThat(minimumOrder.getOrdersShippingAddress()).isNotNull();
        assertThat(minimumOrder.getOrdersShippingAddress().isShippingAddress()).isTrue();
    }

    @Test
    void shouldDelete_Order() {
        when(ordersHandler.create(minimumOrder)).thenReturn(minimumOrder);
        doNothing().when(ordersHandler).delete(minimumOrder.getId());
        ResponseEntity<Orders> orderCreated = controller.create(minimumOrder);
        assertThat(orderCreated).isNotNull();
        assertThat(orderCreated.getBody()).isNotNull();
        assertThat(orderCreated.getBody().getId()).isEqualTo(1L);

        controller.delete(1L);

        verify(ordersHandler, times(1)).delete(1L);
    }

    @Test void shouldFindAllOrderLineItems_forGivenOrdersId() {
        Set<OrderLineItems> itemsFoundry = findOrderLineItems_byOrderId(1L);
        assertThat(itemsFoundry).isNotNull().containsExactlyInAnyOrder(firstProduct, secondProduct);
    }

    @Test void shouldFindAll_Orders() {
        assertThat(findAllOrders()).contains(davidKingMoonMousePad);
    }

    @Test void shouldFindOrders_byId() {
        assertThat(findSingleOrders_byId(1L)).isNotNull();
    }

    @Test void shouldFindAllOrders_byAccountId() {
        when(ordersHandler.findOrdersForAccountId(100L)).thenReturn(Set.of(davidKingMoonMousePad));
        ResponseEntity<Set<Orders>> ordersForAccount100 = controller.findAll(100L);
        assertThat(ordersForAccount100).isNotNull();
        assertThat(ordersForAccount100.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(ordersForAccount100.getBody()).isNotNull();
    }
    @Test void verifyOrderDetailsView_containsExpectedData() {
        /*
        :Requirements

        . Get order details for an order.
            a. URL: /orders/{id}

        i. Order number

        ii. Shipping address

        iii. Total price

        iv. Line items
            1. Product name
            2. Quantity

        v. Shipments
            1. Order Line Items
            2. Shipped Date
            3. Delivery Date
         */
        assertThat(findSingleOrders_DetailsView_byId(1L)).isNotNull();

        /*
        System.out.printf("ORDER DETAILS VIEW: %s%n",findSingleOrders_DetailsView_byId(1L));

        TEST OUTPUT:

        ORDER DETAILS VIEW: AccountOrderDetails(
         orderId=1
         orderNumber=ord-659b989b-f56c-4e14-a279-1fc345569b2c
         shippingAddressDTO=ShippingAddressDTO(
                 id=100
                 address1=100
                 address2=
                 city=Food Forest City
                 state=null
                 province=
                 postalCode=33000
                 country=US)
         totalPrice=33.99
         lineItems=[OrderLineItems(
                 id=3
                 orderId=1
                 productId=1
                 quantity=2
                 price=10
                 totalPrice=20
                 createDateTime=null
                 updateDateTime=null)
                 OrderLineItems(id=4
                 orderId=1
                 productId=3
                 quantity=1
                 price=13.99
                 totalPrice=13.99
                 createDateTime=null
                 updateDateTime=null)]
         */
        AccountOrderDetails detailsView = findSingleOrders_DetailsView_byId(1L);
        assertThat(detailsView).hasFieldOrPropertyWithValue("orderId", 1L)
                .hasFieldOrPropertyWithValue("totalPrice", new BigDecimal("33.99"));
    }


    @Test
    void shouldReturnBadRequest_whenDataIntegrityViolationException_Occurs() {
        when(ordersHandler.create(minimumOrder)).thenThrow(DataIntegrityViolationException.class);
        ResponseEntity<Orders> orderNotCreated = controller.create(minimumOrder);
        assertThat(orderNotCreated).isNotNull();
        assertThat(orderNotCreated.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));
    }

    // HELPER METHODS
    private void initializeMockServer_HappyPath() throws IOException {
        mockWebServer = new MockWebServer();
        accountServiceClient = new AccountServiceClient("http://localhost:9999", path);

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
        mockWebServer.start(9999);
    }
    private static void mockServerFor_FailingUseCase(final int intStatusCode) throws IOException {
        Dispatcher dispatcherMock = new Dispatcher() {
            @NotNull
            @Override
            public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) throws InterruptedException {
                Objects.requireNonNull(recordedRequest, "Request was null for MockServer");
                return new MockResponse().setResponseCode(intStatusCode);
            }
        };

        mockWebServer.setDispatcher(dispatcherMock);
        mockWebServer.start(9999);
    }


    // - BASE_CASE__create_minimum_order, verifies the minimum fields are present.
    private Orders createOrder_VerifyMinimumFields(final Orders orderCreateCommand) {
        when(ordersHandler.create(orderCreateCommand)).thenReturn(orderCreateCommand);
        ResponseEntity<Orders> orderCreated = controller.create(orderCreateCommand);
        assertThat(orderCreated).isNotNull();
        assertThat(orderCreated.getBody()).isNotNull();
        return orderCreated.getBody();
    }

    private Set<OrderLineItems> findOrderLineItems_byOrderId(final Long itemsForOrderId) {
        when(ordersHandler.findOrderLineItemsFor(itemsForOrderId)).thenReturn(Set.of(firstProduct, secondProduct));
        ResponseEntity<Set<OrderLineItems>> findAllOrderLineItemsForOrderId = controller.findOrderLineItemsFor(itemsForOrderId);
        assertThat(findAllOrderLineItemsForOrderId).isNotNull();
        assertThat(findAllOrderLineItemsForOrderId.getBody()).isNotNull().hasSize(2);
        return findAllOrderLineItemsForOrderId.getBody();
    }

    private Set<Orders> findAllOrders() {
        when(ordersHandler.findAll()).thenReturn(Set.of(davidKingMoonMousePad));
        ResponseEntity<Set<Orders>> ordersSet = controller.findAll(null);
        assertThat(ordersSet).isNotNull();
        assertThat(ordersSet.getBody()).isNotNull().hasSize(1);
        return ordersSet.getBody();
    }

    private Orders findSingleOrders_byId(final Long orderId) {
        // Expecting id of 1L
        if (!orderId.equals(davidKingMoonMousePad.getId())) throw new OrdersNotFoundException("Order not found.");
        when(ordersHandler.findById(orderId)).thenReturn(davidKingMoonMousePad);
        ResponseEntity<Orders> ordersSet = controller.findById(orderId);
        assertThat(ordersSet).isNotNull();
        assertThat(ordersSet.getBody()).isNotNull();
        return ordersSet.getBody();
    }
    private AccountOrderDetails findSingleOrders_DetailsView_byId(final Long orderId) {
        // Expecting id of 1L
        if (!orderId.equals(davidKingMoonMousePad.getId())) throw new OrdersNotFoundException("Order not found.");
        when(ordersHandler.findByIdDetailedView(orderId)).thenReturn(AccountOrderDetails.builder()
                        .lineItems(davidKingMoonMousePad.getOrderLineItems())
                        .orderId(davidKingMoonMousePad.getId())
                        .orderNumber(davidKingMoonMousePad.getOrderNumber())
                        .shippingAddressDTO(
                                ShippingAddressDTO.builder() // Objects.requiresNotNullWithDefault ...
                                        .id(davidKingMoonMousePad.getOrdersShippingAddress().getId())
                                        .address1(davidKingMoonMousePad.getOrdersShippingAddress().getAddress1())
                                        .address2(davidKingMoonMousePad.getOrdersShippingAddress().getAddress2())
                                        .city(davidKingMoonMousePad.getOrdersShippingAddress().getCity())
                                        .postalCode(davidKingMoonMousePad.getOrdersShippingAddress().getPostalCode())
                                        .province(davidKingMoonMousePad.getOrdersShippingAddress().getProvince())
                                        .country(davidKingMoonMousePad.getOrdersShippingAddress().getCountry())
                                        .build())
                        .totalPrice(davidKingMoonMousePad.sumLineItems(davidKingMoonMousePad.getOrderLineItems()))
                .build());
        ResponseEntity<AccountOrderDetails> ordersSet = controller.findByIdDetailedView(orderId);
        assertThat(ordersSet).isNotNull();
        assertThat(ordersSet.getBody()).isNotNull();
        return ordersSet.getBody();
    }


    private void verifyOrderNotCreated_whenAccount_orAccountId_IsNull(final Orders invalidOrderCreateCommand) {
        when(ordersHandler.create(invalidOrderCreateCommand)).thenThrow(MissingAccountException.class);
    }
    private void verifyOrderNotCreated_whenAccount_FirstLastName_or_Email_IsNull(final Orders invalidOrderCreateCommand) {
        when(ordersHandler.create(invalidOrderCreateCommand)).thenThrow(InvalidAccountException.class);
    }
    private void verifyOrderNotCreated_whenAccount_orAccount_Address_IsNull(final Orders minimumOrderNullAddress) {
        when(ordersHandler.create(minimumOrderNullAddress)).thenThrow(MissingAddressException.class);
    }

}