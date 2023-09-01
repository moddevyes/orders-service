package com.kinandcarta.ecommerce;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@Slf4j
public class OrdersController implements CrudUseCase<Orders>, ControllerOrdersUseCases {
    final OrdersHandler ordersHandler;

    public OrdersController(OrdersHandler ordersHandler) {
        this.ordersHandler = ordersHandler;
    }

    @Override
    @PostMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Orders> create(@RequestBody final Orders model) {
        try {
            return new ResponseEntity<>(ordersHandler.create(model), HttpStatus.OK);
        } catch (final Exception e) {
            log.error("::METHOD, create, exception occured.", e);
            if (e instanceof InvalidAccountException) {
                log.error("InvalidAccountException: valid Account required to create an Order.");
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @PutMapping(value = "/orders/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Orders> update(@PathVariable("id") @NotNull final Long id, @RequestBody Orders model) {
        try {
            return new ResponseEntity<>(ordersHandler.update(id, model), HttpStatus.OK);
        } catch (final Exception e) {
            log.error("::METHOD, update, exception occured.", e);
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/orders/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void delete(@PathVariable("id") @NotNull final Long id) {
        try {
            ordersHandler.delete(id);
        } catch (final Exception e) {
            log.error("::METHOD, delete, exception occured.", e);
        }
    }

    @Override
    @GetMapping(value = "/orders/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Orders> findById(@PathVariable("id") @NotNull final Long id) {
        try {
            return new ResponseEntity<>(ordersHandler.findById(id), HttpStatus.OK);
        } catch (final Exception e) {
            log.error("::METHOD, findById, exception occured.", e);
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @GetMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<Orders>> findAll() {
        try {
            return new ResponseEntity<>(
                    Optional.ofNullable(ordersHandler.findAll()).orElse(new HashSet<>()), HttpStatus.OK);
        } catch (final Exception e) {
            log.error("::METHOD, findAll, exception occured.", e);
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @GetMapping(value = "/orders/{id}/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<OrderLineItems>> findOrderLineItemsFor(@PathVariable("id") @NotNull final Long id) {
        try {
            return new ResponseEntity<>(ordersHandler.findOrderLineItemsFor(id), HttpStatus.OK);
        } catch (final Exception e) {
            log.error("::METHOD, findOrderLineItemsFor, exception occured.", e);
            return ResponseEntity.notFound().build();
        }
    }
}
