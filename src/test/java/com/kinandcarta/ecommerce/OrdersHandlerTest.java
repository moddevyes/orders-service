package com.kinandcarta.ecommerce;

import com.kinandcarta.ecommerce.entities.*;
import com.kinandcarta.ecommerce.exceptions.InvalidAccountException;
import com.kinandcarta.ecommerce.exceptions.MissingAccountException;
import com.kinandcarta.ecommerce.exceptions.MissingAddressException;
import com.kinandcarta.ecommerce.infrastructure.OrderLineItemsRepository;
import com.kinandcarta.ecommerce.infrastructure.OrdersAccountRepository;
import com.kinandcarta.ecommerce.infrastructure.OrdersAddressRepository;
import com.kinandcarta.ecommerce.infrastructure.OrdersRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.when;


@Slf4j
class OrdersHandlerTest {

    private static final int FOUR_ORDERS_TWO_ITEMS_EACH = 4;
    TestEntityManager entityManager = Mockito.mock(TestEntityManager.class);

    OrdersRepository ordersRepository = Mockito.mock(OrdersRepository.class);
    OrderLineItemsRepository orderLineItemsRepository = Mockito.mock(OrderLineItemsRepository.class);
    OrdersAccountRepository ordersAccountRepository = Mockito.mock(OrdersAccountRepository.class);
    OrdersAddressRepository ordersAddressRepository = Mockito.mock(OrdersAddressRepository.class);
    OrdersHandler ordersHandler;

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

    OrdersAccount ordersAccountNullAccountAddress = OrdersAccount.builder()
            .id(100L)
            .firstName("DukeFirstName")
            .lastName("DukeLastName")
            .emailAddress("dukefirst.last@enjoy.com")
            .addresses(null).build();
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
            .ordersShippingAddress(ordersAddress)
            .orderNumber(orderNumber)
            .orderDate(Instant.now(Clock.systemUTC())).build();

    Orders minimumOrderNullAccountAccount = Orders.builder()
            .id(1L)
            .ordersAccount(ordersAccountNullAccountAddress)
            .ordersShippingAddress(ordersAddress)
            .orderNumber(orderNumber)
            .orderDate(Instant.now(Clock.systemUTC())).build();

    Orders minimumOrderNullShippingAddress = Orders.builder()
            .id(12L)
            .ordersAccount(ordersAccount)
            .ordersShippingAddress(null)
            .orderNumber(orderNumber)
            .orderDate(Instant.now(Clock.systemUTC())).build();

    Orders minimumOrderNullEmailAddress = Orders.builder()
            .id(1L)
            .ordersAccount(ordersAccountEmailNull)
            .ordersShippingAddress(ordersAddress)
            .orderNumber(orderNumber)
            .orderDate(Instant.now(Clock.systemUTC())).build();
    Orders minimumOrder = Orders.builder()
            .id(1L)
            .ordersAccount(ordersAccount)
            .ordersShippingAddress(ordersAddress)
            .orderNumber(orderNumber)
            .orderLineItems(new HashSet<>())
            .orderDate(Instant.now(Clock.systemUTC())).build();

    Orders minimumOrderAccountFirstLastNull = Orders.builder()
            .id(1L)
            .ordersAccount(ordersAccountFirstLastNull)
            .ordersShippingAddress(ordersAddress)
            .orderNumber(orderNumber)
            .orderDate(Instant.now(Clock.systemUTC())).build();
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

