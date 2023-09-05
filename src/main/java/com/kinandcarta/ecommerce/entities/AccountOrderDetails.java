package com.kinandcarta.ecommerce.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@RequiredArgsConstructor
@ToString
@Builder
@AllArgsConstructor
@Slf4j
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AccountOrderDetails {
    private Long orderId;
    private String orderNumber;
    private ShippingAddressDTO shippingAddressDTO;
    private BigDecimal totalPrice;
    private Set<OrderLineItems> lineItems;
}
