package com.kinandcarta.ecommerce;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kinandcarta.ecommerce.entities.*;
import com.kinandcarta.ecommerce.infrastructure.OrdersAccountRepository;
import com.kinandcarta.ecommerce.infrastructure.OrdersAddressRepository;
import com.kinandcarta.ecommerce.infrastructure.OrdersLineItemsRepository;
import com.kinandcarta.ecommerce.infrastructure.OrdersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = OrdersController.class)
@Import({OrdersHandler.class})
class OrdersServiceIntegrationTests {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    OrdersRepository ordersRepository;

    @MockBean
    OrdersLineItemsRepository ordersLineItemsRepository;
    @MockBean
    OrdersAccountRepository ordersAccountRepository;
    @MockBean
    OrdersAddressRepository ordersAddressRepository;

    ObjectMapper mapper = new ObjectMapper();

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
            .totalPrice(new BigDecimal("10"))
            .productId(1L)
            .build();

    OrderLineItems secondProduct = OrderLineItems.builder()
            .id(4L)
            .orderId(1L)
            .quantity(1)
            .price(new BigDecimal("10"))
            .totalPrice(new BigDecimal("10"))
            .productId(1L)
            .build();

    OrderLineItems thirdProduct = OrderLineItems.builder()
            .id(5L)
            .orderId(1L)
            .quantity(1)
            .price(new BigDecimal("13.74"))
            .totalPrice(new BigDecimal("13.74"))
            .productId(5L)
            .build();


