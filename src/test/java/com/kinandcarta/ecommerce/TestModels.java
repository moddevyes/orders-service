package com.kinandcarta.ecommerce;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kinandcarta.ecommerce.entities.OrderLineItems;
import com.kinandcarta.ecommerce.entities.OrdersAccount;
import com.kinandcarta.ecommerce.entities.OrdersAddress;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public final class TestModels {
    public static final String expectedAccountIdRef = "4f464483-a1f0-4ce9-a19e-3c0f23e84a67";
    public static ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
    }

    public static final String orderNumber = "ord-" + UUID.randomUUID();

    public static final OrdersAddress ordersAddress = OrdersAddress.builder().id(100L)
            .address1("100")
            .address2("")
            .city("Food Forest City")
            .state("FL")
            .province("")
            .postalCode("33000")
            .country("US").build();

    public static final OrdersAccount ordersAccount = OrdersAccount.builder()
            .id(100L)
            .accountRefId(expectedAccountIdRef)
            .firstName("DukeFirstName")
            .lastName("DukeLastName")
            .emailAddress("dukefirst.last@enjoy.com")
            .addresses(
                    Set.of(ordersAddress)).build();

    public static final OrderLineItems firstProduct = OrderLineItems.builder()
            .id(3L)
            .orderId(1L)
            .quantity(2)
            .price(new BigDecimal("10"))
            .totalPrice(new BigDecimal("10"))
            .productId(1L)
            .build();

    public static final OrderLineItems secondProduct = OrderLineItems.builder()
            .id(4L)
            .orderId(1L)
            .quantity(1)
            .price(new BigDecimal("10"))
            .totalPrice(new BigDecimal("10"))
            .productId(1L)
            .build();

    public static final OrderLineItems thirdProduct = OrderLineItems.builder()
            .id(5L)
            .orderId(1L)
            .quantity(1)
            .price(new BigDecimal("13.74"))
            .totalPrice(new BigDecimal("13.74"))
            .productId(5L)
            .build();

}
