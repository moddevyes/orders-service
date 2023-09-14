package com.kinandcarta.ecommerce;

import com.kinandcarta.ecommerce.clients.AccountServiceClient;
import com.kinandcarta.ecommerce.entities.AccountOrderDetails;
import com.kinandcarta.ecommerce.entities.OrderLineItems;
import com.kinandcarta.ecommerce.entities.Orders;
import com.kinandcarta.ecommerce.entities.ShippingAddressDTO;
import com.kinandcarta.ecommerce.infrastructure.OrdersAccountRepository;
import com.kinandcarta.ecommerce.infrastructure.OrdersAddressRepository;
import com.kinandcarta.ecommerce.infrastructure.OrdersLineItemsRepository;
import com.kinandcarta.ecommerce.infrastructure.OrdersRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.kinandcarta.ecommerce.TestModels.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


//@Disabled
//@SpringBootTest(classes = {OrdersServiceApplication.class}, value = {"server.port:0", "eureka.client.enabled:false"})
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Slf4j
class OrdersServiceIntegrationTests {
    EntityManager entityManager = Mockito.mock(EntityManager.class);
    MockMvc mockMvc;

    OrdersHandler ordersHandler;

    @Mock
    OrdersRepository ordersRepository;
    @Mock
    OrdersLineItemsRepository ordersLineItemsRepository;
    @Mock
    OrdersAccountRepository ordersAccountRepository;
    @Mock
    OrdersAddressRepository ordersAddressRepository;
    @Mock
    AccountServiceClient mockAccountServiceClient;


    @BeforeEach
    void setUp() {
        ordersHandler = new OrdersHandler(ordersRepository,ordersLineItemsRepository,ordersAccountRepository,ordersAddressRepository);
        this.mockMvc = MockMvcBuilders.standaloneSetup(new OrdersController(ordersHandler, mockAccountServiceClient)).build();
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

        when(mockAccountServiceClient.findByAccountIdRef(expectedAccountIdRef)).thenReturn(ordersAccount);
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

        when(mockAccountServiceClient.findByAccountIdRef(expectedAccountIdRef)).thenReturn(ordersAccount);
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

    @Test
    void shouldFindAllOrders_byAccountId_orderedByDate() throws Exception {
        /*
        HTTP response
     Content type = application / json
     Body = [{
     	"id": 1,
     	"ordersAccount": {
     		"id": 100,
     		"firstName": "DukeFirstName",
     		"lastName": "DukeLastName",
     		"emailAddress": "dukefirst.last@enjoy.com",
     		"addresses": [{
     			"id": 100,
     			"address1": "100",
     			"address2": "",
     			"city": "Food Forest City",
     			"state": "FL",
     			"province": "",
     			"postalCode": "33000",
     			"country": "US",
     			"createDateTime": null,
     			"updateDateTime": null,
     			"shippingAddress": false
     		}],
     		"createDateTime": null,
     		"updateDateTime": null
     	},
     	"orderNumber": "ord-950738f3-2c60-46f7-baff-6d6495f454f4",
     	"orderDate": "2023-09-11T17:17:32.920280700Z",
     	"ordersShippingAddress": null,
     	"totalPrice": 33.99,
     	"createDateTime": null,
     	"updateDateTime": null,
     	"orderLineItems": [{
     		"id": null,
     		"orderId": 1,
     		"productId": 1,
     		"quantity": 2,
     		"price": 10,
     		"totalPrice": 20,
     		"createDateTime": null,
     		"updateDateTime": null
     	}, {
     		"id": null,
     		"orderId": 1,
     		"productId": 3,
     		"quantity": 1,
     		"price": 13.99,
     		"totalPrice": 13.99,
     		"createDateTime": null,
     		"updateDateTime": null
     	}]
     }]

         */
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
        entityManager.persist(ordersAccount);
        entityManager.persist(ordersAddress);
        entityManager.persist(toFind);

        when(ordersRepository.findAllByOrdersAccountIdOrderByOrderDateDesc(100L)).thenReturn(List.of(toFind));
        mockMvc.perform(MockMvcRequestBuilders.get("/orders").param("accountId", "100")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].ordersAccount").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].orderNumber").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].orderDate").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].totalPrice").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].orderDate").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].orderLineItems").exists());
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
        when(ordersRepository.existsById(1L)).thenReturn(Boolean.TRUE);
        when(ordersRepository.getReferenceById(1L)).thenReturn(foundOrder);
    }
}