    Orders davidKingMoonMousePad = Orders.builder()
            .id(1L)
            .ordersAccount(ordersAccount)
            .ordersShippingAddress(ordersAddress)
            .orderNumber(orderNumber)
            .orderDate(Instant.now(Clock.systemUTC()))
            .orderLineItems(Set.of(firstProduct, secondProduct)).build();
    Orders davidKingMoonMousePad_Order2 = Orders.builder()
            .id(2L)
            .ordersAccount(ordersAccount)
            .ordersShippingAddress(ordersAddress)
            .orderNumber(orderNumber+"000")
            .orderDate(Instant.now(Clock.systemUTC()))
            .orderLineItems(Set.of(firstProduct, secondProduct)).build();
    Orders davidKingMoonMousePad_Order3 = Orders.builder()
            .id(3L)
            .ordersAccount(ordersAccount)
            .ordersShippingAddress(ordersAddress)
            .orderNumber(orderNumber+"333")
            .orderDate(Instant.now(Clock.systemUTC()))
            .orderLineItems(Set.of(firstProduct, secondProduct)).build();
    Orders davidKingMoonMousePad_Order4 = Orders.builder()
            .id(4L)
            .ordersAccount(ordersAccount)
            .ordersShippingAddress(ordersAddress)
            .orderNumber(orderNumber+"444")
            .orderDate(Instant.now(Clock.systemUTC()))
            .orderLineItems(Set.of(firstProduct, secondProduct)).build();

    @BeforeEach void setUp() {
        ordersHandler = new OrdersHandler(ordersRepository, orderLineItemsRepository,
                ordersAccountRepository, ordersAddressRepository);

        // Orders
        entityManager.persist(minimumOrder);
        entityManager.persist(davidKingMoonMousePad);
        entityManager.persist(davidKingMoonMousePad_Order2);
        entityManager.persist(davidKingMoonMousePad_Order3);
        entityManager.persist(davidKingMoonMousePad_Order4);
    }

    @AfterEach void tearDown() {
        entityManager.flush();
    }


    @Test
    // Null First and Last Name -> Invalid Account Exception
    void shouldFail_toCreateNewOrder_whenAccount_FirstAndLastName_isNull() {
        when(ordersRepository.save(minimumOrderAccountFirstLastNull)).thenThrow(InvalidAccountException.class);
        assertThatThrownBy(() ->
            ordersHandler.create(minimumOrderAccountFirstLastNull)).isInstanceOf(InvalidAccountException.class)
                .hasStackTraceContaining("com.kinandcarta.ecommerce.exceptions.InvalidAccountException");
    }

    @Test
    void shouldFail_toCreateNewOrder_whenShippingAddress_isNull() {
        when(ordersRepository.save(minimumOrderNullShippingAddress)).thenThrow(MissingAddressException.class);
        assertThatThrownBy(() ->
                ordersHandler.create(minimumOrderNullShippingAddress)).isInstanceOf(MissingAddressException.class);
    }

    @Test
    // Null Account -> Missing Account Exception
    void shouldFail_toCreateNewOrder_whenAccount_isNull() {
        when(ordersRepository.save(minimumOrderNullAccount)).thenThrow(MissingAccountException.class);
        assertThatThrownBy(() ->
            ordersHandler.create(minimumOrderNullAccount)).isInstanceOf(MissingAccountException.class)
                .hasStackTraceContaining("com.kinandcarta.ecommerce.exceptions.MissingAccountException");
    }

    @Test
    // Null Email Address -> Invalid Account Exception
    void shouldFail_toCreateNewOrder_whenEmailAddress_isNull() {
        when(ordersRepository.save(minimumOrderNullEmailAddress)).thenThrow(InvalidAccountException.class);
        assertThatThrownBy(() ->
                ordersHandler.create(minimumOrderNullEmailAddress)).isInstanceOf(InvalidAccountException.class)
                .hasStackTraceContaining("com.kinandcarta.ecommerce.exceptions.InvalidAccountException");
    }

    @Test
    // Null Address -> Missing Address Exception
    void shouldFail_toCreateNewOrder_whenAccountAddress_isNull() {
        when(ordersRepository.save(minimumOrderNullAccountAccount)).thenThrow(MissingAddressException.class);
        assertThatThrownBy(() ->
            ordersHandler.create(minimumOrderNullAccountAccount)).isInstanceOf(MissingAddressException.class);
    }

    @Test
    void shouldCreateNewOrder_withMinimumFields() {

        minimumOrder.setOrderLineItems(new HashSet<>());
        minimumOrder.getOrderLineItems().add(firstProduct);

        // create
        // Save orders account
        when(ordersAccountRepository.save(ordersAccount)).thenReturn(ordersAccount);
        // Save orders address
        when(ordersAddressRepository.save(ordersAddress)).thenReturn(ordersAddress);

        when(ordersRepository.save(minimumOrder)).thenReturn(minimumOrder);



        Orders createOrderCommand = ordersHandler.create(minimumOrder);

        assertThat(createOrderCommand).isNotNull();
        assertThat(createOrderCommand.getId()).isEqualTo(1L);
        assertThat(createOrderCommand.getOrderNumber()).isEqualTo(orderNumber);
    }

