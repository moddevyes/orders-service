package com.kinandcarta.ecommerce.contracts;

import com.kinandcarta.ecommerce.entities.OrderLineItems;
import com.kinandcarta.ecommerce.entities.Orders;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

public interface ControllerOrdersUseCases {

    @GetMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Set<Orders>> findAll(@RequestParam(value = "id", required = false) @NotNull Long accountId);

    ResponseEntity<Set<OrderLineItems>> findOrderLineItemsFor(final Long orderId);

}
