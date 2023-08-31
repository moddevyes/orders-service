package com.kinandcarta.ecommerce;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderLineItemsRepository extends JpaRepository<OrderLineItems, Long> {
    /**
     * findOrderLineItemsByOrderId - find order line item by order id <b>within</b> the order line item
     * @param orderId orderId
     * @return List of order line items
     */
    List<OrderLineItems> findOrderLineItemsByOrderId(final Long orderId);
}
