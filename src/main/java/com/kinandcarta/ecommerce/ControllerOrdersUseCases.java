package com.kinandcarta.ecommerce;

import com.kinandcarta.ecommerce.entities.OrderLineItems;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface ControllerOrdersUseCases {

    ResponseEntity<Set<OrderLineItems>> findOrderLineItemsFor(final Long orderId);
}