    @BeforeEach
    void setUp() {
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void shouldCreateAnOrder_withMinimumFields() throws Exception {
        Orders minimumOrder = Orders.builder()
                .id(1L)
                .ordersAccount(ordersAccount)
                .ordersShippingAddress(ordersAddress)
                .orderNumber(orderNumber)
                .orderLineItems(new HashSet<>())
                .orderDate(Instant.now(Clock.systemUTC())).build();

        final String json = mapper.writeValueAsString(minimumOrder);

        when(ordersRepository.save(minimumOrder)).thenReturn(minimumOrder);

        mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content((json)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    @Test
    void shouldCreateAnOrder_withOrderLineItems() throws Exception {
        Orders minimumOrder = Orders.builder()
                .id(1L)
                .ordersAccount(ordersAccount)
                .ordersShippingAddress(ordersAddress)
                .orderNumber(orderNumber)
                .orderLineItems(Set.of(firstProduct, secondProduct, thirdProduct))
                .orderDate(Instant.now(Clock.systemUTC())).build();

        final String json = mapper.writeValueAsString(minimumOrder);

        when(ordersRepository.save(minimumOrder)).thenReturn(minimumOrder);

        mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content((json)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test void shouldUpdateAnOrder() throws Exception {
        final String json = mapper.writeValueAsString(
                Orders.builder()
                        .id(20L)
                        .ordersAccount(ordersAccount)
                        .orderNumber(orderNumber)
                        .orderDate(Instant.now())
                        .build());

        when(ordersRepository.save(Orders.builder()
                .id(20L)
                .ordersAccount(ordersAccount)
                .orderNumber(orderNumber)
                .orderDate(Instant.now())
                .build())).thenReturn(Orders.builder()
                .id(20L)
                .ordersAccount(ordersAccount)
                .orderNumber(orderNumber)
                .orderDate(Instant.now())
                .build());

        when(ordersRepository.existsById(20L)).thenReturn(Boolean.TRUE);

        when(ordersRepository.getReferenceById(20L)).thenReturn(
                Orders.builder()
                        .id(20L)
                        .ordersAccount(ordersAccount)
                        .orderNumber(orderNumber)
                        .orderDate(Instant.now())
                        .build());

        mockMvc.perform(MockMvcRequestBuilders.put("/orders/{id}", 20L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content((json)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test void shouldDeleteAnOrder() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/orders/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test void shouldFindAnOrder_byId() throws Exception {
        Orders toFind = Orders.builder()
                .id(1L)
                .ordersAccount(ordersAccount)
                .orderNumber(orderNumber)
                .orderLineItems(
                        Set.of(
                                OrderLineItems.builder()
                                        .orderId(1L)
                                        .quantity(2)
                                        .price(new BigDecimal("10"))
                                        .productId(1L)
                                        .build(),
                                OrderLineItems.builder()
                                        .orderId(1L)
                                        .quantity(1)
                                        .price(new BigDecimal("13.99"))
                                        .productId(3L)
                                        .build()
                        )
                )
                .orderDate(Instant.now()).build();

        toFind.sumLineItems(toFind.getOrderLineItems());

        when(ordersRepository.save(toFind)).thenReturn(toFind);
        when(ordersRepository.existsById(1L)).thenReturn(Boolean.TRUE);
        when(ordersRepository.getReferenceById(1L)).thenReturn(toFind);

        mockMvc.perform(MockMvcRequestBuilders.get("/orders/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test void shouldFind_AllOrders() throws Exception {
        OrderLineItems firstProduct = OrderLineItems.builder()
                .orderId(1L)
                .quantity(2)
                .price(new BigDecimal("10"))
                .productId(1L)
                .build();
        OrderLineItems secondProduct = OrderLineItems.builder()
                .orderId(1L)
                .quantity(1)
                .price(new BigDecimal("13.99"))
                .productId(3L)
                .build();

        Orders davidKingMoonMousePad = Orders.builder()
                .id(1L)
                .ordersAccount(ordersAccount)
                .orderNumber(orderNumber)
                .orderLineItems(Set.of(firstProduct, secondProduct)).build();

        when(ordersRepository.save(davidKingMoonMousePad)).thenReturn(davidKingMoonMousePad);
        when (ordersRepository.findAll()).thenReturn(List.of(davidKingMoonMousePad));

        mockMvc.perform(MockMvcRequestBuilders.get("/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test void shouldFindOrders_forOrderId() throws Exception {
        Orders toFind = Orders.builder()
                .id(1L)
                .ordersAccount(ordersAccount)
                .orderNumber(orderNumber)
                .orderLineItems(
                        Set.of(
                                OrderLineItems.builder()
                                        .orderId(1L)
                                        .quantity(2)
                                        .price(new BigDecimal("10"))
                                        .productId(1L)
                                        .build(),
                                OrderLineItems.builder()
                                        .orderId(1L)
                                        .quantity(1)
                                        .price(new BigDecimal("13.99"))
                                        .productId(3L)
                                        .build()
                        )
                )
                .orderDate(Instant.now()).build();

        toFind.sumLineItems(toFind.getOrderLineItems());

        when(ordersRepository.save(toFind)).thenReturn(toFind);
        when (ordersRepository.existsById(1L)).thenReturn(Boolean.TRUE);
        when(ordersRepository.getReferenceById(1L)).thenReturn(toFind);
        mockMvc.perform(MockMvcRequestBuilders.get("/orders/{id}/lines", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test void shouldFindOrderDetailsById_andReturn_CustomDetailsView() throws Exception {

        whenConditionsFor_FindByIdOrders(findByIdOrders());

        mockMvc.perform(MockMvcRequestBuilders.get("/orders/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test void shouldReturnAccountOrderDetails_CustomView() throws Exception {
        // base state
        Orders davidKingMoonMousePad = Orders.builder()
                .id(1L)
                .ordersAccount(ordersAccount)
                .ordersShippingAddress(ordersAddress)
                .orderNumber(orderNumber)
                .orderDate(Instant.now(Clock.systemUTC()))
                .orderLineItems(Set.of(firstProduct)).build();

        when (ordersRepository.existsById(1L)).thenReturn(Boolean.TRUE);
        when(ordersRepository.getReferenceById(1L)).thenReturn(davidKingMoonMousePad);

        AccountOrderDetails accountOrderDetails =
                AccountOrderDetails.builder()
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
                                        .build()).build();

        mockMvc.perform(MockMvcRequestBuilders.get("/orders/{id}/details", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        /*
        JSON response:
            {
                "orderId": 1,
                "orderNumber": "ord-064311f0-3c59-46df-b304-5bd1b513a59b",
                "totalPrice": 0,
                "lineItems": [{
                    "id": 3,
                    "orderId": 1,
                    "productId": 1,
                    "quantity": 2,
                    "price": 10,
                    "totalPrice": 10,
                    "createDateTime": null,
                    "updateDateTime": null
                }],
                "shippingAddress": {
                    "id": 100,
                    "address1": "100",
                    "address2": "",
                    "city": "Food Forest City",
                    "state": null,
                    "province": "",
                    "postalCode": "33000",
                    "country": "US"
                }
            }
         */
    }

    private Orders findByIdOrders() {
        Orders toFind = Orders.builder()
                .id(1L)
                .ordersAccount(ordersAccount)
                .orderNumber(orderNumber)
                .orderLineItems(
                        Set.of(
                                OrderLineItems.builder()
                                        .orderId(1L)
                                        .quantity(2)
                                        .price(new BigDecimal("10"))
                                        .productId(1L)
                                        .build(),
                                OrderLineItems.builder()
                                        .orderId(1L)
                                        .quantity(1)
                                        .price(new BigDecimal("13.99"))
                                        .productId(3L)
                                        .build()
                        )
                )
                .orderDate(Instant.now()).build();

        toFind.sumLineItems(toFind.getOrderLineItems());
        return toFind;
    }
    private void whenConditionsFor_FindByIdOrders(final Orders foundOrder) {
        when(ordersRepository.save(foundOrder)).thenReturn(foundOrder);
        when(ordersRepository.existsById(1L)).thenReturn(Boolean.TRUE);
        when(ordersRepository.getReferenceById(1L)).thenReturn(foundOrder);
    }
}
