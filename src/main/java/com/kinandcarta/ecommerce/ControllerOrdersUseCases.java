package com.kinandcarta.ecommerce;

import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface ControllerOrdersUseCases {

    ResponseEntity<Set<OrderLineItems>> findOrderLineItemsFor(final Long orderId);
}
