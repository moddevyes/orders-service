package com.kinandcarta.ecommerce;

import com.kinandcarta.ecommerce.entities.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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

    Orders minimumOrderNullEmailAddress = Orders.builder()
            .id(1L)
            .ordersAccount(ordersAccountEmailNull)
            .orderNumber(orderNumber)
            .orderDate(Instant.now(Clock.systemUTC())).build();
    Orders minimumOrder = Orders.builder()
            .id(1L)
            .ordersAccount(ordersAccount)
            .orderNumber(orderNumber)
            .orderDate(Instant.now(Clock.systemUTC())).build();

    Orders minimumOrderAccountFirstLastNull = Orders.builder()
            .id(1L)
            .ordersAccount(ordersAccountFirstLastNull)
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

    @BeforeEach void setUp() {
        ordersHandler = new OrdersHandler(ordersRepository, orderLineItemsRepository);
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
                .hasStackTraceContaining("com.kinandcarta.ecommerce.InvalidAccountException");
    }

    @Test
    // Null Account -> Missing Account Exception
    void shouldFail_toCreateNewOrder_whenAccount_isNull() {
        when(ordersRepository.save(minimumOrderNullAccount)).thenThrow(MissingAccountException.class);
        assertThatThrownBy(() ->
            ordersHandler.create(minimumOrderNullAccount)).isInstanceOf(MissingAccountException.class)
                .hasStackTraceContaining("com.kinandcarta.ecommerce.MissingAccountException");
    }

    @Test
    // Null Email Address -> Invalid Account Exception
    void shouldFail_toCreateNewOrder_whenEmailAddress_isNull() {
        when(ordersRepository.save(minimumOrderNullEmailAddress)).thenThrow(InvalidAccountException.class);
        assertThatThrownBy(() ->
                ordersHandler.create(minimumOrderNullEmailAddress)).isInstanceOf(InvalidAccountException.class)
                .hasStackTraceContaining("com.kinandcarta.ecommerce.InvalidAccountException");
    }


    @Test void shouldCreateMultipleOrders_usingThe_SameAccount() {
        // first order with OrdersAccount -> .id(100L)
        when(ordersRepository.save(minimumOrder)).thenReturn(minimumOrder);

        // exists
        when(ordersRepository.existsById(1L)).thenReturn(Boolean.TRUE);
        // find by id
        when(ordersRepository.getReferenceById(1L)).thenReturn(minimumOrder);
//        when(ordersRepository.save(minimumOrderSameAccount)).thenReturn(minimumOrderSameAccount);

        // when-then, first order
        Orders createFirstOrder = ordersHandler.create(minimumOrder);
        assertThat(createFirstOrder).isNotNull();

        // Account used to create first order and upcoming second order
        OrdersAccount sameOrdersAccount = createFirstOrder.getOrdersAccount();
        assertThat(sameOrdersAccount).isNotNull();
        assertThat(sameOrdersAccount.getId()).isEqualTo(100L);

        // second order , has OrdersAccount -> .id(100L)
        Orders secondOrder = ordersHandler.create(davidKingMoonMousePad);
        assertThat(secondOrder).isNotNull();
    }

    @Test
    void shouldCreateNewOrder_withMinimumFields() {
        // create
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
