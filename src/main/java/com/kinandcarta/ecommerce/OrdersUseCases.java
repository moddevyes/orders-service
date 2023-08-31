package com.kinandcarta.ecommerce;

import java.util.Set;

public interface OrdersUseCases {

    Set<OrderLineItems> findOrderLineItemsFor(final Long orderId);
}
