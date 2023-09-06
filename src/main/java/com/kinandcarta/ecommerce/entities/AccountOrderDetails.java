package com.kinandcarta.ecommerce.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
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
    @JsonProperty(value = "shippingAddress")
    private ShippingAddressDTO shippingAddressDTO;
    private BigDecimal totalPrice;
    private Set<OrderLineItems> lineItems;

    private static final String NO_VALUE = "N/A";
    private static final Long MIN_VALUE = Long.MIN_VALUE;

    public AccountOrderDetails(final Orders ordersSource) {
        Objects.requireNonNull(ordersSource, "An order is required to assemble the Order details view.");

        buildOrderDetails(ordersSource);
    }

    private void buildOrderDetails(final Orders source) {
        this.orderId = Objects.requireNonNullElse(source.getId(), MIN_VALUE);
        this.orderNumber = Objects.requireNonNullElse(source.getOrderNumber(), NO_VALUE);
        if (source.getOrdersShippingAddress() != null) {
            this.shippingAddressDTO = ShippingAddressDTO.builder() // Objects.requiresNotNullWithDefault ...
                    .id(source.getOrdersShippingAddress().getId())
                    .address1(source.getOrdersShippingAddress().getAddress1())
                    .address2(source.getOrdersShippingAddress().getAddress2())
                    .city(source.getOrdersShippingAddress().getCity())
                    .postalCode(source.getOrdersShippingAddress().getPostalCode())
                    .province(source.getOrdersShippingAddress().getProvince())
                    .country(source.getOrdersShippingAddress().getCountry())
                    .build();
        } else {
            log.debug("AccountOrderDetails -- build order details, no shipping address provided.");
        }
        this.totalPrice = Objects.requireNonNullElse(source.getTotalPrice(), BigDecimal.ZERO);
        if (source.getOrderLineItems() != null) {
            this.lineItems = new HashSet<>(source.getOrderLineItems());
        } else {
            log.debug("AccountOrderDetails -- build order details, no order line items provided.");
        }

    }
}