    @Test
    void should_UpdateOrder() {
        // create
        when(ordersRepository.save(davidKingMoonMousePad_Order2)).thenReturn(davidKingMoonMousePad_Order2);
        // exists
        when(ordersRepository.existsById(2L)).thenReturn(Boolean.TRUE);
        // find by id
        when(ordersRepository.getReferenceById(2L)).thenReturn(davidKingMoonMousePad_Order2);
        // update
        Orders update = ordersHandler.update(2L, davidKingMoonMousePad_Order2);
        update.setOrderLineItems(Set.of(firstProduct, secondProduct));
        update.setOrderDate(Instant.now(Clock.systemUTC()));
        update.setTotalPrice(update.sumLineItems(update.getOrderLineItems()));
        update.setUpdateDateTime(Instant.now(Clock.systemUTC()));
        update.setCreateDateTime(Instant.now(Clock.systemUTC()));
        update.setOrdersShippingAddress(ordersAddress);
        assertThat(update).isNotNull();
        System.out.printf("Updates?:     %s%n", Arrays.toString(update.getOrderLineItems().toArray()));
    }

    @Test
    void shouldDelete_Order() {
        when (ordersRepository.existsById(1L)).thenReturn(Boolean.TRUE);
        willDoNothing().given(ordersRepository).deleteById(1L);
        ordersHandler.delete(1L);
        assertThat(ordersHandler.findById(1L)).isNull();
    }

    @Test
    void shouldFindOrder_byId() {
        when (ordersRepository.existsById(1L)).thenReturn(Boolean.TRUE);
        when (ordersRepository.getReferenceById(1L)).thenReturn(davidKingMoonMousePad);
        Orders orderOneFound = ordersHandler.findById(1L);
        assertThat(orderOneFound).isNotNull();
        assertThat(orderOneFound.getOrderNumber()).isEqualTo(orderNumber);
        assertThat(orderOneFound.getId()).isEqualTo(1L);
    }

    @Test
    void shouldFindAllOrders() {
        when(ordersRepository.findAll())
                .thenReturn(List.of(
                        davidKingMoonMousePad, davidKingMoonMousePad_Order2, davidKingMoonMousePad_Order3, davidKingMoonMousePad_Order4
                ));
        Set<Orders> findingAll = ordersHandler.findAll();
        assertThat(findingAll).isNotNull()
                .hasSize(FOUR_ORDERS_TWO_ITEMS_EACH);
    }

    @Test void shouldFindOrderDetailsById_andReturn_CustomDetailsView() {
        AccountOrderDetails accountOrderDetails =
            AccountOrderDetails.builder()
                    .orderId(davidKingMoonMousePad.getId())
                    .lineItems(davidKingMoonMousePad.getOrderLineItems())
                    .totalPrice(davidKingMoonMousePad.getTotalPrice())
                    .orderNumber(davidKingMoonMousePad.getOrderNumber())
                    .shippingAddressDTO(null) // Client, davidKingMoonMousePad.getShippingAddressId()
                    .build();

        // base state
        when (ordersRepository.existsById(1L)).thenReturn(Boolean.TRUE);
        when (ordersRepository.getReferenceById(1L)).thenReturn(davidKingMoonMousePad);

        // custom view for order-details-view
        assertThat(accountOrderDetails).isNotNull();
        assertThat(accountOrderDetails.getOrderNumber()).isEqualTo(orderNumber);
        assertThat(accountOrderDetails.getOrderId()).isEqualTo(davidKingMoonMousePad.getId());
        assertThat(accountOrderDetails.getTotalPrice()).isEqualTo(davidKingMoonMousePad.getTotalPrice());
        assertThat(accountOrderDetails.getLineItems()).isEqualTo(davidKingMoonMousePad.getOrderLineItems());

    }

}
