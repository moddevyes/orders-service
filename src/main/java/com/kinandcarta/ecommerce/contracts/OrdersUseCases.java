package com.kinandcarta.ecommerce.contracts;

import com.kinandcarta.ecommerce.entities.OrderLineItems;

import java.util.Set;

public interface OrdersUseCases {
    Set<OrderLineItems> findOrderLineItemsFor(final Long orderId);
}
