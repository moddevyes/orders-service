package com.kinandcarta.ecommerce;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = OrdersController.class)
@Import(OrdersHandler.class)
class OrdersServiceIntegrationTests {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    OrdersRepository ordersRepository;

    @MockBean
    OrderLineItemsRepository orderLineItemsRepository;

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

    @BeforeEach
    void setUp() {
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void shouldCreateAnOrder_withMinimumFields() throws Exception {
        Orders save = Orders.builder()
                .id(1L)
                .ordersAccount(ordersAccount)
                .orderNumber(orderNumber)
                .orderDate(Instant.now())
                .build();

        final String json = mapper.writeValueAsString(save);

        given(ordersRepository.save(save)).willReturn(save);

        when(ordersRepository.getReferenceById(1L)).thenReturn(save);

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
